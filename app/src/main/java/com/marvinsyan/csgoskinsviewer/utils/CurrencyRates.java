package com.marvinsyan.csgoskinsviewer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.net.URL;
import java.util.Date;
import java.util.Scanner;

/**
 * Returns the conversion rate from USD.
 * Runs once every 24 hours and can be changed in settings.
 */
public class CurrencyRates {
    private static double rate;
    private SharedPreferences prefs;
    private String source;
    private long lastUpdated;
    private static final long MS_IN_HOUR = 3600000;
    private final String RATES_PREF_NAME = "rates_in_JSON";
    private final String UPDATED_PREF_NAME = "last_updated";

    public CurrencyRates(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        source = prefs.getString(RATES_PREF_NAME, "");
        lastUpdated = prefs.getLong(UPDATED_PREF_NAME, 0);
    }

    public double getRate(String abbrv) {
        try {
            // http://stackoverflow.com/a/8610695
            if (abbrv.equals("USD")) {
                rate = 1;
            } else {
                updateRates();
                JsonNode rootNode = new ObjectMapper().readTree(source);
                rate = rootNode.get("rates").get(abbrv).asDouble();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rate;
    }

    private void updateRates() {
        long millisNow = new Date().getTime(); // Current time in milliseconds

        // If changed or 1 hour elapsed or if app's first launch, update rates
        if (millisNow - lastUpdated > MS_IN_HOUR) {
            try {
                URL url = new URL("http://api.fixer.io/latest?base=USD");
                Scanner scan = new Scanner(url.openStream());

                while (scan.hasNext()) {
                    source += scan.nextLine();
                }
                scan.close();

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(RATES_PREF_NAME, source);
                editor.putLong(UPDATED_PREF_NAME, millisNow);
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}