package com.marvinsyan.csgoskinsviewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

/**
 * http://stackoverflow.com/a/8004498
 * This class allows a ListPreference selection to be displayed in the summary field.
 */
public class ListPreferenceShowSummary extends ListPreference {

    //private final static String TAG = ListPreferenceShowSummary.class.getName();
    private Context context;
    private SharedPreferences prefs;

    public ListPreferenceShowSummary(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public ListPreferenceShowSummary(Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference arg0, Object arg1) {

                return true;
            }
        });
    }

    @Override
    public CharSequence getSummary() {
        return super.getEntry();
    }
}