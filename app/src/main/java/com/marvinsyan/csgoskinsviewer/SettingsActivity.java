package com.marvinsyan.csgoskinsviewer;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.chrslee.csgopedia.app.R;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}