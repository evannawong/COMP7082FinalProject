package com.example.comp7082.photoapp;

public class Attraction {
    private String name;
    private String address;
    private float latitude;
    private float longitude;

    public Attraction(){
        super();
    }

    public Attraction(String name, String address, float latitude, float longitude){
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName(){
        return name;
    }

    public float getLatitude(){
        return latitude;
    }

    public float getLongitude(){
        return longitude;
    }
}
