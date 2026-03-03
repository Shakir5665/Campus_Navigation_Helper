package com.example.campus_navigation_helper.models;

public class Location {
    private int id;
    private String name;
    private String block;
    private String shortDescription;

    public Location(int id, String name, String block, String shortDescription) {
        this.id = id;
        this.name = name;
        this.block = block;
        this.shortDescription = shortDescription;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBlock() {
        return block;
    }

    public String getShortDescription() {
        return shortDescription;
    }
}
