package com.kazinak.dailyselfie.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImageUtils {
    private static final String ALBUM_NAME = "DailySelfie";
    private static final String CAMERA_DIR = "/DCIM/";
    private static final String IMAGE_PREFIX = "SELFIE_";
    private static final String IMAGE_SUFFIX = ".jpeg";

    private final int PHOTO_SIZE_DEFAULT = 150;

    private static String mCurrentPhotoPath;

    public File getAlbumStorageDir(String albumName) {
        File file = new File(Environment.getExternalStorageDirectory() + CAMERA_DIR + albumName);
        if (!file.exists()) {
            if (file.mkdir()) {
                return file;
            }
        }
        return file;
    }

    public File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = getAlbumStorageDir(ALBUM_NAME);

            if (storageDir == null) {
                Log.v(ALBUM_NAME, "Album not found!");
                return null;
            }

        } else {
            Log.v(ALBUM_NAME, "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    public List<File> getPhotoList(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".jpeg")) {
                inFiles.add(file);
            }
        }
        return inFiles;
    }

    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = IMAGE_PREFIX + timeStamp;
        File albumF = getAlbumDir();
        File imageF = new File(albumF, imageFileName + IMAGE_SUFFIX);
        return imageF;
    }

    public boolean removeImageFile(String photoPath) {
        File file = new File(photoPath);
        return file.delete();
    }

    public Bitmap resizeBitmap(String photoPath) {
        int targetW = PHOTO_SIZE_DEFAULT;
        int targetH = PHOTO_SIZE_DEFAULT;

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }
}
