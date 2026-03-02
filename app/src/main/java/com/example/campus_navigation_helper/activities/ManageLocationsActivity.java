package com.example.campus_navigation_helper.activities;

import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_navigation_helper.R;
import com.example.campus_navigation_helper.adapters.AdminLocationAdapter;
import com.example.campus_navigation_helper.database.DatabaseHelper;
import com.example.campus_navigation_helper.models.Location;

import java.util.ArrayList;
import java.util.List;

public class ManageLocationsActivity extends AppCompatActivity {

    private RecyclerView rvAdminLocations;
    private AdminLocationAdapter adapter;
    private List<Location> locationList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        android.content.SharedPreferences pref = getSharedPreferences("UserSession",
                android.content.Context.MODE_PRIVATE);
        String role = pref.getString("userRole", "user");
        if (!"admin".equals(role)) {
            android.widget.Toast.makeText(this, "Unauthorized access. Admins only.", android.widget.Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        setContentView(R.layout.activity_manage_locations);

        rvAdminLocations = findViewById(R.id.rvAdminLocations);
        rvAdminLocations.setLayoutManager(new LinearLayoutManager(this));

        locationList = new ArrayList<>();
        dbHelper = new DatabaseHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLocations();
    }

    private void loadLocations() {
        locationList.clear();
        Cursor cursor = dbHelper.getAllLocations();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("id");
                int nameIndex = cursor.getColumnIndex("name");
                int blockIndex = cursor.getColumnIndex("block");
                int descIndex = cursor.getColumnIndex("description");

                int id = idIndex != -1 ? cursor.getInt(idIndex) : -1;
                String name = nameIndex != -1 ? cursor.getString(nameIndex) : "";
                String block = blockIndex != -1 ? cursor.getString(blockIndex) : "";
                String desc = descIndex != -1 ? cursor.getString(descIndex) : "";

                locationList.add(new Location(id, name, block, desc));
            } while (cursor.moveToNext());
            cursor.close();
        }

        adapter = new AdminLocationAdapter(this, locationList, dbHelper);
        rvAdminLocations.setAdapter(adapter);
    }
}
