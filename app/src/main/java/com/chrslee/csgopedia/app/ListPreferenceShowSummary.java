package com.chrslee.csgopedia.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.Toast;

/**
 * http://stackoverflow.com/a/8004498
 * This class allows a ListPreference selection to be displayed in the summary field.
 */
public class ListPreferenceShowSummary extends ListPreference {

    //private final static String TAG = ListPreferenceShowSummary.class.getName();
    private Context context;

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
        setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference arg0, Object arg1) {
                if (arg0.getKey().equals("theme")) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("changedTheme", false);
                    editor.commit();
                    Toast.makeText(context, "Please restart the app for changes to take effect.", Toast.LENGTH_LONG).show();
                }
                arg0.setSummary(getEntry());
                return true;
            }
        });
    }

    @Override
    public CharSequence getSummary() {
        return super.getEntry();
    }
}