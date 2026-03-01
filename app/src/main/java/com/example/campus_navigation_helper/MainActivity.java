package com.example.campus_navigation_helper;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use SharedPreferences to check if user is logged in
        android.content.SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Already logged in, go to Dashboard
            startActivity(new android.content.Intent(MainActivity.this,
                    com.example.campus_navigation_helper.activities.DashboardActivity.class));
        } else {
            // Not logged in, go to Login
            startActivity(new android.content.Intent(MainActivity.this,
                    com.example.campus_navigation_helper.activities.LoginActivity.class));
        }
        finish(); // Close MainActivity so user can't go back to it
    }
}