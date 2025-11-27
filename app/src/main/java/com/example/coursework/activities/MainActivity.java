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

    // UI Components
    private RecyclerView hikesRecyclerView;
    private Button addHikeButton, resetButton, filtersButton;
    private SearchView searchView;
    private TextView emptyTextView; // TextView for empty state

    // Data and Adapter
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

        // Initialize UI components by finding them in the layout
        hikesRecyclerView = findViewById(R.id.hikes_recycler_view);
        addHikeButton = findViewById(R.id.add_hike_button);
        resetButton = findViewById(R.id.reset_button);
        filtersButton = findViewById(R.id.filters_button);
        searchView = findViewById(R.id.search_view);
        emptyTextView = findViewById(R.id.empty_text);

        // Set up the RecyclerView.
        hikesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load the list of hikes from the database.
        loadHikes();

        // Set up the click listener for the "Add Hike" button to start AddHikeActivity.
        addHikeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddHikeActivity.class);
            startActivityForResult(intent, ADD_HIKE_REQUEST);
        });

        // Set up the click listener for the "Reset" button.
        resetButton.setOnClickListener(v -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getString(R.string.confirm_reset_title))
                    .setMessage(getString(R.string.confirm_reset_message))
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                        hikeDAO.deleteAllHikes();
                        loadHikes(); // Reload to show empty state
                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();
        });

        // Set up the click listener for the "Filters" button.
        filtersButton.setOnClickListener(v -> showFilterDialog());

        // Set up the search view for filtering hikes by name.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Not handling submit, filtering is real-time
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter hikes based on the search query
                List<Hike> filteredHikes = hikeDAO.filterHikes(newText, null,null, null, null, null);
                hikeAdapter.filterList(filteredHikes);
                checkEmptyView(); // Check if the list is empty after filtering
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

        // Initialize input fields from the dialog layout
        final TextInputEditText nameInput = dialogView.findViewById(R.id.filter_hike_name);
        final TextInputEditText locationInput = dialogView.findViewById(R.id.filter_location);
        final TextView dateInput = dialogView.findViewById(R.id.filter_date);
        final Spinner difficultySpinner = dialogView.findViewById(R.id.filter_difficulty);
        final EditText minLengthInput = dialogView.findViewById(R.id.filter_min_length);
        final EditText maxLengthInput = dialogView.findViewById(R.id.filter_max_length);

        // Set up date picker for the date field
        dateInput.setOnClickListener(v -> showDatePickerDialog(dateInput));

        // Set up the spinner for difficulty levels
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_array_filter, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(adapter);

        builder.setTitle(getString(R.string.filter_hikes_title))
                .setPositiveButton(getString(R.string.apply_filters), (dialog, id) -> {
                    // Get filter values from the input fields
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

                    // Apply filters and update the adapter
                    List<Hike> filteredHikes = hikeDAO.filterHikes(name, location, date, difficulty, minLength,
                            maxLength);
                    hikeAdapter.filterList(filteredHikes);
                    checkEmptyView(); // Check if list is empty after filtering
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, id) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Shows a DatePickerDialog to allow the user to select a date.
     * @param dateView The TextView to update with the selected date.
     */
    private void showDatePickerDialog(TextView dateView) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create and show the dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> dateView.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1), year, month, day);
        datePickerDialog.show();
    }


    /**
     * Loads the list of hikes from the database and updates the RecyclerView.
     */
    private void loadHikes() {
        hikes = hikeDAO.getAllHikes();
        hikeAdapter = new HikeAdapter(hikes, this);
        hikesRecyclerView.setAdapter(hikeAdapter);
        checkEmptyView(); // Check if the list is empty after loading
    }

    /**
     * Toggles the visibility of the RecyclerView and the empty view TextView.
     * Shows the empty view if the adapter has no items, otherwise shows the RecyclerView.
     */
    private void checkEmptyView() {
        if (hikeAdapter.getItemCount() == 0) {
            hikesRecyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            hikesRecyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
        }
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
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.confirm_delete_title))
                .setMessage(getString(R.string.confirm_delete_message))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    hikeDAO.deleteHike(hikeToDelete.getId());
                    loadHikes(); // Reload the hikes to reflect the deletion.
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
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
