package com.example.campus_navigation_helper.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_navigation_helper.R;
import com.example.campus_navigation_helper.adapters.LocationAdapter;
import com.example.campus_navigation_helper.database.DatabaseHelper;
import com.example.campus_navigation_helper.models.Location;

import java.util.ArrayList;
import java.util.List;

public class LocationListActivity extends AppCompatActivity {

    private RecyclerView rvLocations;
    private LocationAdapter adapter;
    private List<Location> locationList;
    private DatabaseHelper dbHelper;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        dbHelper = new DatabaseHelper(this);
        rvLocations = findViewById(R.id.rvLocations);
        etSearch = findViewById(R.id.etSearch);

        locationList = new ArrayList<>();
        loadLocations();

        adapter = new LocationAdapter(locationList, location -> {
            Intent intent = new Intent(LocationListActivity.this, LocationDetailsActivity.class);
            intent.putExtra("locId", location.getId());
            intent.putExtra("locName", location.getName());
            intent.putExtra("locBlock", location.getBlock());
            intent.putExtra("locDesc", location.getShortDescription());
            startActivity(intent);
        });

        rvLocations.setLayoutManager(new LinearLayoutManager(this));
        rvLocations.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadLocations() {
        Cursor cursor = dbHelper.getAllLocations();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idIdx = cursor.getColumnIndex("id");
                int nameIdx = cursor.getColumnIndex("name");
                int blockIdx = cursor.getColumnIndex("block");
                int descIdx = cursor.getColumnIndex("description");

                if (idIdx != -1 && nameIdx != -1 && blockIdx != -1 && descIdx != -1) {
                    locationList.add(new Location(
                            cursor.getInt(idIdx),
                            cursor.getString(nameIdx),
                            cursor.getString(blockIdx),
                            cursor.getString(descIdx)));
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
    }
}
