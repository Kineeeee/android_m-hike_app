package com.example.coursework.data.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages the creation and versioning of the application's SQLite database.
 * This class defines the database schema and handles creation and upgrades.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "mHike.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_HIKES = "hikes";
    public static final String TABLE_OBSERVATIONS = "observations";

    // Hikes Table Columns
    public static final String KEY_HIKE_ID = "id";
    public static final String KEY_HIKE_NAME = "name";
    public static final String KEY_HIKE_LOCATION = "location";
    public static final String KEY_HIKE_DATE = "date";
    public static final String KEY_HIKE_PARKING = "parking_available";
    public static final String KEY_HIKE_LENGTH = "length";
    public static final String KEY_HIKE_DIFFICULTY = "difficulty";
    public static final String KEY_HIKE_DESCRIPTION = "description";
    // Creative fields
    public static final String KEY_HIKE_WEATHER = "weather";
    public static final String KEY_HIKE_RECOMMENDED_GEAR = "recommended_gear";


    // Observations Table Columns
    public static final String KEY_OBSERVATION_ID = "id";
    public static final String KEY_OBSERVATION_HIKE_ID_FK = "hike_id";
    public static final String KEY_OBSERVATION_TEXT = "observation";
    public static final String KEY_OBSERVATION_TIME = "time";
    public static final String KEY_OBSERVATION_COMMENTS = "comments";

    // SQL statement to create the 'hikes' table.
    private static final String CREATE_TABLE_HIKES = "CREATE TABLE " + TABLE_HIKES +
            "(" +
            KEY_HIKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_HIKE_NAME + " TEXT NOT NULL," +
            KEY_HIKE_LOCATION + " TEXT NOT NULL," +
            KEY_HIKE_DATE + " TEXT NOT NULL," +
            KEY_HIKE_PARKING + " TEXT NOT NULL," +
            KEY_HIKE_LENGTH + " REAL NOT NULL," +
            KEY_HIKE_DIFFICULTY + " TEXT NOT NULL," +
            KEY_HIKE_DESCRIPTION + " TEXT," +
            KEY_HIKE_WEATHER + " TEXT," +
            KEY_HIKE_RECOMMENDED_GEAR + " TEXT" +
            ")";

    // SQL statement to create the 'observations' table.
    private static final String CREATE_TABLE_OBSERVATIONS = "CREATE TABLE " + TABLE_OBSERVATIONS +
            "(" +
            KEY_OBSERVATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_OBSERVATION_HIKE_ID_FK + " INTEGER," +
            KEY_OBSERVATION_TEXT + " TEXT NOT NULL," +
            KEY_OBSERVATION_TIME + " TEXT NOT NULL," +
            KEY_OBSERVATION_COMMENTS + " TEXT," +
            "FOREIGN KEY(" + KEY_OBSERVATION_HIKE_ID_FK + ") REFERENCES " + TABLE_HIKES + "(" + KEY_HIKE_ID + ")" +
            ")";

    /**
     * Constructor for the DatabaseHelper.
     *
     * @param context The application context.
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_HIKES);
        db.execSQL(CREATE_TABLE_OBSERVATIONS);
    }

    /**
     * Called when the database needs to be upgraded.
     * This method will drop the existing tables and recreate them.
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest upgrade policy is to drop the old tables and create new ones.
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIKES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBSERVATIONS);
            onCreate(db);
        }
    }
}
