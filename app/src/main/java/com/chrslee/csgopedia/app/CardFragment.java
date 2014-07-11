/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chrslee.csgopedia.app;

/**
 * Used to display the image and price sections.
 *
 * TODO: Image gets left-aligned when screen orientation is landscape.
 * Temporary solution - ImageAndPriceActivity is always shown in portrait mode.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;

public class CardFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private int position;
    private SharedPreferences sharedPrefs;
    private Context context;

    public static CardFragment newInstance(int position) {
        CardFragment f = new CardFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
        context = getActivity();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        FrameLayout fl = new FrameLayout(getActivity());
        fl.setLayoutParams(params);

        // First - image section
        LayoutParams imgParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imgParams.gravity = Gravity.CENTER;
        ImageView iv = new ImageView(getActivity());
        iv.setLayoutParams(imgParams);
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        iv.setAdjustViewBounds(true);
        iv.setBackgroundResource(getActivity().getIntent().getExtras().getInt("iconID"));

        // Second - prices section
        //final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
        //        .getDisplayMetrics());

        LinearLayout linLayout = new LinearLayout(getActivity());
        linLayout.setOrientation(LinearLayout.VERTICAL);
        linLayout.setLayoutParams(params);
        linLayout.setGravity(Gravity.CENTER);

        // Get prices
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        String query = getActivity().getIntent().getStringExtra("searchQuery");
        if (cd.isConnectedToInternet()) {
            if (query.equals("-1")) {
                TextView t = new TextView(getActivity());
                t.setText("Regular skins are not for sale!");
                linLayout.addView(t);
            } else {
                new ScraperAsyncTask(linLayout).execute(query);
            }
        } else {
            TextView t = new TextView(getActivity());
            t.setText("Please connect to the Internet to view prices.");
            linLayout.addView(t);
        }

        if (position == 0) {
            fl.addView(iv);
        } else {
            fl.addView(linLayout);
        }
        return fl;
    }

    private enum Quality {
        ST_FACTORY_NEW("ST Factory New"),
        ST_MINIMAL_WEAR("ST Minimal Wear"),
        ST_FIELD_TESTED("ST Field-Tested"),
        ST_WELL_WORN("ST Well-Worn"),
        ST_BATTLE_SCARRED("ST Battle-Scarred"),
        FACTORY_NEW("Factory New"),
        MINIMAL_WEAR("Minimal Wear"),
        FIELD_TESTED("Field-Tested"),
        WELL_WORN("Well-Worn"),
        BATTLE_SCARRED("Battle-Scarred");

        private String displayName;

        Quality(String displayName) {
            this.displayName = displayName;
        }

        public String displayName() {
            return displayName;
        }
    }

    /**
     * Do scraping in separate thread so main UI doesn't freeze up.
     * This task gets the starting prices for all qualities of a skin.
     */
    private class ScraperAsyncTask extends AsyncTask<String, Void, HashMap<String, Double>> {
        LinearLayout l;
        double rate;
        boolean isAutoDetected;
        String symbol;
        protected ScraperAsyncTask(LinearLayout linLayout) {
            l = linLayout;
        }

        @Override
        protected HashMap<String, Double> doInBackground(String... params) {
            publishProgress();
            isAutoDetected = sharedPrefs.getBoolean("auto_detect_locale", true);
            symbol = sharedPrefs.getString("custom_currency", "USD");

            // Auto locale detection on/off
            if (isAutoDetected) {
                rate = new CurrencyRates(context).getRate(Currency.getInstance(Locale.getDefault()).toString());
            } else {
                rate = new CurrencyRates(context).getRate(symbol);
            }

            return PriceScraper.getPrices(params[0]);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            TextView t = new TextView(getActivity());
            t.setText("Loading prices...");
            t.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            l.addView(t);
        }

        @Override
        protected void onPostExecute(HashMap<String, Double> priceData) {
            l.removeAllViewsInLayout();

            TextView title = new TextView(getActivity());
            title.setText("- Prices (beta) -");
            title.setGravity(Gravity.CENTER_HORIZONTAL);
            l.addView(title);

            // See SpannableStrings if multi-colored text desired in the text
            // (e.g. StatTrak orange, prices green)
            for (Quality q : Quality.values()) {
                TextView price = new TextView(getActivity());
                price.setGravity(Gravity.CENTER_HORIZONTAL);

                if (priceData.containsKey(q.displayName())) {
                    if (isAutoDetected) {
                        price.setText(q.displayName()
                                + ": "
                                + NumberFormat.getCurrencyInstance()
                                .format(priceData.get(q.displayName()) * rate));
                    } else {
                        price.setText(q.displayName() + ": "
                                + CurrencyFormatter.formatCurrencyValue(
                                new BigDecimal(priceData.get(q.displayName()) * rate),
                                Currency.getInstance(symbol))
                                + " " + symbol);
                    }
                } else {
                    price.setText(q.displayName() + ": Not Available");
                }

                l.addView(price);
            }
        }
    }
}
