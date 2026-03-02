package com.example.campus_navigation_helper.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.campus_navigation_helper.R;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private CardView cardLocations, cardFavorites, cardLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvWelcome = findViewById(R.id.tvWelcome);
        cardLocations = findViewById(R.id.cardViewLocations);
        cardFavorites = findViewById(R.id.cardViewFavorites);
        cardLogout = findViewById(R.id.cardViewLogout);

        // Get user info from session
        SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
        String name = pref.getString("userName", "User");
        tvWelcome.setText("Welcome,\n" + name);

        cardLocations.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, LocationListActivity.class));
        });

        cardFavorites.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, FavoritesActivity.class));
        });

        cardLogout.setOnClickListener(v -> {
            logout();
        });
    }

    private void logout() {
        SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
