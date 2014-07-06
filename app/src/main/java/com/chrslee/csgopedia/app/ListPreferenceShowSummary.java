package com.chrslee.csgopedia.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
                // TODO: Check if user picked a different theme. Otherwise, do not prompt for restart.
                if (arg0.getKey().equals("theme")) {
                    final String themeName = arg1.toString();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Changing the theme will restart the app. Are you sure?")
                            .setTitle("Restart notice");

                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            // Go to MainActivity, and kill the app.
                            // http://stackoverflow.com/a/13173279
                            SharedPreferences.Editor edit = prefs.edit();
                            edit.putString("theme", themeName);
                            edit.commit();
                            context.startActivity(new Intent(context, MainActivity.class));
                            System.exit(0);
                        }
                    });

                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    arg0.setSummary(getEntry());
                    // If ok is picked, app restarts and nothing is returned. Therefore, we must
                    // manually save the new value into SharedPreferences.
                    // True: Save selected value.  False: Ignore selected value.
                    return false;
                }
                return true;
            }
        });
    }

    @Override
    public CharSequence getSummary() {
        return super.getEntry();
    }
}