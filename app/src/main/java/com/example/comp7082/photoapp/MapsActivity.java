package com.example.comp7082.photoapp;

import android.content.Intent;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public String directory = Environment.getExternalStorageDirectory() + "/Android/data/com.example.comp7082.photoapp/files/Pictures/";
    static final int REQUEST_SET_IMAGE = 3;
    public static final String EXTRA_KEYWORDS_VIEWIAMGE = "com.example.comp7082.comp7082photogallery.KEYWORDS_VIEWIAMGE";
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    String[] sourceFilenames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        sourceFilenames = intent.getStringArrayExtra(MainActivity.EXTRA_PHOTO_LIST);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(googleMap != null){
            mMap = googleMap;
            mMap.setOnMarkerClickListener(this);
            if(sourceFilenames.length > 0 && sourceFilenames != null){
                for(String imageFileName : sourceFilenames){
                    try {
                        String path = directory + imageFileName;
                        File localFile = new File(path);
                        ExifInterface exif = new ExifInterface(path);
                        float[] latLong = ExifUtility.getCoordinates(localFile);

                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latLong[0], latLong[1]))
                                .title(imageFileName));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent tagIntent = new Intent( MapsActivity.this, ViewImage.class );
        tagIntent.putExtra( "FileName", directory + marker.getTitle());
        startActivityForResult( tagIntent , REQUEST_SET_IMAGE );
        return false;
    }
}
