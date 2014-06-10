package com.chrslee.csgopedia.app;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chrslee.csgopedia.app.util.Item;

import java.util.HashMap;
import java.util.List;

/**
 * ViewHolder pattern to improve ListView scrolling performance.
 */
public class PerformanceArrayAdapter extends ArrayAdapter<Item> {
    private final Activity context;
    private final List<Item> data;
    private static HashMap<Integer, String> priceCache;

    static class ViewHolder {
        ImageView icon;
        TextView name;
        TextView price;
        TextView description;
        TextView marketPrice;
        int position;
    }

    public PerformanceArrayAdapter(Activity context, List<Item> data) {
        super(context, R.layout.item_view, data);
        this.context = context;
        this.data = data;
        priceCache = new HashMap<Integer, String>();
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

        // Clear out price field so it doesn't reappear for the next item when scrolling
        viewHolder.marketPrice.setText("");

        viewHolder.icon.setImageBitmap(
                decodeSampledBitmapFromResource(context.getResources(), currentItem.getIconID(), 90, 90));
        viewHolder.name.setText(currentItem.getItemName());
        viewHolder.price.setText(currentItem.getPrice());
        viewHolder.description.setText(currentItem.getDescription());
        viewHolder.position = position;

        String weaponName = currentItem.getWeaponName();
        String cachedLowest = priceCache.get(position);

        if (viewHolder.description.getText().toString().length() > 0 && viewHolder.name.getText().toString().length() > 0) {
            if (cachedLowest != null) {
                viewHolder.marketPrice.setText(cachedLowest);
            } else {
                new ScraperAsyncTask(position, viewHolder, weaponName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
            }
        }

        return convertView;
    }

    /**
     * http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Do scraping in separate thread so main UI doesn't freeze up.
     * http://jmsliu.com/1431/download-images-by-asynctask-in-listview-android-example.html
     *
     * Also keep track of position since View recycling can occur at the same time (scrolling)
     * http://stackoverflow.com/questions/11695850/android-listview-updating-of-image-thumbnails-using-asynctask-causes-view-recycl
     */
    private static class ScraperAsyncTask extends AsyncTask<Void, String, String> {
        private ViewHolder mHolder;
        private String mWeaponName;
        private int mPosition;

        public ScraperAsyncTask(int position, ViewHolder holder, String weaponName) {
            mPosition = position;
            mHolder = holder;
            mWeaponName = weaponName;
        }

        @Override
        protected String doInBackground(Void... params) {
            String skinName = mHolder.name.getText().toString();
            // Strip out knives that have default skins so scraper searches correctly
            if (skinName.equals("Regular")) {
                skinName = "";
            }
            publishProgress("Fetching price...");

            return LowestPriceScraper.getLowestPrice(mWeaponName + "+" + skinName);
        }

        @Override
        protected void onPostExecute(String lowestPrice) {
            if (mHolder.position == mPosition) {
                if (lowestPrice != null) {
                    String output = "Starting at: " + lowestPrice;
                    priceCache.put(mPosition, output);
                    mHolder.marketPrice.setText(output);
                }
            }
        }

        @Override
        protected void onProgressUpdate(String... params) {
            mHolder.marketPrice.setText(params[0]);
        }
    }
}
