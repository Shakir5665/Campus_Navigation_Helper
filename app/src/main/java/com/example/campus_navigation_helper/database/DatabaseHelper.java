package com.example.campus_navigation_helper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.campus_navigation_helper.models.Location;
import com.example.campus_navigation_helper.models.NavigationStep;

import java.util.ArrayList;
import java.util.List;

/**
 * This class helps manage the SQLite database for the app.
 * It handles creating tables, preloading data, and user authentication.
 * 
 * Beginner Tip: SQL is a language used to talk to databases. SQLite is a
 * lightweight version of SQL that runs directly on your phone!
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Database name and version
    private static final String DATABASE_NAME = "campus_nav.db";
    private static final int DATABASE_VERSION = 3; // Incremented for Master Admin features

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_LOCATIONS = "locations";
    private static final String TABLE_STEPS = "navigation_steps";
    private static final String TABLE_FAVORITES = "favorites";

    // User Table Columns
    private static final String COL_USER_ID = "id";
    private static final String COL_USER_NAME = "name";
    private static final String COL_USER_EMAIL = "email";
    private static final String COL_USER_PASSWORD = "password";
    private static final String COL_USER_ROLE = "role"; // NEW COLUMN
    private static final String COL_USER_ADMIN_SECRET = "admin_secret"; // Admin secret column

    // Location Table Columns
    private static final String COL_LOC_ID = "id";
    private static final String COL_LOC_NAME = "name";
    private static final String COL_LOC_BLOCK = "block";
    private static final String COL_LOC_DESC = "description";

    // Steps Table Columns
    private static final String COL_STEP_ID = "id";
    private static final String COL_STEP_LOC_ID = "location_id";
    private static final String COL_STEP_NUM = "step_number";
    private static final String COL_STEP_DESC = "step_description";

    // Favorites Table Columns
    private static final String COL_FAV_ID = "id";
    private static final String COL_FAV_USER_ID = "user_id";
    private static final String COL_FAV_LOC_ID = "location_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NAME + " TEXT, " +
                COL_USER_EMAIL + " TEXT UNIQUE, " +
                COL_USER_PASSWORD + " TEXT, " +
                COL_USER_ROLE + " TEXT DEFAULT 'user', " +
                COL_USER_ADMIN_SECRET + " TEXT)");

        // Insert Default Master Admin
        ContentValues adminValues = new ContentValues();
        adminValues.put(COL_USER_NAME, "Mohamed Shakir (Admin)");
        adminValues.put(COL_USER_EMAIL, "shakiradmin123@gmail.com");
        adminValues.put(COL_USER_PASSWORD, "123");
        adminValues.put(COL_USER_ROLE, "master_admin");
        db.insert(TABLE_USERS, null, adminValues);

        // Create Locations Table
        db.execSQL("CREATE TABLE " + TABLE_LOCATIONS + " (" +
                COL_LOC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_LOC_NAME + " TEXT, " +
                COL_LOC_BLOCK + " TEXT, " +
                COL_LOC_DESC + " TEXT)");

        // Create Steps Table
        db.execSQL("CREATE TABLE " + TABLE_STEPS + " (" +
                COL_STEP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_STEP_LOC_ID + " INTEGER, " +
                COL_STEP_NUM + " INTEGER, " +
                COL_STEP_DESC + " TEXT, " +
                "FOREIGN KEY(" + COL_STEP_LOC_ID + ") REFERENCES " + TABLE_LOCATIONS + "(" + COL_LOC_ID + "))");

        // Create Favorites Table
        db.execSQL("CREATE TABLE " + TABLE_FAVORITES + " (" +
                COL_FAV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FAV_USER_ID + " INTEGER, " +
                COL_FAV_LOC_ID + " INTEGER, " +
                "FOREIGN KEY(" + COL_FAV_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "), " +
                "FOREIGN KEY(" + COL_FAV_LOC_ID + ") REFERENCES " + TABLE_LOCATIONS + "(" + COL_LOC_ID + "))");

        preloadData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STEPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        onCreate(db);
    }

    // --- Authentication Methods ---

    public boolean registerUser(String name, String email, String password, String role) {
        return registerUser(name, email, password, role, null);
    }

    public boolean registerUser(String name, String email, String password, String role, String adminSecret) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, name);
        values.put(COL_USER_EMAIL, email);
        values.put(COL_USER_PASSWORD, password);
        values.put(COL_USER_ROLE, role != null ? role : "user");
        if (adminSecret != null) {
            values.put(COL_USER_ADMIN_SECRET, adminSecret);
        }
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public Cursor loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USER_EMAIL + "=? AND " + COL_USER_PASSWORD + "=?",
                new String[] { email, password });
    }

    // --- Location Methods ---

    public Cursor getAllLocations() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_LOCATIONS, null);
    }

    public Cursor searchLocations(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_LOCATIONS + " WHERE " + COL_LOC_NAME + " LIKE ?",
                new String[] { "%" + query + "%" });
    }

    public Cursor getNavigationSteps(int locationId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_STEPS + " WHERE " + COL_STEP_LOC_ID + "=? ORDER BY " + COL_STEP_NUM + " ASC",
                new String[] { String.valueOf(locationId) });
    }

    // --- User Management Methods (Admin) ---

    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " ORDER BY " + COL_USER_ID + " DESC", null);
    }

    public boolean deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Security Check: Prevent deleting master_admin or admins normally
        Cursor cursor = db.rawQuery("SELECT " + COL_USER_ROLE + " FROM " + TABLE_USERS + " WHERE " + COL_USER_ID + "=?",
                new String[] { String.valueOf(userId) });
        if (cursor != null && cursor.moveToFirst()) {
            int roleIndex = cursor.getColumnIndex(COL_USER_ROLE);
            String role = (roleIndex != -1) ? cursor.getString(roleIndex) : "user";
            cursor.close();

            if ("master_admin".equals(role) || "admin".equals(role)) {
                return false;
            }
        } else {
            if (cursor != null)
                cursor.close();
            return false;
        }

        // Maintain referential integrity: remove favorites for this user first
        db.delete(TABLE_FAVORITES, COL_FAV_USER_ID + "=?", new String[] { String.valueOf(userId) });

        // Delete user
        return db.delete(TABLE_USERS, COL_USER_ID + "=?", new String[] { String.valueOf(userId) }) > 0;
    }

    public boolean deleteAdmin(int adminId, String secretCode) {
        SQLiteDatabase db = this.getWritableDatabase();

        // 1. Verify admin_secret matches
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_USER_ROLE + ", " + COL_USER_ADMIN_SECRET + " FROM " + TABLE_USERS + " WHERE "
                        + COL_USER_ID + "=?",
                new String[] { String.valueOf(adminId) });
        if (cursor != null && cursor.moveToFirst()) {
            int roleIndex = cursor.getColumnIndex(COL_USER_ROLE);
            int secretIndex = cursor.getColumnIndex(COL_USER_ADMIN_SECRET);
            String role = (roleIndex != -1) ? cursor.getString(roleIndex) : "";
            String secret = (secretIndex != -1) ? cursor.getString(secretIndex) : "";
            cursor.close();

            if ("master_admin".equals(role)) {
                return false;
            } // Cannot delete master admin
            if (!"admin".equals(role)) {
                return false;
            } // Must be admin
            if (secret == null || !secret.equals(secretCode)) {
                return false;
            } // Secret mismatch
        } else {
            if (cursor != null)
                cursor.close();
            return false;
        }

        db.delete(TABLE_FAVORITES, COL_FAV_USER_ID + "=?", new String[] { String.valueOf(adminId) });
        return db.delete(TABLE_USERS, COL_USER_ID + "=?", new String[] { String.valueOf(adminId) }) > 0;
    }

    public boolean resetPassword(int userId, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_PASSWORD, newPassword);
        return db.update(TABLE_USERS, values, COL_USER_ID + "=?", new String[] { String.valueOf(userId) }) > 0;
    }

    public boolean updateUserRole(int userId, String newRole) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_ROLE, newRole);
        return db.update(TABLE_USERS, values, COL_USER_ID + "=?", new String[] { String.valueOf(userId) }) > 0;
    }

    // Admin Operations
    public long addLocation(String name, String block, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LOC_NAME, name);
        values.put(COL_LOC_BLOCK, block);
        values.put(COL_LOC_DESC, description);
        return db.insert(TABLE_LOCATIONS, null, values);
    }

    public boolean updateLocation(int id, String name, String block, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LOC_NAME, name);
        values.put(COL_LOC_BLOCK, block);
        values.put(COL_LOC_DESC, description);
        return db.update(TABLE_LOCATIONS, values, COL_LOC_ID + "=?", new String[] { String.valueOf(id) }) > 0;
    }

    public boolean deleteLocation(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Also delete related steps and favorites
        db.delete(TABLE_STEPS, COL_STEP_LOC_ID + "=?", new String[] { String.valueOf(id) });
        db.delete(TABLE_FAVORITES, COL_FAV_LOC_ID + "=?", new String[] { String.valueOf(id) });
        return db.delete(TABLE_LOCATIONS, COL_LOC_ID + "=?", new String[] { String.valueOf(id) }) > 0;
    }

    public void addNavigationStep(long locationId, int stepNum, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_STEP_LOC_ID, locationId);
        values.put(COL_STEP_NUM, stepNum);
        values.put(COL_STEP_DESC, description);
        db.insert(TABLE_STEPS, null, values);
    }

    public void deleteNavigationStepsForLocation(int locationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STEPS, COL_STEP_LOC_ID + "=?", new String[] { String.valueOf(locationId) });
    }

    // --- Favorites Methods ---

    public boolean addFavorite(int userId, int locationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FAV_USER_ID, userId);
        values.put(COL_FAV_LOC_ID, locationId);
        long result = db.insert(TABLE_FAVORITES, null, values);
        return result != -1;
    }

    public boolean removeFavorite(int userId, int locationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_FAVORITES, COL_FAV_USER_ID + "=? AND " + COL_FAV_LOC_ID + "=?",
                new String[] { String.valueOf(userId), String.valueOf(locationId) }) > 0;
    }

    public boolean isFavorite(int userId, int locationId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_FAVORITES + " WHERE " + COL_FAV_USER_ID + "=? AND " + COL_FAV_LOC_ID + "=?",
                new String[] { String.valueOf(userId), String.valueOf(locationId) });
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public Cursor getFavoriteLocations(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT l.* FROM " + TABLE_LOCATIONS + " l INNER JOIN " + TABLE_FAVORITES + " f ON l." + COL_LOC_ID
                        + " = f." + COL_FAV_LOC_ID + " WHERE f." + COL_FAV_USER_ID + "=?",
                new String[] { String.valueOf(userId) });
    }

    private void preloadData(SQLiteDatabase db) {
        String[][] locations = {
                { "Main Library", "Block A", "The central library with vast resources." },
                { "Faculty of Science", "Block B", "Main building for science departments." },
                { "Science Auditorium", "Block B", "Large hall for lectures and events." },
                { "Playground", "South Area", "Main outdoor sports area." },
                { "Faculty of Commerce", "Block C", "Home to business and finance studies." },
                { "Medical Centre", "Health Zone", "Primary healthcare for students and staff." },
                { "Administrative Building", "Block D", "Main office for university administration." },
                { "Examination Division", "Block D", "Center for handles all exam matters." },
                { "Faculty of Humanities", "Block E", "Departments of literature and language." },
                { "Lecture Halls", "General Area", "Common area for large classes." },
                { "Faculty of Social Sciences", "Block F", "Departments of sociology and psychology." },
                { "ICT Centre", "Block G", "Main computer labs and IT services." },
                { "Convocation Hall", "Main Plaza", "Venue for graduation ceremonies." },
                { "Faculty of Arts", "Block H", "Creative arts and design studios." },
                { "Male Hostel", "North Wing", "Residential hall for male students." },
                { "Female Hostel", "West Wing", "Residential hall for female students." },
                { "Students’ Centre", "Central Hub", "Student union and recreational area." },
                { "Gymnasium", "Sports Complex", "Indoor fitness and sports facility." },
                { "Canteen", "Dining Area", "Main cafeteria for meals and snacks." },
                { "Main Playground", "East Area", "Large field for major sports events." }
        };

        for (int i = 0; i < locations.length; i++) {
            ContentValues values = new ContentValues();
            values.put(COL_LOC_NAME, locations[i][0]);
            values.put(COL_LOC_BLOCK, locations[i][1]);
            values.put(COL_LOC_DESC, locations[i][2]);
            long locId = db.insert(TABLE_LOCATIONS, null, values);

            // Add sample steps for each location
            addSampleSteps(db, locId, locations[i][0]);
        }
    }

    private void addSampleSteps(SQLiteDatabase db, long locId, String locName) {
        String[] steps = {
                "Enter through the main campus gate and proceed straight.",
                "Turn left at the Roundabout near the Clock Tower.",
                "Continue for 200 meters until you see the " + locName + " sign on your right.",
                "Enter the building and follow the signs to the main reception."
        };

        for (int i = 0; i < steps.length; i++) {
            ContentValues values = new ContentValues();
            values.put(COL_STEP_LOC_ID, locId);
            values.put(COL_STEP_NUM, i + 1);
            values.put(COL_STEP_DESC, steps[i]);
            db.insert(TABLE_STEPS, null, values);
        }
    }
}
