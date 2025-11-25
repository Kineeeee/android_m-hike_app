package com.example.coursework.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coursework.R;
import com.example.coursework.data.models.Hike;
import com.example.coursework.data.sqlite.HikeDAO;

import java.util.Calendar;

/**
 * This activity allows the user to add a new hike to the database.
 * It provides a form for entering the hike's details and a confirmation dialog before saving.
 */
public class AddHikeActivity extends AppCompatActivity {

    // UI elements
    private EditText hikeName, hikeLocation, hikeLength, hikeDescription, hikeWeather, hikeRecommendedGear;
    private TextView hikeDate;
    private RadioGroup parkingGroup;
    private Spinner hikeDifficulty;
    private Button saveButton;

    // Database access object
    private HikeDAO hikeDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hike);

        // Initialize the database access object and open the database.
        hikeDAO = new HikeDAO(this);
        hikeDAO.open();

        // Initialize UI components by finding them in the layout.
        hikeName = findViewById(R.id.hike_name);
        hikeLocation = findViewById(R.id.hike_location);
        hikeDate = findViewById(R.id.hike_date);
        hikeLength = findViewById(R.id.hike_length);
        hikeDescription = findViewById(R.id.hike_description);
        hikeWeather = findViewById(R.id.hike_weather);
        hikeRecommendedGear = findViewById(R.id.hike_recommended_gear);
        parkingGroup = findViewById(R.id.parking_group);
        hikeDifficulty = findViewById(R.id.hike_difficulty);
        saveButton = findViewById(R.id.save_button);

        // Set up the spinner for hike difficulty levels.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hikeDifficulty.setAdapter(adapter);

        // Set a click listener for the hike date TextView to show a date picker dialog.
        hikeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Set a click listener for the save button to save the hike.
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveHike();
            }
        });
    }

    /**
     * Shows a DatePickerDialog to allow the user to select a date for the hike.
     */
    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Set the selected date on the hikeDate TextView.
                        hikeDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    /**
     * Gathers the hike details from the input fields, shows a confirmation dialog, and then saves the hike to the database.
     */
    private void saveHike() {
        // Get the hike details from the input fields.
        String name = hikeName.getText().toString().trim();
        String location = hikeLocation.getText().toString().trim();
        String date = hikeDate.getText().toString().trim();
        String lengthStr = hikeLength.getText().toString().trim();
        String description = hikeDescription.getText().toString().trim();
        String weather = hikeWeather.getText().toString().trim();
        String recommendedGear = hikeRecommendedGear.getText().toString().trim();

        int selectedParkingId = parkingGroup.getCheckedRadioButtonId();

        // Validate that all required fields are filled.
        if (name.isEmpty() || location.isEmpty() || date.isEmpty() || lengthStr.isEmpty() || selectedParkingId == -1 || date.equals("Select date")) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedParking = findViewById(selectedParkingId);
        String parking = selectedParking.getText().toString();
        double length = Double.parseDouble(lengthStr);
        String difficulty = hikeDifficulty.getSelectedItem().toString();

        // Build a confirmation message with the hike details.
        String confirmationMessage = "Please confirm the details of the hike:\n\n" +
                "Name: " + name + "\n" +
                "Location: " + location + "\n" +
                "Date: " + date + "\n" +
                "Length: " + length + " (km)\n" +
                "Difficulty: " + difficulty + "\n" +
                "Parking Available: " + parking + "\n" +
                "Description: " + description + "\n" +
                "Weather: " + weather + "\n" +
                "Recommended Gear: " + recommendedGear;

        // Show a confirmation dialog before saving the hike.
        new AlertDialog.Builder(this)
                .setTitle("Confirm Hike Details")
                .setMessage(confirmationMessage)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // If confirmed, create a new Hike object and add it to the database.
                        Hike newHike = new Hike(name, location, date, parking, length, difficulty, description, weather, recommendedGear);
                        long result = hikeDAO.addHike(newHike);

                        if (result != -1) {
                            Toast.makeText(AddHikeActivity.this, "Hike saved successfully", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish(); // Close the activity and return to the main activity.
                        } else {
                            Toast.makeText(AddHikeActivity.this, "Error saving hike", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null) // If canceled, do nothing.
                .show();
    }

    /**
     * Closes the database connection when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        hikeDAO.close();
    }
}
