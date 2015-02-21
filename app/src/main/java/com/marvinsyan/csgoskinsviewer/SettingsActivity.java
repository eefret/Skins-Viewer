package com.marvinsyan.csgoskinsviewer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Light/Dark theme
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLightTheme = prefs.getString("theme", "light").equals("light");
        if (isLightTheme) {
            setTheme(R.style.AppTheme_Base_Light);
        } else {
            setTheme(R.style.AppTheme_Base_Dark);
        }
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}