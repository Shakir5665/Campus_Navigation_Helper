package com.example.campus_navigation_helper.activities;

import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_navigation_helper.R;
import com.example.campus_navigation_helper.adapters.AdminUserAdapter;
import com.example.campus_navigation_helper.database.DatabaseHelper;
import com.example.campus_navigation_helper.models.User;

import java.util.ArrayList;
import java.util.List;

public class ManageUsersActivity extends AppCompatActivity {

    private RecyclerView rvAdminUsers;
    private AdminUserAdapter adapter;
    private List<User> userList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        android.content.SharedPreferences pref = getSharedPreferences("UserSession",
                android.content.Context.MODE_PRIVATE);
        String role = pref.getString("userRole", "user");
        if (!"admin".equals(role) && !"master_admin".equals(role)) {
            android.widget.Toast.makeText(this, "Unauthorized access. Admins only.", android.widget.Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        setContentView(R.layout.activity_manage_users);

        rvAdminUsers = findViewById(R.id.rvAdminUsers);
        rvAdminUsers.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        dbHelper = new DatabaseHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }

    private void loadUsers() {
        userList.clear();
        Cursor cursor = dbHelper.getAllUsers();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("id");
                int nameIndex = cursor.getColumnIndex("name");
                int emailIndex = cursor.getColumnIndex("email");
                int roleIndex = cursor.getColumnIndex("role");

                int id = idIndex != -1 ? cursor.getInt(idIndex) : -1;
                String name = nameIndex != -1 ? cursor.getString(nameIndex) : "";
                String email = emailIndex != -1 ? cursor.getString(emailIndex) : "";
                String uRole = roleIndex != -1 ? cursor.getString(roleIndex) : "user";

                // Expose user to the list
                userList.add(new User(id, name, email, "", uRole));
            } while (cursor.moveToNext());
            cursor.close();
        }

        adapter = new AdminUserAdapter(this, userList, dbHelper);
        rvAdminUsers.setAdapter(adapter);
    }
}
