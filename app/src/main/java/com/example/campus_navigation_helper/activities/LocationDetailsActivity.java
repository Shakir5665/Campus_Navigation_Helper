package com.example.campus_navigation_helper.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_navigation_helper.R;
import com.example.campus_navigation_helper.adapters.NavigationStepAdapter;
import com.example.campus_navigation_helper.database.DatabaseHelper;
import com.example.campus_navigation_helper.models.NavigationStep;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class LocationDetailsActivity extends AppCompatActivity {

    private int locId, userId;
    private String locName, locBlock, locDesc;
    private TextView tvName, tvBlock, tvDesc;
    private RecyclerView rvSteps;
    private MaterialButton btnFav;
    private ImageButton btnBack;
    private DatabaseHelper dbHelper;
    private List<NavigationStep> stepList;
    private NavigationStepAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);

        dbHelper = new DatabaseHelper(this);

        // Get user session
        SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = pref.getInt("userId", -1);

        // Get intent data
        locId = getIntent().getIntExtra("locId", -1);
        locName = getIntent().getStringExtra("locName");
        locBlock = getIntent().getStringExtra("locBlock");
        locDesc = getIntent().getStringExtra("locDesc");

        tvName = findViewById(R.id.tvDetailName);
        tvBlock = findViewById(R.id.tvDetailBlock);
        tvDesc = findViewById(R.id.tvDetailDesc);
        rvSteps = findViewById(R.id.rvSteps);
        btnFav = findViewById(R.id.btnFavAction);
        btnBack = findViewById(R.id.btnBack);

        tvName.setText(locName);
        tvBlock.setText(locBlock);
        tvDesc.setText(locDesc);

        stepList = new ArrayList<>();
        loadSteps();

        adapter = new NavigationStepAdapter(stepList);
        rvSteps.setAdapter(adapter);

        updateFavButton();

        btnFav.setOnClickListener(v -> toggleFavorite());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadSteps() {
        Cursor cursor = dbHelper.getNavigationSteps(locId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idIdx = cursor.getColumnIndex("id");
                int locIdIdx = cursor.getColumnIndex("location_id");
                int stepNumIdx = cursor.getColumnIndex("step_number");
                int stepDescIdx = cursor.getColumnIndex("step_description");

                if (idIdx != -1 && locIdIdx != -1 && stepNumIdx != -1 && stepDescIdx != -1) {
                    stepList.add(new NavigationStep(
                            cursor.getInt(idIdx),
                            cursor.getInt(locIdIdx),
                            cursor.getInt(stepNumIdx),
                            cursor.getString(stepDescIdx)));
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void updateFavButton() {
        if (dbHelper.isFavorite(userId, locId)) {
            btnFav.setText("Remove from Favorites");
            btnFav.setIconResource(0); // Optional: change icon
        } else {
            btnFav.setText("Add to Favorites");
            btnFav.setIconResource(R.drawable.ic_favorite);
        }
    }

    private void toggleFavorite() {
        if (dbHelper.isFavorite(userId, locId)) {
            if (dbHelper.removeFavorite(userId, locId)) {
                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (dbHelper.addFavorite(userId, locId)) {
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
            }
        }
        updateFavButton();
    }
}
