package com.kazinak.dailyselfie.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

public class CurrentSelfieActivity extends ActionBarActivity {

    private ImageView currentSelfieView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_selfie);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        Intent currentSelfieIntent = getIntent();
        String photoPath = currentSelfieIntent.getStringExtra("photoPath");

        currentSelfieView = (ImageView) findViewById(R.id.currentSelfie);
        Bitmap photo = BitmapFactory.decodeFile(photoPath);
        currentSelfieView.setImageBitmap(photo);
    }
}
