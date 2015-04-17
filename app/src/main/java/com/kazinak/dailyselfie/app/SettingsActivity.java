package com.kazinak.dailyselfie.app;

import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.*;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    private static final String RINGTONE_KEY = "ringtone_pref";
    private static final String NOTIFICATION_KEY = "notification_pref";
    private static final String FREQUENCY_REMINDER_KEY = "frequency_reminder_pref";
    private static final String VIBRATE_KEY = "vibrate_pref";

    private static final String DEFAULT_RINGTONE_URI = Settings.System.DEFAULT_NOTIFICATION_URI.toString();

    private RingtonePreference ringtonePref;
    private CheckBoxPreference notificationPref;
    private CheckBoxPreference vibratePref;
    private ListPreference frequencyReminderPref;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        ringtonePref = (RingtonePreference) findPreference(RINGTONE_KEY);
        notificationPref = (CheckBoxPreference) findPreference(NOTIFICATION_KEY);
        frequencyReminderPref = (ListPreference) findPreference(FREQUENCY_REMINDER_KEY);
        vibratePref = (CheckBoxPreference) findPreference(VIBRATE_KEY);

        ringtonePref.setOnPreferenceChangeListener(this);
        notificationPref.setOnPreferenceChangeListener(this);
        frequencyReminderPref.setOnPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        additionalSettings(prefs, ringtonePref);
        additionalSettings(prefs, notificationPref);
        additionalSettings(prefs, frequencyReminderPref);
    }

    private void additionalSettings(SharedPreferences prefs, Preference settingsPref) {
        if (settingsPref instanceof RingtonePreference) {
            String uri = prefs.getString("ringtone_pref", DEFAULT_RINGTONE_URI);
            Ringtone ringtone = RingtoneManager.getRingtone(this, Uri.parse(uri));
            settingsPref.setSummary(ringtone.getTitle(this));
        } else if (settingsPref instanceof CheckBoxPreference) {
            boolean state = prefs.getBoolean("notification_pref", false);
            if (state) {
                frequencyReminderPref.setEnabled(true);
                vibratePref.setEnabled(true);
                ringtonePref.setEnabled(true);
            }
        } else if (settingsPref instanceof ListPreference) {
            if (settingsPref == null) {
                settingsPref.setDefaultValue(0);
            }
            String itemValue = prefs.getString("frequency_reminder_pref", null);
            if (itemValue != null) {
                int indexOfValue = frequencyReminderPref.findIndexOfValue(itemValue);
                CharSequence[] entries = frequencyReminderPref.getEntries();
                frequencyReminderPref.setSummary(entries[indexOfValue]);
            }
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        ViewGroup contentView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, new LinearLayout(this), false);
        toolbar = (Toolbar) contentView.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ViewGroup contentWrapper = (ViewGroup) contentView.findViewById(R.id.content_wrapper);
        LayoutInflater.from(this).inflate(layoutResID, contentWrapper, true);

        getWindow().setContentView(contentView);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if (key.equals(NOTIFICATION_KEY)) {
            if ((Boolean) newValue) {
                frequencyReminderPref.setEnabled(true);
                vibratePref.setEnabled(true);
                ringtonePref.setEnabled(true);
            } else {
                frequencyReminderPref.setEnabled(false);
                vibratePref.setEnabled(false);
                ringtonePref.setEnabled(false);
            }
        } else if (key.equals(FREQUENCY_REMINDER_KEY)) {
            int index = frequencyReminderPref.findIndexOfValue((String) newValue);
            CharSequence item = frequencyReminderPref.getEntries()[index];
            frequencyReminderPref.setSummary(item);
        } else if (key.equals(RINGTONE_KEY)) {
            Ringtone ringtone = RingtoneManager.getRingtone(getBaseContext(), Uri.parse((String) newValue));
            if (ringtone != null) {
                ringtonePref.setSummary(ringtone.getTitle(getBaseContext()));
            } else {
                ringtonePref.setSummary(R.string.settings_ringtone_summary);
            }
        }
        return true;
    }
}
