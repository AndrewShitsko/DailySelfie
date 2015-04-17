package com.kazinak.dailyselfie.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final String DEFAULT_RINGTONE_URI = Settings.System.DEFAULT_NOTIFICATION_URI.toString();

    private boolean notificationPreference;
    private String frequencyReminderPreference;
    private boolean vibratePreference;
    private String ringtonePreference;

    private RecyclerView recyclerView;
    private SelfieAdapter adapter;
    private ImageUtils imageUtils;
    private PendingIntent alarmPendingIntent;

    List<Selfie> selfies = Collections.emptyList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageUtils = new ImageUtils();

        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new SelfieAdapter(this, getData());
        adapter.setOnItemClickListener(new SelfieAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Selfie selfie = selfies.get(position);
                Intent currentSelfieIntent = new Intent(v.getContext(), CurrentSelfieActivity.class);
                currentSelfieIntent.putExtra("photoPath", selfie.getPhotoPath());
                startActivity(currentSelfieIntent);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPrefs();
    }

    public void startAlarm(int freqRemindInMillis) {
        Long time = new GregorianCalendar().getTimeInMillis() + freqRemindInMillis;

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("vibrate", vibratePreference);
        alarmIntent.putExtra("ringtone", ringtonePreference);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmPendingIntent = PendingIntent.getBroadcast(this, 1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, alarmPendingIntent);
    }

    public List<Selfie> getData() {
        List<File> files = imageUtils.getPhotoList(imageUtils.getAlbumDir());
        if (files.isEmpty())
            return new ArrayList<Selfie>();

        selfies = new ArrayList<Selfie>();

        for (int i = 0; i < files.size(); i++) {
            String photoPath = files.get(i).getAbsolutePath();
            Bitmap smallCurrentPhoto = imageUtils.resizeBitmap(photoPath);
            String currentTitle = files.get(i).getName().substring(0, files.get(i).getName().indexOf("."));
            Selfie selfie = new Selfie(smallCurrentPhoto, photoPath, currentTitle);
            selfies.add(selfie);
        }

        return selfies;
    }

    private void getPrefs() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        notificationPreference = prefs.getBoolean("notification_pref", false);
        if (notificationPreference) {
            frequencyReminderPreference = prefs.getString("frequency_reminder_pref", null);
            vibratePreference = prefs.getBoolean("vibrate_pref", false);
            ringtonePreference = prefs.getString("ringtone_pref", DEFAULT_RINGTONE_URI);
        } else {
            setDefaultValuesOfPrefs();
        }
    }

    private void setDefaultValuesOfPrefs() {
        frequencyReminderPreference = null;
        vibratePreference = false;
        ringtonePreference = DEFAULT_RINGTONE_URI;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = imageUtils.createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "File is not created!", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.takePicture) {
            dispatchTakePictureIntent();
            return true;
        } else if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            adapter.updateList(getData());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (notificationPreference && frequencyReminderPreference != null) {
                int frequency = Integer.parseInt(frequencyReminderPreference);
                startAlarm(frequency);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
