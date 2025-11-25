package com.example.coursework.activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.R;
import com.example.coursework.adapters.HikeAdapter;
import com.example.coursework.data.models.Hike;
import com.example.coursework.data.sqlite.HikeDAO;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;

/**
 * The main activity of the application, which displays a list of hikes.
 * This activity allows the user to view, add, edit, delete, and filter hikes.
 */
public class MainActivity extends AppCompatActivity implements HikeAdapter.OnHikeListener {

    private RecyclerView hikesRecyclerView;
    private Button addHikeButton, resetButton, filtersButton;
    private SearchView searchView;
    private HikeAdapter hikeAdapter;
    private HikeDAO hikeDAO;
    private List<Hike> hikes;

    // Request codes for starting activities for result.
    private static final int ADD_HIKE_REQUEST = 1;
    private static final int EDIT_HIKE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the database access object.
        hikeDAO = new HikeDAO(this);
        hikeDAO.open();

        // Initialize UI components.
        hikesRecyclerView = findViewById(R.id.hikes_recycler_view);
        addHikeButton = findViewById(R.id.add_hike_button);
        resetButton = findViewById(R.id.reset_button);
        filtersButton = findViewById(R.id.filters_button);
        searchView = findViewById(R.id.search_view);

        // Set up the RecyclerView.
        hikesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load the list of hikes from the database.
        loadHikes();

        // Set up the click listener for the "Add Hike" button.
        addHikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddHikeActivity.class);
                startActivityForResult(intent, ADD_HIKE_REQUEST);
            }
        });

        // Set up the click listener for the "Reset" button.
        resetButton.setOnClickListener(v -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getString(R.string.confirm_reset_title))
                    .setMessage(getString(R.string.confirm_reset_message))
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                        hikeDAO.deleteAllHikes();
                        loadHikes();
                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();
        });

        // Set up the click listener for the "Filters" button.
        filtersButton.setOnClickListener(v -> {
            showFilterDialog();
        });

        // Set up the search view for filtering hikes by name.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Hike> filteredHikes = hikeDAO.filterHikes(newText, null,null, null, null, null);
                hikeAdapter.filterList(filteredHikes);
                return true;
            }
        });
    }

    /**
     * Displays a dialog for filtering the list of hikes.
     */
    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_filter, null);
        builder.setView(dialogView);

        final TextInputEditText nameInput = dialogView.findViewById(R.id.filter_hike_name);
        final TextInputEditText locationInput = dialogView.findViewById(R.id.filter_location);
        final TextView dateInput = dialogView.findViewById(R.id.filter_date);
        final Spinner difficultySpinner = dialogView.findViewById(R.id.filter_difficulty);
        final EditText minLengthInput = dialogView.findViewById(R.id.filter_min_length);
        final EditText maxLengthInput = dialogView.findViewById(R.id.filter_max_length);

        dateInput.setOnClickListener(v -> {
            showDatePickerDialog(dateInput);
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_array_filter, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(adapter);

        builder.setTitle(getString(R.string.filter_hikes_title))
                .setPositiveButton(getString(R.string.apply_filters), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String name = nameInput.getText().toString();
                        String location = locationInput.getText().toString();
                        String date = dateInput.getText().toString().equals("Select date") ? null : dateInput.getText().toString();
                        String difficulty = difficultySpinner.getSelectedItem().toString();
                        Double minLength = !minLengthInput.getText().toString().isEmpty()
                                ? Double.parseDouble(minLengthInput.getText().toString())
                                : null;
                        Double maxLength = !maxLengthInput.getText().toString().isEmpty()
                                ? Double.parseDouble(maxLengthInput.getText().toString())
                                : null;

                        List<Hike> filteredHikes = hikeDAO.filterHikes(name, location, date, difficulty, minLength,
                                maxLength);
                        hikeAdapter.filterList(filteredHikes);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDatePickerDialog(TextView dateView) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateView.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }


    /**
     * Loads the list of hikes from the database and updates the RecyclerView.
     */
    private void loadHikes() {
        hikes = hikeDAO.getAllHikes();
        hikeAdapter = new HikeAdapter(hikes, this);
        hikesRecyclerView.setAdapter(hikeAdapter);
    }

    /**
     * Handles the result from the AddHikeActivity and EditHikeActivity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == ADD_HIKE_REQUEST || requestCode == EDIT_HIKE_REQUEST)
                && (resultCode == RESULT_OK || resultCode == RESULT_CANCELED)) {
            loadHikes(); // Reload the hikes to reflect any changes.
        }
    }

    /**
     * Handles the edit click on a hike item.
     */
    @Override
    public void onEditClick(int position) {
        Hike hikeToEdit = hikes.get(position);
        Intent intent = new Intent(this, EditHikeActivity.class);
        intent.putExtra("hike_id", hikeToEdit.getId());
        startActivityForResult(intent, EDIT_HIKE_REQUEST);
    }

    /**
     * Handles the delete click on a hike item.
     */
    @Override
    public void onDeleteClick(int position) {
        Hike hikeToDelete = hikes.get(position);
        hikeDAO.deleteHike(hikeToDelete.getId());
        loadHikes(); // Reload the hikes to reflect the deletion.
    }

    /**
     * Handles the click on a hike item to view its details.
     */
    @Override
    public void onItemClick(int position) {
        Hike hikeToShow = hikes.get(position);
        Intent intent = new Intent(this, HikeDetailActivity.class);
        intent.putExtra("hike_id", hikeToShow.getId());
        startActivity(intent);
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
