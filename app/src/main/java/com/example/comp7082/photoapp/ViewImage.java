package com.example.comp7082.photoapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;

public class ViewImage extends AppCompatActivity {

    private Intent intent;
    private String fileName;
    private ImageView imageView;
    public Bitmap bitmap;
    private int width;
    private int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        imageView = findViewById(R.id.imageView3);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics( dm );

        width = dm.widthPixels;
        height = dm.heightPixels;
        getWindow().setLayout( (int)(width *.8),(int)(height* .7) );
        intent = getIntent();
        fileName = intent.getStringExtra( "FileName" );
        setPhoto();
    }

    private void setPhoto(){
        if(fileName != null){
            createPicture(fileName);
            imageView.setImageBitmap(bitmap);
        }
    }

    public void createPicture(String filepath) {
        // Get the dimensions of the View
        int targetW = (int)(width *.8);
        int targetH = (int)(height* .7);

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
    }
}
