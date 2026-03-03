package com.example.campus_navigation_helper.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_navigation_helper.R;
import com.example.campus_navigation_helper.activities.AddLocationActivity;
import com.example.campus_navigation_helper.database.DatabaseHelper;
import com.example.campus_navigation_helper.models.Location;

import java.util.List;

public class AdminLocationAdapter extends RecyclerView.Adapter<AdminLocationAdapter.ViewHolder> {

    private Context context;
    private List<Location> locationList;
    private DatabaseHelper dbHelper;

    public AdminLocationAdapter(Context context, List<Location> locationList, DatabaseHelper dbHelper) {
        this.context = context;
        this.locationList = locationList;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Location location = locationList.get(position);
        holder.tvName.setText(location.getName());
        holder.tvBlock.setText(location.getBlock());

        holder.ivEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddLocationActivity.class);
            intent.putExtra("isEditing", true);
            intent.putExtra("locationId", location.getId());
            intent.putExtra("name", location.getName());
            intent.putExtra("block", location.getBlock());
            intent.putExtra("description", location.getShortDescription());
            context.startActivity(intent);
        });

        holder.ivDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Location")
                    .setMessage(
                            "Are you sure you want to delete this location? All its navigation steps and favorites will be removed.")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dbHelper.deleteLocation(location.getId());
                        locationList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, locationList.size());
                        Toast.makeText(context, "Location deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvBlock;
        ImageView ivEdit, ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAdminLocName);
            tvBlock = itemView.findViewById(R.id.tvAdminLocBlock);
            ivEdit = itemView.findViewById(R.id.ivEditLocation);
            ivDelete = itemView.findViewById(R.id.ivDeleteLocation);
        }
    }
}
