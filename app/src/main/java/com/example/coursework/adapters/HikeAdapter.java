package com.example.coursework.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.R;
import com.example.coursework.data.models.Hike;

import java.util.List;

/**
 * Adapter for the RecyclerView that displays a list of hikes.
 * This class binds the hike data to the views in the hike_item layout.
 */
public class HikeAdapter extends RecyclerView.Adapter<HikeAdapter.HikeViewHolder> {

    private List<Hike> hikes;
    private OnHikeListener onHikeListener;

    /**
     * Interface for handling clicks on items in the RecyclerView.
     */
    public interface OnHikeListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
        void onItemClick(int position);
    }

    /**
     * Constructor for HikeAdapter.
     * @param hikes The list of hikes to display.
     * @param onHikeListener The listener for item clicks.
     */
    public HikeAdapter(List<Hike> hikes, OnHikeListener onHikeListener) {
        this.hikes = hikes;
        this.onHikeListener = onHikeListener;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new HikeViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public HikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hike_item, parent, false);
        return new HikeViewHolder(view, onHikeListener);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull HikeViewHolder holder, int position) {
        Hike hike = hikes.get(position);
        holder.hikeName.setText(hike.getName());
        holder.hikeLocation.setText(hike.getLocation());
        holder.hikeDate.setText(hike.getDate());
        holder.hikeLength.setText(holder.itemView.getContext().getString(R.string.length_format, hike.getLength()));
        holder.hikeParking.setText(hike.getParkingAvailable());
        holder.hikeDifficulty.setText(hike.getDifficulty());
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return hikes.size();
    }

    /**
     * Updates the list of hikes with a filtered list and notifies the adapter.
     * @param filteredList The filtered list of hikes.
     */
    public void filterList(List<Hike> filteredList) {
        hikes = filteredList;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder for the hike item.
     * This class holds the views for a single item in the RecyclerView.
     */
    public static class HikeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView hikeName, hikeLocation, hikeDate, hikeLength, hikeParking, hikeDifficulty;
        Button viewDetailsButton;
        ImageButton deleteButton;
        OnHikeListener onHikeListener;

        public HikeViewHolder(@NonNull View itemView, OnHikeListener onHikeListener) {
            super(itemView);
            hikeName = itemView.findViewById(R.id.hike_name_text);
            hikeLocation = itemView.findViewById(R.id.hike_location_text);
            hikeDate = itemView.findViewById(R.id.hike_date_text);
            hikeLength = itemView.findViewById(R.id.hike_length_text);
            hikeParking = itemView.findViewById(R.id.hike_parking_text);
            hikeDifficulty = itemView.findViewById(R.id.hike_difficulty_text);
            viewDetailsButton = itemView.findViewById(R.id.view_details_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
            this.onHikeListener = onHikeListener;

            // Set click listeners for the buttons and the item view itself.
            viewDetailsButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        /**
         * Handles clicks on the views within the ViewHolder.
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.delete_button) {
                onHikeListener.onDeleteClick(getAdapterPosition());
            } else { // Handles both viewDetailsButton and itemView clicks
                onHikeListener.onItemClick(getAdapterPosition());
            }
        }
    }
}
