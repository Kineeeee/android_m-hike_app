package com.example.coursework.data.models;

/**
 * Represents a single hike entity.
 * This class is a simple Plain Old Java Object (POJO) that holds information about a hike.
 */
public class Hike {
    // Unique identifier for the hike in the database.
    private long id;
    // Name of the hike.
    private String name;
    // Location of the hike.
    private String location;
    // Date of the hike.
    private String date;
    // Information about parking availability.
    private String parkingAvailable;
    // Length of the hike in kilometers.
    private double length;
    // Difficulty level of the hike (e.g., Easy, Medium, Hard).
    private String difficulty;
    // A description of the hike.
    private String description;
    // Weather conditions for the hike.
    private String weather;
    // Recommended gear for the hike.
    private String recommendedGear;

    /**
     * Default constructor.
     */
    public Hike() {
    }

    /**
     * Constructor to create a new Hike object.
     *
     * @param name             Name of the hike.
     * @param location         Location of the hike.
     * @param date             Date of the hike.
     * @param parkingAvailable "Yes" or "No" indicating parking availability.
     * @param length           Length of the hike in kilometers.
     * @param difficulty       Difficulty level of the hike.
     * @param description      A description of the hike.
     * @param weather          Weather conditions for the hike.
     * @param recommendedGear  Recommended gear for the hike.
     */
    public Hike(String name, String location, String date, String parkingAvailable, double length, String difficulty, String description, String weather, String recommendedGear) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.parkingAvailable = parkingAvailable;
        this.length = length;
        this.difficulty = difficulty;
        this.description = description;
        this.weather = weather;
        this.recommendedGear = recommendedGear;
    }

    // Getters and Setters for the hike properties.

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getParkingAvailable() {
        return parkingAvailable;
    }

    public void setParkingAvailable(String parkingAvailable) {
        this.parkingAvailable = parkingAvailable;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getRecommendedGear() {
        return recommendedGear;
    }

    public void setRecommendedGear(String recommendedGear) {
        this.recommendedGear = recommendedGear;
    }
}
