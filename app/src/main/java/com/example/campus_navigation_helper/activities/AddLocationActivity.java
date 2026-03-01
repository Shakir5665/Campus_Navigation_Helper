package com.example.campus_navigation_helper.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_navigation_helper.R;
import com.example.campus_navigation_helper.adapters.EditStepAdapter;
import com.example.campus_navigation_helper.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class AddLocationActivity extends AppCompatActivity {

    private EditText etName, etBlock, etDesc;
    private Button btnAddStep, btnSaveLocation;
    private RecyclerView rvEditSteps;
    private TextView tvTitle;

    private EditStepAdapter stepAdapter;
    private List<String> stepsList;
    private DatabaseHelper dbHelper;

    private boolean isEditing = false;
    private int locationId = -1;

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

        setContentView(R.layout.activity_add_location);

        dbHelper = new DatabaseHelper(this);

        tvTitle = findViewById(R.id.tvAddLocationTitle);
        etName = findViewById(R.id.etLocName);
        etBlock = findViewById(R.id.etLocBlock);
        etDesc = findViewById(R.id.etLocDesc);
        btnAddStep = findViewById(R.id.btnAddStep);
        btnSaveLocation = findViewById(R.id.btnSaveLocation);
        rvEditSteps = findViewById(R.id.rvEditSteps);

        stepsList = new ArrayList<>();

        rvEditSteps.setLayoutManager(new LinearLayoutManager(this));
        stepAdapter = new EditStepAdapter(this, stepsList);
        rvEditSteps.setAdapter(stepAdapter);

        if (getIntent().hasExtra("isEditing")) {
            isEditing = getIntent().getBooleanExtra("isEditing", false);
            locationId = getIntent().getIntExtra("locationId", -1);

            tvTitle.setText("Edit Location");
            etName.setText(getIntent().getStringExtra("name"));
            etBlock.setText(getIntent().getStringExtra("block"));
            etDesc.setText(getIntent().getStringExtra("description"));

            loadSteps(locationId);
            btnSaveLocation.setText("Update Location");
        } else {
            stepsList.add("");
            stepAdapter.notifyItemInserted(0);
        }

        btnAddStep.setOnClickListener(v -> {
            stepsList.add("");
            stepAdapter.notifyItemInserted(stepsList.size() - 1);
            rvEditSteps.scrollToPosition(stepsList.size() - 1);
        });

        btnSaveLocation.setOnClickListener(v -> saveLocation());
    }

    private void loadSteps(int locId) {
        stepsList.clear();
        Cursor cursor = dbHelper.getNavigationSteps(locId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int descIndex = cursor.getColumnIndex("step_description");
                if (descIndex != -1) {
                    stepsList.add(cursor.getString(descIndex));
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        stepAdapter.notifyDataSetChanged();
    }

    private void saveLocation() {
        String name = etName.getText().toString().trim();
        String block = etBlock.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();

        if (name.isEmpty() || block.isEmpty()) {
            Toast.makeText(this, "Name and Block are required", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> validSteps = new ArrayList<>();
        for (String step : stepsList) {
            String s = step.trim();
            if (!s.isEmpty()) {
                validSteps.add(s);
            }
        }

        if (validSteps.isEmpty()) {
            Toast.makeText(this, "Please add at least one valid step", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditing) {
            dbHelper.updateLocation(locationId, name, block, desc);
            dbHelper.deleteNavigationStepsForLocation(locationId);
            for (int i = 0; i < validSteps.size(); i++) {
                dbHelper.addNavigationStep(locationId, i + 1, validSteps.get(i));
            }
            Toast.makeText(this, "Location updated", Toast.LENGTH_SHORT).show();
        } else {
            long newLocId = dbHelper.addLocation(name, block, desc);
            if (newLocId != -1) {
                for (int i = 0; i < validSteps.size(); i++) {
                    dbHelper.addNavigationStep((int) newLocId, i + 1, validSteps.get(i));
                }
                Toast.makeText(this, "Location added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error adding location", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        finish();
    }
}
