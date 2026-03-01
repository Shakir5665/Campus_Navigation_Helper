package com.example.campus_navigation_helper.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campus_navigation_helper.R;
import com.example.campus_navigation_helper.database.DatabaseHelper;

public class CreateAdminActivity extends AppCompatActivity {

    private EditText etAdminName, etAdminEmail, etAdminPassword, etAdminSecret;
    private Button btnSubmit;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        android.content.SharedPreferences pref = getSharedPreferences("UserSession",
                android.content.Context.MODE_PRIVATE);
        String role = pref.getString("userRole", "user");
        if (!"master_admin".equals(role)) {
            Toast.makeText(this, "Unauthorized access. Master Admins only.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_create_admin);

        dbHelper = new DatabaseHelper(this);
        etAdminName = findViewById(R.id.etAdminName);
        etAdminEmail = findViewById(R.id.etAdminEmail);
        etAdminPassword = findViewById(R.id.etAdminPassword);
        etAdminSecret = findViewById(R.id.etAdminSecret);
        btnSubmit = findViewById(R.id.btnCreateAdminSubmit);

        btnSubmit.setOnClickListener(v -> createAdmin());
    }

    private void createAdmin() {
        String name = etAdminName.getText().toString().trim();
        String email = etAdminEmail.getText().toString().trim();
        String pwd = etAdminPassword.getText().toString().trim();
        String secret = etAdminSecret.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || pwd.isEmpty() || secret.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = dbHelper.registerUser(name, email, pwd, "admin", secret);
        if (success) {
            Toast.makeText(this, "Admin created successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error creating admin (Email may exist).", Toast.LENGTH_SHORT).show();
        }
    }
}
