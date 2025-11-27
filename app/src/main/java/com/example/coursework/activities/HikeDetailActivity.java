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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    private FloatingActionButton editHikeFab;

    // Database access objects and data
    private HikeDAO hikeDAO;
    private ObservationDAO observationDAO;
    private ObservationAdapter observationAdapter;
    private List<Observation> observations;
    private long hikeId;

    // Request codes for starting activities for result
    private static final int ADD_OBSERVATION_REQUEST = 1;
    private static final int EDIT_OBSERVATION_REQUEST = 2;
    private static final int EDIT_HIKE_REQUEST = 3;

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
        initializeViews();

        // Set up the RecyclerView for observations
        observationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the hike ID from the intent
        Intent intent = getIntent();
        hikeId = intent.getLongExtra("hike_id", -1);

        // Set up the click listeners
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hikeId != -1) {
            loadHikeDetails();
            loadObservations();
        }
    }

    private void initializeViews() {
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
        editHikeFab = findViewById(R.id.edit_hike_fab);
    }

    private void setupClickListeners() {
        addObservationButton.setOnClickListener(v -> {
            Intent addObservationIntent = new Intent(HikeDetailActivity.this, AddObservationActivity.class);
            addObservationIntent.putExtra("hike_id", hikeId);
            startActivityForResult(addObservationIntent, ADD_OBSERVATION_REQUEST);
        });

        editHikeFab.setOnClickListener(v -> {
            Intent editHikeIntent = new Intent(HikeDetailActivity.this, EditHikeActivity.class);
            editHikeIntent.putExtra("hike_id", hikeId);
            startActivityForResult(editHikeIntent, EDIT_HIKE_REQUEST);
        });
    }

    private void loadHikeDetails() {
        Hike hike = hikeDAO.getHikeById(hikeId);
        if (hike != null) {
            hikeName.setText(hike.getName());
            hikeLocation.setText(hike.getLocation());
            hikeDate.setText(hike.getDate());
            hikeParking.setText(hike.getParkingAvailable());
            hikeLength.setText(hike.getLength() + " km");
            hikeDifficulty.setText(hike.getDifficulty());

            hikeDescription.setText(hike.getDescription() != null && !hike.getDescription().isEmpty() ? hike.getDescription() : "N/A");
            hikeWeather.setText(hike.getWeather() != null && !hike.getWeather().isEmpty() ? hike.getWeather() : "N/A");
            hikeRecommendedGear.setText(hike.getRecommendedGear() != null && !hike.getRecommendedGear().isEmpty() ? hike.getRecommendedGear() : "N/A");
        }
    }

    private void loadObservations() {
        observations = observationDAO.getObservationsForHike(hikeId);
        observationAdapter = new ObservationAdapter(this, observations, observationDAO);
        observationsRecyclerView.setAdapter(observationAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_OBSERVATION_REQUEST || requestCode == EDIT_OBSERVATION_REQUEST) {
                loadObservations();
            } else if (requestCode == EDIT_HIKE_REQUEST) {
                loadHikeDetails();
                loadObservations();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hikeDAO.close();
        observationDAO.close();
    }
}
