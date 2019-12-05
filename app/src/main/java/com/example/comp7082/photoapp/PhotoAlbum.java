package com.example.comp7082.photoapp;

import java.util.List;

public class PhotoAlbum {
    public List<Photo> photos;

    public PhotoAlbum(){
        super();
    }

    public void addPhoto(Photo photo){
        photos.add(photo);
    }

    public List<Photo> getPhotos(){
        return photos;
    }
}
