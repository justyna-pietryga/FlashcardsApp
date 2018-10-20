package com.example.justyna.flashcards.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.example.justyna.flashcards.R;

public class MyPreferenceFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        setSummary("orderType", 0);
        setSummary("displayType",1);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("orderType")) {
            Preference connectionPref = findPreference(key);
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
        }

        if (key.equals("displayType")) {
            Preference connectionPref = findPreference(key);
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
        }
    }

    public void setSummary(String key, int i){
        Preference preference = getPreferenceScreen().getPreference(i);
        SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();
        preference.setSummary(sharedPrefs.getString(key, ""));
    }
}
