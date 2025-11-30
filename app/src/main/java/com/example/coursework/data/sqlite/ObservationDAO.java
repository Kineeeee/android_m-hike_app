package com.example.coursework.data.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.coursework.data.models.Observation;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the 'observations' table.
 * This class provides methods to perform CRUD (Create, Read, Update, Delete) operations on the observations table.
 */
public class ObservationDAO {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    /**
     * Constructor for ObservationDAO.
     * @param context The application context.
     */
    public ObservationDAO(Context context) {
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
     * Adds a new observation to the database.
     * @param observation The Observation object to add.
     * @return The ID of the newly inserted observation, or -1 if an error occurred.
     */
    public long addObservation(Observation observation) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_OBSERVATION_HIKE_ID_FK, observation.getHikeId());
        values.put(DatabaseHelper.KEY_OBSERVATION_TEXT, observation.getObservation());
        values.put(DatabaseHelper.KEY_OBSERVATION_TIME, observation.getTime());
        values.put(DatabaseHelper.KEY_OBSERVATION_COMMENTS, observation.getComments());

        return database.insert(DatabaseHelper.TABLE_OBSERVATIONS, null, values);
    }

    /**
     * Retrieves all observations for a specific hike.
     * @param hikeId The ID of the hike.
     * @return A list of all Observation objects for the given hike.
     */
    public List<Observation> getObservationsForHike(long hikeId) {
        List<Observation> observations = new ArrayList<>();
        String selection = DatabaseHelper.KEY_OBSERVATION_HIKE_ID_FK + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(hikeId)};
        Cursor cursor = database.query(DatabaseHelper.TABLE_OBSERVATIONS, null, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Observation observation = cursorToObservation(cursor);
                observations.add(observation);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return observations;
    }

    /**
     * Retrieves a single observation by its ID.
     * @param observationId The ID of the observation to retrieve.
     * @return The Observation object if found, otherwise null.
     */
    public Observation getObservationById(long observationId) {
        String selection = DatabaseHelper.KEY_OBSERVATION_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(observationId)};
        Cursor cursor = database.query(DatabaseHelper.TABLE_OBSERVATIONS, null, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Observation observation = cursorToObservation(cursor);
            cursor.close();
            return observation;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    /**
     * Updates an existing observation in the database.
     * @param observation The Observation object with updated information.
     * @return The number of rows affected.
     */
    public int updateObservation(Observation observation) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_OBSERVATION_TEXT, observation.getObservation());
        values.put(DatabaseHelper.KEY_OBSERVATION_TIME, observation.getTime());
        values.put(DatabaseHelper.KEY_OBSERVATION_COMMENTS, observation.getComments());

        return database.update(DatabaseHelper.TABLE_OBSERVATIONS, values, DatabaseHelper.KEY_OBSERVATION_ID + " = ?",
                new String[]{String.valueOf(observation.getId())});
    }

    /**
     * Deletes an observation from the database by its ID.
     * @param observationId The ID of the observation to delete.
     */
    public void deleteObservation(long observationId) {
        database.delete(DatabaseHelper.TABLE_OBSERVATIONS, DatabaseHelper.KEY_OBSERVATION_ID + " = " + observationId, null);
    }

    /**
     * Helper method to convert a Cursor object to an Observation object.
     * @param cursor The Cursor to convert.
     * @return An Observation object.
     */
    private Observation cursorToObservation(Cursor cursor) {
        Observation observation = new Observation();
        observation.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_OBSERVATION_ID)));
        observation.setHikeId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_OBSERVATION_HIKE_ID_FK)));
        observation.setObservation(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_OBSERVATION_TEXT)));
        observation.setTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_OBSERVATION_TIME)));
        observation.setComments(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_OBSERVATION_COMMENTS)));
        return observation;
    }



    // delete all observation
    public void deleteAllObservations() {
        database.delete(DatabaseHelper.TABLE_OBSERVATIONS, null, null);
    }
}
