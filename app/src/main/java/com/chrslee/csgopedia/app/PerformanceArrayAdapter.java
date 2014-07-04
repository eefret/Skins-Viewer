package com.chrslee.csgopedia.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chrslee.csgopedia.app.util.Item;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * ViewHolder pattern to improve ListView scrolling performance.
 */
public class PerformanceArrayAdapter extends ArrayAdapter<Item> {
    private Activity context;
    private final List<Item> data;
    private HashMap<Integer, String> priceCache;
    private boolean isAutoDetected;
    private String symbol;

    public PerformanceArrayAdapter(Activity context, List<Item> data) {
        super(context, R.layout.item_view, data);
        this.context = context;
        this.data = data;
        priceCache = new HashMap<Integer, String>();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        isAutoDetected = sharedPrefs.getBoolean("auto_detect_locale", true);
        symbol = sharedPrefs.getString("custom_currency", "USD");
    }

    class ViewHolder {
        ImageView icon;
        TextView name;
        TextView price;
        TextView description;
        TextView marketPrice;
        int position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_view, null);

            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.item_icon);
            viewHolder.name = (TextView) convertView.findViewById(R.id.item_name);
            viewHolder.price = (TextView) convertView.findViewById(R.id.item_price);
            viewHolder.description = (TextView) convertView.findViewById(R.id.item_description);
            viewHolder.marketPrice = (TextView) convertView.findViewById(R.id.market_price);

            convertView.setTag(viewHolder);
        }

        Item currentItem = data.get(position);
        viewHolder = (ViewHolder) convertView.getTag();

        // Clear out market price field so it doesn't reappear for the next item when scrolling
        viewHolder.marketPrice.setText("");
        viewHolder.marketPrice.setTextColor(Color.rgb(112, 176, 74)); // green
        viewHolder.icon.setImageBitmap(
                BitmapResizer.decodeSampledBitmapFromResource(context.getResources(), currentItem.getIconID(), 90, 90));
        viewHolder.name.setText(currentItem.getItemName());

        String rarity = currentItem.getPrice();
        viewHolder.price.setText(currentItem.getPrice());

        // Assign rarity colors
        if (rarity != null) {
            if (rarity.equals("Rare")) {
                viewHolder.price.setTextColor(Color.rgb(255, 215, 0)); // yellow
            } else if (rarity.equals("Consumer")) {
                viewHolder.price.setTextColor(Color.rgb(181,181,181)); // grey
            } else if (rarity.equals("Industrial")) {
                viewHolder.price.setTextColor(Color.rgb(176, 195, 217)); // light blue
            } else if (rarity.equals("Mil-Spec")) {
                viewHolder.price.setTextColor(Color.rgb(75, 105, 255)); // blue
            } else if (rarity.equals("Restricted")) {
                viewHolder.price.setTextColor(Color.rgb(136, 71, 255)); // purple
            } else if (rarity.equals("Classified")) {
                viewHolder.price.setTextColor(Color.rgb(211, 44, 230)); // pinkish-purple
            } else if (rarity.equals("Covert")) {
                viewHolder.price.setTextColor(Color.rgb(235, 75, 75)); // red
            } else if (rarity.equals("Contraband")) {
                viewHolder.price.setTextColor(Color.rgb(255, 165, 0)); // light orange
            }
        }

        viewHolder.description.setText(currentItem.getDescription());
        viewHolder.position = position;

        String weaponName = currentItem.getWeaponName();
        String cachedLowest = priceCache.get(position);

        if (viewHolder.description.getText().toString().length() > 0 && viewHolder.name.getText().toString().length() > 0) {
            if (cachedLowest != null) {
                viewHolder.marketPrice.setText(cachedLowest);
            } else {
                ConnectionDetector cd = new ConnectionDetector(context);
                if (cd.isConnectedToInternet()) {
                    new ScraperAsyncTask(position, viewHolder, weaponName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void)null);
                }
            }
        }

        return convertView;
    }

    /**
     * Do scraping in separate thread so main UI doesn't freeze up.
     * http://jmsliu.com/1431/download-images-by-asynctask-in-listview-android-example.html
     *
     * Also keep track of position since View recycling can occur at the same time (scrolling)
     * http://stackoverflow.com/questions/11695850/android-listview-updating-of-image-thumbnails-using-asynctask-causes-view-recycl
     */
    private class ScraperAsyncTask extends AsyncTask<Void, String, Double> {
        private ViewHolder mHolder;
        private String mWeaponName;
        private int mPosition;
        private double rate;
        private int precision;

        public ScraperAsyncTask(int position, ViewHolder holder, String weaponName) {
            mPosition = position;
            mHolder = holder;
            mWeaponName = weaponName;
        }

        @Override
        protected Double doInBackground(Void... params) {
            // If auto-detect locale enabled, get local currency. Otherwise, get custom currency.
            // TODO: This block of code is inefficient. Needs to run only once.
            if (isAutoDetected) {
                rate = new CurrencyRates(context).getRate(Currency.getInstance(Locale.getDefault()).toString());
            } else {
                rate = new CurrencyRates(context).getRate(symbol);

                // Some currencies don't have "cents"
                Currency cur = Currency.getInstance(symbol);
                precision = cur.getDefaultFractionDigits();
            }

            String skinName = mHolder.name.getText().toString();
            // Strip out knives that have default skins so scraper searches correctly.
            if (skinName.equals("Regular")) {
                skinName = "";
            }
            publishProgress("Fetching price...");

            return LowestPriceScraper.getLowestPrice(mWeaponName + "+\"" + skinName + "\"") * rate;
         }

        @Override
        protected void onPostExecute(Double lowestPrice) {
            if (mHolder.position == mPosition) {
                String output;
                // Format price according to device locale
                if(lowestPrice > 0.0) {
                    if (isAutoDetected) {
                        output = "Starting at: " + NumberFormat.getCurrencyInstance().format(lowestPrice);
                    } else {
                        BigDecimal bd = new BigDecimal(lowestPrice);
                        output = "Starting at: " + formatCurrencyValue(bd, Currency.getInstance(symbol)) + " " + symbol;
                    }
                } else {
                    output = "None currently listed";
                }
                priceCache.put(mPosition, output);
                mHolder.marketPrice.setText(output);
            }
        }

        @Override
        protected void onProgressUpdate(String... params) {
            mHolder.marketPrice.setText(params[0]);
            // Sometimes when fast scrolling, "Fetching price" will appear green.
            mHolder.marketPrice.setTextColor(Color.rgb(112, 176, 74));
        }
    }

    // https://www.codota.com/android/scenarios/52fcbcbbda0a2bf89c4a97c7/java.util.Currency?tag=dragonfly&fullSource=1
    public String formatCurrencyValue(BigDecimal amount, Currency currency) {
        NumberFormat currencyFormatter = NumberFormat.getInstance();
        if (currencyFormatter == null) {
            currencyFormatter = NumberFormat.getInstance();
        }
        currencyFormatter.setGroupingUsed(false);
        currencyFormatter.setMaximumFractionDigits(currency
                .getDefaultFractionDigits());
        return currencyFormatter
                .format(amount.setScale(currency.getDefaultFractionDigits(),
                        BigDecimal.ROUND_HALF_EVEN));
    }
}
