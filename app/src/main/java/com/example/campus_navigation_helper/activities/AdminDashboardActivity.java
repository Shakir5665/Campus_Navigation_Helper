package com.example.campus_navigation_helper.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campus_navigation_helper.R;

public class AdminDashboardActivity extends AppCompatActivity {

    private Button btnAddLocation, btnManageLocations, btnManageUsers, btnLogout;
    private TextView tvAdminGreeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
        String role = pref.getString("userRole", "user");
        if (!"admin".equals(role)) {
            android.widget.Toast.makeText(this, "Unauthorized access. Admins only.", android.widget.Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        setContentView(R.layout.activity_admin_dashboard);

        tvAdminGreeting = findViewById(R.id.tvAdminGreeting);
        btnAddLocation = findViewById(R.id.btnAddLocation);
        btnManageLocations = findViewById(R.id.btnManageLocations);
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnLogout = findViewById(R.id.btnLogout);

        String userName = pref.getString("userName", "Admin");
        tvAdminGreeting.setText("Welcome Admin, " + userName);

        btnAddLocation.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AddLocationActivity.class));
        });

        btnManageLocations.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, ManageLocationsActivity.class));
        });

        btnManageUsers.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, ManageUsersActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(AdminDashboardActivity.this, LoginActivity.class));
            finishAffinity();
        });
    }
}
