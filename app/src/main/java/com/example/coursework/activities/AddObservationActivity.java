package com.example.coursework.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coursework.R;
import com.example.coursework.data.models.Observation;
import com.example.coursework.data.sqlite.ObservationDAO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This activity allows the user to add a new observation for a specific hike.
 * It provides a form for entering the observation details.
 */
public class AddObservationActivity extends AppCompatActivity {

    // UI elements
    private EditText observationText, observationTime, observationComments;
    private Button saveObservationButton;

    // Database access object and the ID of the hike this observation belongs to
    private ObservationDAO observationDAO;
    private long hikeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_observation);

        // Initialize the database access object and open the database.
        observationDAO = new ObservationDAO(this);
        observationDAO.open();

        // Initialize UI components by finding them in the layout.
        observationText = findViewById(R.id.observation_text);
        observationTime = findViewById(R.id.observation_time);
        observationComments = findViewById(R.id.observation_comments);
        saveObservationButton = findViewById(R.id.save_observation_button);

        // Get the hike ID from the intent.
        hikeId = getIntent().getLongExtra("hike_id", -1);

        // Set the default observation time to the current time.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        observationTime.setText(currentTime);

        // Set a click listener for the save button to save the observation.
        saveObservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveObservation();
            }
        });
    }

    /**
     * Gathers the observation details from the input fields and saves the observation to the database.
     */
    private void saveObservation() {
        // Get the observation details from the input fields.
        String observation = observationText.getText().toString().trim();
        String time = observationTime.getText().toString().trim();
        String comments = observationComments.getText().toString().trim();

        // Validate that all required fields are filled.
        if (observation.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Observation object and add it to the database.
        Observation newObservation = new Observation(hikeId, observation, time, comments);
        long result = observationDAO.addObservation(newObservation);

        if (result != -1) {
            Toast.makeText(this, "Observation saved successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish(); // Close the activity and return to the detail activity.
        } else {
            Toast.makeText(this, "Error saving observation", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Closes the database connection when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        observationDAO.close();
    }
}
