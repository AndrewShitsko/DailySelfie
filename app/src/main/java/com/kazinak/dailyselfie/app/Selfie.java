package com.kazinak.dailyselfie.app;

import android.graphics.Bitmap;

public class Selfie {
    private Bitmap bitmapPhoto;
    private String photoPath;
    private String title;

    public Selfie(Bitmap bitmapPhoto, String photoPath, String title) {
        this.bitmapPhoto = bitmapPhoto;
        this.photoPath = photoPath;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getPhoto() {
        return bitmapPhoto;
    }

    public String getPhotoPath() { return photoPath; }
}
