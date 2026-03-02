package com.example.campus_navigation_helper.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campus_navigation_helper.R;
import com.example.campus_navigation_helper.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegisterLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = dbHelper.loginUser(email, password);
        if (cursor != null && cursor.moveToFirst()) {
            // Login successful
            int idIndex = cursor.getColumnIndex("id");
            int nameIndex = cursor.getColumnIndex("name");
            int roleIndex = cursor.getColumnIndex("role");

            int userId = (idIndex != -1) ? cursor.getInt(idIndex) : -1;
            String userName = (nameIndex != -1) ? cursor.getString(nameIndex) : "User";
            String userRole = (roleIndex != -1) ? cursor.getString(roleIndex) : "user";

            // Save to SharedPreferences
            SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putInt("userId", userId);
            editor.putString("userName", userName);
            editor.putString("userRole", userRole);
            editor.apply();

            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
            if ("master_admin".equals(userRole)) {
                startActivity(new Intent(LoginActivity.this, MasterAdminDashboardActivity.class));
            } else if ("admin".equals(userRole)) {
                startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
            } else {
                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            }
            finish();
            cursor.close();
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}
