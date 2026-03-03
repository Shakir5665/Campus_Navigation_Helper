package com.example.campus_navigation_helper.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.EditText;
import android.text.InputType;

import com.example.campus_navigation_helper.R;
import com.example.campus_navigation_helper.database.DatabaseHelper;
import com.example.campus_navigation_helper.models.User;

import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;
    private DatabaseHelper dbHelper;
    private String currentUserRole;

    public AdminUserAdapter(Context context, List<User> userList, DatabaseHelper dbHelper) {
        this.context = context;
        this.userList = userList;
        this.dbHelper = dbHelper;

        android.content.SharedPreferences pref = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        this.currentUserRole = pref.getString("userRole", "user");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvName.setText(user.getName());
        holder.tvEmail.setText(user.getEmail());
        holder.tvRole.setText("Role: " + user.getRole());

        holder.ivDelete.setOnClickListener(v -> {
            if ("master_admin".equals(user.getRole())) {
                Toast.makeText(context, "Cannot delete Master Admin!", Toast.LENGTH_SHORT).show();
                return;
            }

            if ("admin".equals(user.getRole())) {
                if (!"master_admin".equals(currentUserRole)) {
                    Toast.makeText(context, "Only Master Admin can delete Admins!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Master Admin deleting an Admin - require secret
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Verify Admin Secret");
                builder.setMessage("Enter the secret code for admin '" + user.getName() + "' to confirm deletion:");

                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);

                builder.setPositiveButton("Verify & Delete", (dialog, which) -> {
                    String secret = input.getText().toString().trim();
                    boolean success = dbHelper.deleteAdmin(user.getId(), secret);
                    if (success) {
                        userList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, userList.size());
                        Toast.makeText(context, "Admin deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to delete Admin (Incorrect Secret?)", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                builder.show();

            } else {
                // Anyone (Admin/Master Admin) deleting a normal User
                new AlertDialog.Builder(context)
                        .setTitle("Delete User")
                        .setMessage("Are you sure you want to delete user '" + user.getName()
                                + "'? All favorites related to this user will be permanently removed.")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            boolean success = dbHelper.deleteUser(user.getId());
                            if (success) {
                                userList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, userList.size());
                                Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to delete user", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvRole;
        ImageView ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAdminUserName);
            tvEmail = itemView.findViewById(R.id.tvAdminUserEmail);
            tvRole = itemView.findViewById(R.id.tvAdminUserRole);
            ivDelete = itemView.findViewById(R.id.ivDeleteUser);
        }
    }
}
