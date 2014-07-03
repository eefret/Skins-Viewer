package com.chrslee.csgopedia.app;

import android.content.Context;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.AttributeSet;

/**
 * http://stackoverflow.com/a/8004498
 * This class allows a ListPreference selection to be displayed in the summary field.
 */
public class ListPreferenceShowSummary extends ListPreference {

    private final static String TAG = ListPreferenceShowSummary.class.getName();

    public ListPreferenceShowSummary(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ListPreferenceShowSummary(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference arg0, Object arg1) {
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