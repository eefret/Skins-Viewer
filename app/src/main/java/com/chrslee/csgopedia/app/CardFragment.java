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
        //Taking the params in the constructor and passing them as args
        //This is required while instantiating fragments because of the workflow
        //basically fragments mimic a part of the activity workflow but can be instantiated
        //instead of called by intents this merges both functionalities
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
        context = getActivity();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        //settings args passed from constructor as global fields
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Looks like he did this layout via code this is a layoutparams object
        //it defines the metrics and bounds of a specific view
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        //Creating a FrameLayout a FrameLayout is basically a empty container
        FrameLayout fl = new FrameLayout(context);
        fl.setLayoutParams(params);
        //setting the params from above

        // First - image section
        //Building the imageview via code
        LayoutParams imgParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imgParams.gravity = Gravity.CENTER;
        //setting all centered
        ImageView iv = new ImageView(context);
        iv.setLayoutParams(imgParams);
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        iv.setAdjustViewBounds(true);
        iv.setBackgroundResource(getActivity().getIntent().getExtras().getInt("iconID"));
        //seting the iconID as the image of this imageView

        // Second - prices section
        //final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
        //        .getDisplayMetrics());
        //

        //LinearLayout is another container it just take the
        //widgets inside it and display them in a linear order
        LinearLayout linLayout = new LinearLayout(context);
        linLayout.setOrientation(LinearLayout.VERTICAL);
        linLayout.setLayoutParams(params);
        linLayout.setGravity(Gravity.CENTER);

        // Get prices
        ConnectionDetector cd = new ConnectionDetector(context); //creating a connection detector to check for internet
        String query = getActivity().getIntent().getStringExtra("searchQuery"); //taking the name or whatever he pass from mainactivity from the searchquery arg
        TextView placeholder = new TextView(context); ///this is a textview for the name of the item
        if (cd.isConnectedToInternet()) {//if its connected to internet then
            if (query.equals("-1")) {//if the skin is regular he display is not for sale
                placeholder.setText("Regular skins are not for sale!");
                linLayout.addView(placeholder);
            } else {//else he calls the scrappperAsyncTask with the query
                new ScraperAsyncTask(linLayout).execute(query);
            }
        } else {//if its not connected he display the message here
            placeholder.setText("Please connect to the Internet to view prices.");
            linLayout.addView(placeholder);
        }

        //here he defines which tab he is displaying, Now I see why he used the same fragment,
        //this is a bad practice, he created both widgets before but just displays one of them
        //for each case if its the first tab he display the image if its the second the prices
        if (position == 0) {
            fl.addView(iv);
        } else {
            fl.addView(linLayout);
        }

        //Then he returns the framelayout (container) to display it in the screen
        return fl;
    }

    public enum Quality {
        //Enum with the quality, what a silly he just had to take the Enum.toString() and
        //replace the _ with spaces and do a camelCase parsing, instead of hardcoding the
        //same enum name as string -_-
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
        //Will explain what an async task is, network request cant be done in the UI thread
        //(the main one) so we need to execute those requests in a separate thread
        //that is async task for, thus is not a good practice to use them for this because
        //asynctask live inside the activity lifecycle so if the activity goes onpause
        //while the asynctask is executing it will die and start again -_-
        LinearLayout l;
        double rate;
        boolean isAutoDetected;
        String symbol;
        TextView loading;

        protected ScraperAsyncTask(LinearLayout linLayout) {
            //Constructor for the asyncTask takes the parent linear layout as parameter to
            //set a loading... message in the meantime the request is executing
            l = linLayout;
            loading = new TextView(context);
            loading.setText("Loading...");
            loading.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            l.addView(loading);
        }

        @Override
        protected HashMap<String, Double> doInBackground(String... params) {
            isAutoDetected = sharedPrefs.getBoolean("auto_detect_locale", true);
            symbol = sharedPrefs.getString("custom_currency", "USD");
            //getting a boolean and a string from prefs

            // Auto locale detection on/off
            if (isAutoDetected) {
                rate = new CurrencyRates(context).getRate(Currency.getInstance(Locale.getDefault()).toString());
            } else {
                rate = new CurrencyRates(context).getRate(symbol);
            }
            //Executing the PriceScrapper and returning the hashmap it returns
            return PriceScraper.getPrices(params[0]);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        @Override
        protected void onPostExecute(HashMap<String, Double> priceData) {
            //this executes after the scrapper has finished and returns his data
            l.removeAllViewsInLayout();//removes all view from the linear container he passed
            ///in the params of the asynctask
            //this also remove the loading... message

            // See SpannableStrings if multi-colored text desired in the text
            // (e.g. StatTrak orange, prices green)

            //Now displays all  prices and name with the color of their rarity in the
            //linear passed in the params
            for (Quality q : Quality.values()) {//iterating through prices to display them
                TextView price = new TextView(context);
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
