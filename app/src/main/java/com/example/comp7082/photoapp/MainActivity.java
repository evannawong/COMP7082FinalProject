package com.example.comp7082.photoapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener,
        LocationListener {

    public String currentPhotoPath;
    public String directory = Environment.getExternalStorageDirectory() + "/Android/data/com.example.comp7082.photoapp/files/Pictures/";
    public String[] filenames;
    public int currentIndex = 0;
    public Bitmap bitmap;

    public ImageView imageView;
    private TextView imageIndexTextView;

    private static final float MIN_FLING_DISTANCE = 200.0f;
    private static final float MAX_FLING_DISTANCE = 1000.0f;
    public static final int NAVIGATE_RIGHT = 1;
    public static final int NAVIGATE_LEFT = -1;

    public static final String EXTRA_PHOTO_LIST = "com.example.comp7082.comp7082photogallery.PHOTO_LIST";

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_MAP = 2;

    private GestureDetector gestureScanner;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        imageIndexTextView = findViewById(R.id.imageIndexTextView);

        gestureScanner = new GestureDetector(getBaseContext(), this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        getFilenames(directory);
        if (filenames != null && filenames.length > 0) {
            currentPhotoPath = getCurrentFilePath();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableLocationUpdates();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (currentPhotoPath != null) {
                createPicture(currentPhotoPath);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private void updateImageIndexText() {
        StringBuilder sb = new StringBuilder();
        sb.append(currentIndex + 1);
        if (filenames != null && filenames.length > 0) {
            sb.append(" of ");
            sb.append(filenames.length);
        }
        imageIndexTextView.setText(sb.toString());
    }

    public void onSnapClicked(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //enableLocationUpdates();    // begin scanning for location upon taking a photo
        Log.d("onSnapClicked", "Begin capturing a photo");
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this,
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.comp7082.photoapp.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public void onMapClicked(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        getFilenames(directory);    // ensure we send the whole list each time

        intent.putExtra(EXTRA_PHOTO_LIST, filenames);
        startActivityForResult(intent, REQUEST_MAP);
    }

    public void onPointsOfInterestClicked(View view) {
        Intent intent = new Intent(this, PointsOfInterestMapsActivity.class);
        startActivityForResult(intent, REQUEST_MAP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            getFilenames(directory);
            createPicture(currentPhotoPath);
            imageView.setImageBitmap(bitmap);
            currentIndex = filenames.length - 1;
            //getPhotoLocation();
            setLocation(currentPhotoPath);
            Log.d("onActivityResult", "Finished request image capture");
        }
    }

    private void setLocation(String fileName) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double currentLongitude = location.getLongitude();
            double currentLatitude = location.getLatitude();

            try {
                File currentFile = new File(currentPhotoPath);
                ExifInterface exif = new ExifInterface(currentFile.getAbsolutePath());

                double latitude = Math.abs(currentLatitude);
                double longitude = Math.abs(currentLongitude);

                int num1Lat = (int) Math.floor(latitude);
                int num2Lat = (int) Math.floor((latitude - num1Lat) * 60);
                double num3Lat = (latitude - ((double) num1Lat + ((double) num2Lat / 60))) * 3600000;

                int num1Lon = (int) Math.floor(longitude);
                int num2Lon = (int) Math.floor((longitude - num1Lon) * 60);
                double num3Lon = (longitude - ((double) num1Lon + ((double) num2Lon / 60))) * 3600000;

                String lat = num1Lat + "/1," + num2Lat + "/1," + num3Lat + "/1000";
                String lon = num1Lon + "/1," + num2Lon + "/1," + num3Lon + "/1000";

                if (currentLatitude > 0) {
                    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
                } else {
                    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
                }

                if (currentLongitude > 0) {
                    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
                } else {
                    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
                }

                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, lat);
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, lon);

                exif.saveAttributes();

            } catch (IOException e) {
                Log.e("PictureActivity", e.getLocalizedMessage());
            }

            return;
        }
    }

    private String getCurrentFilePath() {
        return directory + filenames[currentIndex];
    }

    private void getFilenames(String directory){
        File path = new File(directory);
        if (path.exists()) {
            filenames = path.list();
            Log.d("getFileNames", "filenames length = " + filenames.length);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void createPicture(String filepath) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        bitmap = BitmapFactory.decodeFile(filepath, bmOptions);
        updateImageIndexText();
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        return gestureScanner.onTouchEvent(me);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float v, float v1) {

        // Get swipe delta value in x axis.
        float deltaX = e1.getX() - e2.getX();

        // Get swipe delta value in y axis.
        float deltaY = e1.getY() - e2.getY();

        // Get absolute value.
        float deltaXAbs = Math.abs(deltaX);
        float deltaYAbs = Math.abs(deltaY);

        Log.d("Fling, deltaX = ", Float.toString(deltaX));
        Log.d("Fling, deltaY = ", Float.toString(deltaY));
        Log.d("Fling, deltaXAbs = ", Float.toString(deltaXAbs));
        Log.d("Fling, deltaYAbs = ", Float.toString(deltaYAbs));
        if ((deltaXAbs >= MIN_FLING_DISTANCE) && (deltaXAbs <= MAX_FLING_DISTANCE)) {
            if (deltaX > 0) {
                // left swipe - so scrolling to the right
                Log.d("Fling, SWIPE LEFT","!");
                scrollGallery(NAVIGATE_RIGHT); // scroll right
            }
            else {
                // right swipe - so scrolling to the left
                Log.d("Fling, SWIPE RIGHT","!");
                scrollGallery(NAVIGATE_LEFT);  // scroll left
            }
        }
        return true;
    }

    // direction parameter should be an enum
    private void scrollGallery(int direction) {
        switch (direction) {
            case NAVIGATE_LEFT:     // left
                Log.d("scrollGallery :", "Scroll Left");
                --currentIndex;
                break;
            case NAVIGATE_RIGHT:    // right
                Log.d("scrollGallery :", "Scroll Right");
                ++currentIndex;
                break;
            default:
                break;
        }

        // stay in bounds
        if (currentIndex < 0) {
            currentIndex = 0;
        }
        if (filenames.length > 0 && currentIndex >= filenames.length) {
            currentIndex = filenames.length - 1;
        }

        // update the gallery image
        currentPhotoPath = directory + filenames[currentIndex];
        Log.d("scrollGallery :", "currentIndex = " + currentIndex + " filenames.length = " + filenames.length);
        Log.d("scrollGallery :", "currentPhotoPath = " + currentPhotoPath);
        createPicture(currentPhotoPath);
        imageView.setImageBitmap(bitmap);
    }

   /* private void getPhotoLocation() {

        float location[] = {0.0f, 0.0f} ;   // lat, long

        File currentFile = new File(currentPhotoPath);
        if (ExifUtility.getExifLatLong(currentFile, location)) {
            String city = "";
            float latitude = location[0];
            float longitude = location[1];
            Log.d("getPhotoLocation", "File location: lat: " + latitude + " long: " + longitude);

            Geocoder geo = new Geocoder(this);
            try {
                List<Address> addressList = geo.getFromLocation(latitude, longitude, 1);
                for (Address addr : addressList) {
                    city = addr.getLocality();
                    Log.d("getPhotoLocation", "addr: " + addr.getLocality());
                }
            } catch (IOException e) {
                Log.d("getPhotoLocation", "geo IOException " + e.getMessage());
            }
        }
        else {
            Log.d("getPhotoLocation", "File location: not retrieved");
        }

    }*/

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged","@@@ Location: lat[" + location.getLatitude()+ "] long[" + location.getLongitude()+ "]");

        // experimental: get the location name from a gps location
        Geocoder geo = new Geocoder(this);
        String city= "Vancouver BC";
        try {
            List<Address> addressList = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            for (Address addr : addressList) {
                Log.d("onLocationChanged", "addr1: " + addr.getLocality());
                city = addr.getLocality();
            }

            // experimental: get the gps location from a location name
            city= "10 downing st london";
            if (city == null) {
                Log.d("onLocationChanged", "TESTNOW: " + (city == null ? "is null" : city));

            }
            else {
                Log.d("onLocationChanged", "TESTNOW: " + (city == null ? "is null" : city));
                addressList = geo.getFromLocationName(city, 4);
                for (Address addr : addressList) {
                    Log.d("onLocationChanged", "By locationname: " +
                            addr.getCountryName() + "\n" +
                            addr.getLocality() + "\n" +
                            addr.getSubLocality() + "\n" +
                            addr.getThoroughfare() + "\n" +
                            addr.getSubThoroughfare() + "\n" +
                            addr.getPostalCode() + "\n\n" +
                            addr.getLatitude() + " " + addr.getLongitude()
                    );
                }
            }
        } catch (IOException e) {
            Log.d("onLocationChanged", "geo IOException " + e.getMessage());
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return;
    }

    private void enableLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 1, this);
            //cachedLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.d("enableLocationUpdates","Begin accepting location updates");
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            Log.d("enableLocationUpdates","Request FINE location permission");
        }
    }

    private void disableLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            locationManager.removeUpdates(this);
            Log.d("disableLocationUpdates","End accepting location updates");
        }
    }
}
