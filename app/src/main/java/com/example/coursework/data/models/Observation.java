package com.example.coursework.data.models;

/**
 * Represents a single observation made during a hike.
 * This class is a simple Plain Old Java Object (POJO) that holds information about an observation.
 */
public class Observation {
    // Unique identifier for the observation in the database.
    private long id;
    // The ID of the hike this observation belongs to.
    private long hikeId;
    // The main observation text (e.g., "Saw a bird").
    private String observation;
    // The time of the observation.
    private String time;
    // Additional comments about the observation.
    private String comments;

    /**
     * Default constructor.
     */
    public Observation() {
    }

    /**
     * Constructor to create a new Observation object.
     *
     * @param hikeId      The ID of the hike this observation belongs to.
     * @param observation The main observation text.
     * @param time        The time of the observation.
     * @param comments    Additional comments about the observation.
     */
    public Observation(long hikeId, String observation, String time, String comments) {
        this.hikeId = hikeId;
        this.observation = observation;
        this.time = time;
        this.comments = comments;
    }

    // Getters and Setters for the observation properties.

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getHikeId() {
        return hikeId;
    }

    public void setHikeId(long hikeId) {
        this.hikeId = hikeId;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
