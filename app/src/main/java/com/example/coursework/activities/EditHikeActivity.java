package com.example.coursework.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
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
 * This activity allows the user to edit the details of an existing hike.
 * It pre-fills the form with the current hike details and allows the user to save the changes.
 */
public class EditHikeActivity extends AppCompatActivity {

    // UI elements
    private EditText hikeName, hikeLocation, hikeLength, hikeDescription, hikeWeather, hikeRecommendedGear;
    private TextView hikeDate;
    private RadioGroup parkingGroup;
    private RadioButton parkingYes, parkingNo;
    private Spinner hikeDifficulty;
    private Button updateButton;

    // Database access object and the hike being edited
    private HikeDAO hikeDAO;
    private Hike hike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hike);

        // Initialize the database access object and open the database.
        hikeDAO = new HikeDAO(this);
        hikeDAO.open();

        // Initialize UI components by finding them in the layout.
        hikeName = findViewById(R.id.hike_name_edit);
        hikeLocation = findViewById(R.id.hike_location_edit);
        hikeDate = findViewById(R.id.hike_date_edit);
        hikeLength = findViewById(R.id.hike_length_edit);
        hikeDescription = findViewById(R.id.hike_description_edit);
        hikeWeather = findViewById(R.id.hike_weather_edit);
        hikeRecommendedGear = findViewById(R.id.hike_recommended_gear_edit);
        parkingGroup = findViewById(R.id.parking_group_edit);
        parkingYes = findViewById(R.id.parking_yes_edit);
        parkingNo = findViewById(R.id.parking_no_edit);
        hikeDifficulty = findViewById(R.id.hike_difficulty_edit);
        updateButton = findViewById(R.id.update_button);

        // Set up the spinner for hike difficulty levels.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hikeDifficulty.setAdapter(adapter);

        // Get the hike ID from the intent.
        Intent intent = getIntent();
        long hikeId = intent.getLongExtra("hike_id", -1);

        if (hikeId != -1) {
            // Retrieve the hike from the database.
            hike = hikeDAO.getHikeById(hikeId);
        }

        if (hike != null) {
            // Populate the input fields with the hike's current details.
            hikeName.setText(hike.getName());
            hikeLocation.setText(hike.getLocation());
            hikeDate.setText(hike.getDate());
            hikeLength.setText(String.valueOf(hike.getLength()));
            hikeDescription.setText(hike.getDescription());
            hikeWeather.setText(hike.getWeather());
            hikeRecommendedGear.setText(hike.getRecommendedGear());

            String parking = hike.getParkingAvailable();
            if (parking != null && parking.equals("Yes")) {
                parkingYes.setChecked(true);
            } else {
                parkingNo.setChecked(true);
            }

            String difficulty = hike.getDifficulty();
            if (difficulty != null) {
                int spinnerPosition = adapter.getPosition(difficulty);
                hikeDifficulty.setSelection(spinnerPosition);
            }
        } else {
            // If the hike is not found, show an error message and finish the activity.
            Toast.makeText(this, "Hike not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set a click listener for the hike date TextView to show a date picker dialog.
        hikeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Set a click listener for the update button to save the changes.
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateHike();
            }
        });
    }

    /**
     * Shows a DatePickerDialog to allow the user to select a new date for the hike.
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
     * Gathers the updated hike details from the input fields and saves them to the database.
     */
    private void updateHike() {
        // Get the updated hike details from the input fields.
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

        // Update the Hike object with the new details.
        hike.setName(name);
        hike.setLocation(location);
        hike.setDate(date);
        hike.setParkingAvailable(parking);
        hike.setLength(length);
        hike.setDifficulty(difficulty);
        hike.setDescription(description);
        hike.setWeather(weather);
        hike.setRecommendedGear(recommendedGear);

        // Update the hike in the database.
        int result = hikeDAO.updateHike(hike);

        if (result > 0) {
            Toast.makeText(this, "Hike updated successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish(); // Close the activity and return to the main activity.
        } else {
            Toast.makeText(this, "Error updating hike", Toast.LENGTH_SHORT).show();
        }
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
