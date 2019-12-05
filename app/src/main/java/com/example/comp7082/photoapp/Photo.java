package com.example.comp7082.photoapp;

public class Photo {
    public String filename;
    public String path;
    public float latitude;
    public float longitude;

    public Photo(){
        super();
    }

    public Photo(String filename, String path, float latitude, float longitude){
     this.filename = filename;
     this.path = path;
     this.latitude = latitude;
     this.longitude = longitude;
    }

    public String getFilename(){
        return filename;
    }

    public String getPath() {
        return path;
    }

    public float getLatitude(){
        return latitude;
    }

    public float getLongitude(){
        return longitude;
    }
}
