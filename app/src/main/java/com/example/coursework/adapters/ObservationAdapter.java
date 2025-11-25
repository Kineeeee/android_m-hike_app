package com.example.coursework.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.R;
import com.example.coursework.activities.EditObservationActivity;
import com.example.coursework.data.models.Observation;
import com.example.coursework.data.sqlite.ObservationDAO;

import java.util.List;

/**
 * Adapter for the RecyclerView that displays a list of observations for a hike.
 * This class binds the observation data to the views in the observation_item layout.
 */
public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.ObservationViewHolder> {

    private List<Observation> observations;
    private ObservationDAO observationDAO;
    private Context context;

    /**
     * Constructor for ObservationAdapter.
     * @param context The application context.
     * @param observations The list of observations to display.
     * @param observationDAO The DAO for interacting with the observation data.
     */
    public ObservationAdapter(Context context, List<Observation> observations, ObservationDAO observationDAO) {
        this.context = context;
        this.observations = observations;
        this.observationDAO = observationDAO;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ObservationViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ObservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.observation_item, parent, false);
        return new ObservationViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ObservationViewHolder holder, int position) {
        Observation observation = observations.get(position);
        holder.observationText.setText(observation.getObservation());
        holder.observationTime.setText(observation.getTime());
        holder.observationComments.setText(observation.getComments());

        // Set a click listener for the edit button to open the EditObservationActivity.
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditObservationActivity.class);
            intent.putExtra("observation_id", observation.getId());
            ((Activity) context).startActivityForResult(intent, 2); // 2 is the request code for editing an observation
        });

        // Set a click listener for the delete button to remove the observation.
        holder.deleteButton.setOnClickListener(v -> {
            observationDAO.deleteObservation(observation.getId());
            observations.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, observations.size());
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return observations.size();
    }

    /**
     * ViewHolder for the observation item.
     * This class holds the views for a single item in the RecyclerView.
     */
    public static class ObservationViewHolder extends RecyclerView.ViewHolder {
        TextView observationText, observationTime, observationComments;
        Button editButton, deleteButton;

        public ObservationViewHolder(@NonNull View itemView) {
            super(itemView);
            observationText = itemView.findViewById(R.id.observation_text);
            observationTime = itemView.findViewById(R.id.observation_time_text);
            observationComments = itemView.findViewById(R.id.observation_comments_text);
            editButton = itemView.findViewById(R.id.edit_observation_button);
            deleteButton = itemView.findViewById(R.id.delete_observation_button);
        }
    }
}
