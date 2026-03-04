package com.example.campus_navigation_helper.models;

public class NavigationStep {
    private int id;
    private int locationId;
    private int stepNumber;
    private String stepDescription;

    public NavigationStep(int id, int locationId, int stepNumber, String stepDescription) {
        this.id = id;
        this.locationId = locationId;
        this.stepNumber = stepNumber;
        this.stepDescription = stepDescription;
    }

    public int getId() {
        return id;
    }

    public int getLocationId() {
        return locationId;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public String getStepDescription() {
        return stepDescription;
    }
}
