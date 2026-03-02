package com.example.campus_navigation_helper.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_navigation_helper.R;
import com.example.campus_navigation_helper.adapters.LocationAdapter;
import com.example.campus_navigation_helper.database.DatabaseHelper;
import com.example.campus_navigation_helper.models.Location;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView rvFavs;
    private TextView tvEmpty;
    private LocationAdapter adapter;
    private List<Location> favList;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        dbHelper = new DatabaseHelper(this);
        SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = pref.getInt("userId", -1);

        rvFavs = findViewById(R.id.rvFavs);
        tvEmpty = findViewById(R.id.tvEmptyFavs);

        favList = new ArrayList<>();
        loadFavorites();

        adapter = new LocationAdapter(favList, location -> {
            Intent intent = new Intent(FavoritesActivity.this, LocationDetailsActivity.class);
            intent.putExtra("locId", location.getId());
            intent.putExtra("locName", location.getName());
            intent.putExtra("locBlock", location.getBlock());
            intent.putExtra("locDesc", location.getShortDescription());
            startActivity(intent);
        });

        rvFavs.setLayoutManager(new LinearLayoutManager(this));
        rvFavs.setAdapter(adapter);

        checkEmptyState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
        adapter.notifyDataSetChanged();
        checkEmptyState();
    }

    private void loadFavorites() {
        favList.clear();
        Cursor cursor = dbHelper.getFavoriteLocations(userId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idIdx = cursor.getColumnIndex("id");
                int nameIdx = cursor.getColumnIndex("name");
                int blockIdx = cursor.getColumnIndex("block");
                int descIdx = cursor.getColumnIndex("description");

                if (idIdx != -1 && nameIdx != -1 && blockIdx != -1 && descIdx != -1) {
                    favList.add(new Location(
                            cursor.getInt(idIdx),
                            cursor.getString(nameIdx),
                            cursor.getString(blockIdx),
                            cursor.getString(descIdx)));
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void checkEmptyState() {
        if (favList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvFavs.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvFavs.setVisibility(View.VISIBLE);
        }
    }
}
