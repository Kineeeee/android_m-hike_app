package com.example.coursework.data.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.coursework.data.models.Hike;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the 'hikes' table.
 * This class provides methods to perform CRUD (Create, Read, Update, Delete) operations on the hikes table.
 */
public class HikeDAO {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    /**
     * Constructor for HikeDAO.
     * @param context The application context.
     */
    public HikeDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Opens the database for writing.
     */
    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Closes the database connection.
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * Adds a new hike to the database.
     * @param hike The Hike object to add.
     * @return The ID of the newly inserted hike, or -1 if an error occurred.
     */
    public long addHike(Hike hike) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_HIKE_NAME, hike.getName());
        values.put(DatabaseHelper.KEY_HIKE_LOCATION, hike.getLocation());
        values.put(DatabaseHelper.KEY_HIKE_DATE, hike.getDate());
        values.put(DatabaseHelper.KEY_HIKE_PARKING, hike.getParkingAvailable());
        values.put(DatabaseHelper.KEY_HIKE_LENGTH, hike.getLength());
        values.put(DatabaseHelper.KEY_HIKE_DIFFICULTY, hike.getDifficulty());
        values.put(DatabaseHelper.KEY_HIKE_DESCRIPTION, hike.getDescription());
        values.put(DatabaseHelper.KEY_HIKE_WEATHER, hike.getWeather());
        values.put(DatabaseHelper.KEY_HIKE_RECOMMENDED_GEAR, hike.getRecommendedGear());

        return database.insert(DatabaseHelper.TABLE_HIKES, null, values);
    }

    /**
     * Retrieves all hikes from the database.
     * @return A list of all Hike objects.
     */
    public List<Hike> getAllHikes() {
        List<Hike> hikes = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_HIKES, null, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Hike hike = cursorToHike(cursor);
                hikes.add(hike);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return hikes;
    }

    /**
     * Retrieves a single hike by its ID.
     * @param hikeId The ID of the hike to retrieve.
     * @return The Hike object if found, otherwise null.
     */
    public Hike getHikeById(long hikeId) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_HIKES,
                null, // all columns
                DatabaseHelper.KEY_HIKE_ID + " = ?", // selection
                new String[]{String.valueOf(hikeId)}, // selection args
                null, // groupBy
                null, // having
                null); // orderBy

        if (cursor != null && cursor.moveToFirst()) {
            Hike hike = cursorToHike(cursor);
            cursor.close();
            return hike;
        }

        if(cursor != null){
            cursor.close();
        }

        return null; // Hike not found
    }

    /**
     * Updates an existing hike in the database.
     * @param hike The Hike object with updated information.
     * @return The number of rows affected.
     */
    public int updateHike(Hike hike) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_HIKE_NAME, hike.getName());
        values.put(DatabaseHelper.KEY_HIKE_LOCATION, hike.getLocation());
        values.put(DatabaseHelper.KEY_HIKE_DATE, hike.getDate());
        values.put(DatabaseHelper.KEY_HIKE_PARKING, hike.getParkingAvailable());
        values.put(DatabaseHelper.KEY_HIKE_LENGTH, hike.getLength());
        values.put(DatabaseHelper.KEY_HIKE_DIFFICULTY, hike.getDifficulty());
        values.put(DatabaseHelper.KEY_HIKE_DESCRIPTION, hike.getDescription());
        values.put(DatabaseHelper.KEY_HIKE_WEATHER, hike.getWeather());
        values.put(DatabaseHelper.KEY_HIKE_RECOMMENDED_GEAR, hike.getRecommendedGear());

        return database.update(DatabaseHelper.TABLE_HIKES, values, DatabaseHelper.KEY_HIKE_ID + " = ?",
                new String[]{String.valueOf(hike.getId())});
    }

    /**
     * Deletes a hike from the database by its ID.
     * @param hikeId The ID of the hike to delete.
     */
    public void deleteHike(long hikeId) {
        database.delete(DatabaseHelper.TABLE_HIKES, DatabaseHelper.KEY_HIKE_ID + " = " + hikeId, null);
    }

    /**
     * Deletes all hikes from the database.
     */
    public void deleteAllHikes() {
        database.delete(DatabaseHelper.TABLE_HIKES, null, null);
    }

    /**
     * Filters hikes based on specified criteria.
     * @param name The name of the hike (can be partial).
     * @param location The location of the hike (can be partial).
     * @param date The date of the hike.
     * @param difficulty The difficulty of the hike.
     * @param minLength The minimum length of the hike.
     * @param maxLength The maximum length of the hike.
     * @return A list of hikes that match the filter criteria.
     */
    public List<Hike> filterHikes(String name, String location, String date, String difficulty, Double minLength, Double maxLength) {
        List<Hike> hikes = new ArrayList<>();
        StringBuilder selection = new StringBuilder();
        List<String> selectionArgsList = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            selection.append(DatabaseHelper.KEY_HIKE_NAME + " LIKE ?");
            selectionArgsList.add("%" + name + "%");
        }

        if (location != null && !location.isEmpty()) {
            if (selection.length() > 0) {
                selection.append(" AND ");
            }
            selection.append(DatabaseHelper.KEY_HIKE_LOCATION + " LIKE ?");
            selectionArgsList.add("%" + location + "%");
        }

        if (date != null && !date.isEmpty()) {
            if (selection.length() > 0) {
                selection.append(" AND ");
            }
            selection.append(DatabaseHelper.KEY_HIKE_DATE + " = ?");
            selectionArgsList.add(date);
        }

        if (difficulty != null && !difficulty.isEmpty() && !difficulty.equalsIgnoreCase("Any difficulty")) {
            if (selection.length() > 0) {
                selection.append(" AND ");
            }
            selection.append(DatabaseHelper.KEY_HIKE_DIFFICULTY + " = ?");
            selectionArgsList.add(difficulty);
        }

        if (minLength != null) {
            if (selection.length() > 0) {
                selection.append(" AND ");
            }
            selection.append(DatabaseHelper.KEY_HIKE_LENGTH + " >= ?");
            selectionArgsList.add(String.valueOf(minLength));
        }

        if (maxLength != null) {
            if (selection.length() > 0) {
                selection.append(" AND ");
            }
            selection.append(DatabaseHelper.KEY_HIKE_LENGTH + " <= ?");
            selectionArgsList.add(String.valueOf(maxLength));
        }

        String[] selectionArgs = selectionArgsList.toArray(new String[0]);
        Cursor cursor = database.query(DatabaseHelper.TABLE_HIKES, null, selection.toString(), selectionArgs, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Hike hike = cursorToHike(cursor);
                hikes.add(hike);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return hikes;
    }

    /**
     * Helper method to convert a Cursor object to a Hike object.
     * @param cursor The Cursor to convert.
     * @return A Hike object.
     */
    private Hike cursorToHike(Cursor cursor) {
        Hike hike = new Hike();
        hike.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_HIKE_ID)));
        hike.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_HIKE_NAME)));
        hike.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_HIKE_LOCATION)));
        hike.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_HIKE_DATE)));
        hike.setParkingAvailable(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_HIKE_PARKING)));
        hike.setLength(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_HIKE_LENGTH)));
        hike.setDifficulty(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_HIKE_DIFFICULTY)));
        hike.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_HIKE_DESCRIPTION)));
        hike.setWeather(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_HIKE_WEATHER)));
        hike.setRecommendedGear(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_HIKE_RECOMMENDED_GEAR)));
        return hike;
    }
}
