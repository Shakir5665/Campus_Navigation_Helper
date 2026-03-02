package com.example.campus_navigation_helper.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campus_navigation_helper.R;

public class MasterAdminDashboardActivity extends AppCompatActivity {

    private Button btnCreateAdmin, btnManageUsers, btnLogout;
    private TextView tvMasterAdminGreeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
        String role = pref.getString("userRole", "user");
        if (!"master_admin".equals(role)) {
            android.widget.Toast
                    .makeText(this, "Unauthorized access. Master Admins only.", android.widget.Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        setContentView(R.layout.activity_master_admin_dashboard);

        tvMasterAdminGreeting = findViewById(R.id.tvMasterAdminGreeting);
        btnCreateAdmin = findViewById(R.id.btnCreateAdmin);
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnLogout = findViewById(R.id.btnLogout);

        String userName = pref.getString("userName", "Master");
        tvMasterAdminGreeting.setText("Welcome Master Admin, " + userName);

        btnCreateAdmin.setOnClickListener(v -> {
            startActivity(new Intent(MasterAdminDashboardActivity.this, CreateAdminActivity.class));
        });

        btnManageUsers.setOnClickListener(v -> {
            startActivity(new Intent(MasterAdminDashboardActivity.this, ManageUsersActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(MasterAdminDashboardActivity.this, LoginActivity.class));
            finishAffinity();
        });
    }
}
