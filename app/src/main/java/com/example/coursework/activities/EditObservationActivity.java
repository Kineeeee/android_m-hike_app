package com.example.coursework.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coursework.R;
import com.example.coursework.data.models.Observation;
import com.example.coursework.data.sqlite.ObservationDAO;

/**
 * This activity allows the user to edit an existing observation.
 * It pre-fills the form with the current observation details and allows the user to save the changes.
 */
public class EditObservationActivity extends AppCompatActivity {

    // UI elements
    private EditText observationText, observationTime, observationComments;
    private Button updateObservationButton;

    // Database access object and the observation being edited
    private ObservationDAO observationDAO;
    private Observation observation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_observation);

        // Initialize the database access object and open the database.
        observationDAO = new ObservationDAO(this);
        observationDAO.open();

        // Initialize UI components by finding them in the layout.
        observationText = findViewById(R.id.edit_observation_text);
        observationTime = findViewById(R.id.edit_observation_time);
        observationComments = findViewById(R.id.edit_observation_comments);
        updateObservationButton = findViewById(R.id.update_observation_button);

        // Get the observation ID from the intent.
        Intent intent = getIntent();
        long observationId = intent.getLongExtra("observation_id", -1);

        // Retrieve the observation from the database.
        observation = observationDAO.getObservationById(observationId);

        // Populate the input fields with the observation's current details.
        observationText.setText(observation.getObservation());
        observationTime.setText(observation.getTime());
        observationComments.setText(observation.getComments());

        // Set a click listener for the update button to save the changes.
        updateObservationButton.setOnClickListener(v -> {
            // Update the Observation object with the new details.
            observation.setObservation(observationText.getText().toString());
            observation.setTime(observationTime.getText().toString());
            observation.setComments(observationComments.getText().toString());


            // Validate that all required fields are filled.
            if (observationText.getText().toString().trim().isEmpty()
                    || observationTime.getText().toString().trim().isEmpty()
                    || observationComments.getText().toString().trim().isEmpty()) {

                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }


            // Update the observation in the database.
            observationDAO.updateObservation(observation);

            // Set the result to OK and finish the activity.
            setResult(RESULT_OK);
            finish();
        });
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
