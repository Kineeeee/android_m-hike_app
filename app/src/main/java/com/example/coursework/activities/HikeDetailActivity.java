package com.example.coursework.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.R;
import com.example.coursework.adapters.ObservationAdapter;
import com.example.coursework.data.models.Hike;
import com.example.coursework.data.models.Observation;
import com.example.coursework.data.sqlite.HikeDAO;
import com.example.coursework.data.sqlite.ObservationDAO;

import java.util.List;

/**
 * This activity displays the details of a specific hike, including its observations.
 * It allows the user to add new observations for the hike.
 */
public class HikeDetailActivity extends AppCompatActivity {

    // UI elements
    private TextView hikeName, hikeLocation, hikeDate, hikeParking, hikeLength, hikeDifficulty, hikeDescription, hikeWeather, hikeRecommendedGear;
    private RecyclerView observationsRecyclerView;
    private Button addObservationButton;

    // Database access objects and data
    private HikeDAO hikeDAO;
    private ObservationDAO observationDAO;
    private ObservationAdapter observationAdapter;
    private List<Observation> observations;
    private long hikeId;

    // Request codes for starting activities for result
    private static final int ADD_OBSERVATION_REQUEST = 1;
    private static final int EDIT_OBSERVATION_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_detail);

        // Initialize DAOs and open database connections
        hikeDAO = new HikeDAO(this);
        hikeDAO.open();
        observationDAO = new ObservationDAO(this);
        observationDAO.open();

        // Initialize UI components
        hikeName = findViewById(R.id.hike_name_detail);
        hikeLocation = findViewById(R.id.hike_location_detail);
        hikeDate = findViewById(R.id.hike_date_detail);
        hikeParking = findViewById(R.id.hike_parking_detail);
        hikeLength = findViewById(R.id.hike_length_detail);
        hikeDifficulty = findViewById(R.id.hike_difficulty_detail);
        hikeDescription = findViewById(R.id.hike_description_detail);
        hikeWeather = findViewById(R.id.hike_weather_detail);
        hikeRecommendedGear = findViewById(R.id.hike_recommended_gear_detail);
        observationsRecyclerView = findViewById(R.id.observations_recycler_view);
        addObservationButton = findViewById(R.id.add_observation_button);

        // Set up the RecyclerView for observations
        observationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the hike ID from the intent
        Intent intent = getIntent();
        hikeId = intent.getLongExtra("hike_id", -1);

        if (hikeId != -1) {
            // Load hike details from the database
            Hike hike = hikeDAO.getHikeById(hikeId);
            if (hike != null) {
                // Populate the UI with hike details
                hikeName.setText(hike.getName());
                hikeLocation.setText(hike.getLocation());
                hikeDate.setText(hike.getDate());
                hikeParking.setText(hike.getParkingAvailable());
                hikeLength.setText(hike.getLength() + " km");
                hikeDifficulty.setText(hike.getDifficulty());

                // Handle optional fields, displaying "N/A" if they are empty
                if (hike.getDescription() != null && !hike.getDescription().isEmpty()) {
                    hikeDescription.setText(hike.getDescription());
                } else {
                    hikeDescription.setText("N/A");
                }

                if (hike.getWeather() != null && !hike.getWeather().isEmpty()) {
                    hikeWeather.setText(hike.getWeather());
                } else {
                    hikeWeather.setText("N/A");
                }

                if (hike.getRecommendedGear() != null && !hike.getRecommendedGear().isEmpty()) {
                    hikeRecommendedGear.setText(hike.getRecommendedGear());
                } else {
                    hikeRecommendedGear.setText("N/A");
                }
            }
        }

        // Load the observations for this hike
        loadObservations();

        // Set up the click listener for the "Add Observation" button
        addObservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addObservationIntent = new Intent(HikeDetailActivity.this, AddObservationActivity.class);
                addObservationIntent.putExtra("hike_id", hikeId);
                startActivityForResult(addObservationIntent, ADD_OBSERVATION_REQUEST);
            }
        });
    }

    /**
     * Loads the observations for the current hike from the database and updates the RecyclerView.
     */
    private void loadObservations() {
        observations = observationDAO.getObservationsForHike(hikeId);
        observationAdapter = new ObservationAdapter(this, observations, observationDAO);
        observationsRecyclerView.setAdapter(observationAdapter);
    }

    /**
     * Handles the result from the AddObservationActivity and EditObservationActivity.
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == ADD_OBSERVATION_REQUEST || requestCode == EDIT_OBSERVATION_REQUEST) && resultCode == RESULT_OK) {
            // If an observation was added or edited, reload the list of observations.
            loadObservations();
        }
    }

    /**
     * Closes the database connections when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        hikeDAO.close();
        observationDAO.close();
    }
}
