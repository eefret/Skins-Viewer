package com.chrslee.csgopedia.app;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chrslee.csgopedia.app.util.Item;

import java.util.List;

/**
 * ViewHolder pattern to improve ListView scrolling performance.
 */
public class PerformanceArrayAdapter extends ArrayAdapter<Item> {
    private final Activity context;
    private final List<Item> data;

    static class ViewHolder {
        ImageView icon;
        TextView name;
        TextView price;
        TextView description;
    }

    public PerformanceArrayAdapter(Activity context, List<Item> data) {
        super(context, R.layout.item_view, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_view, parent, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) rowView.findViewById(R.id.item_icon);
            viewHolder.name = (TextView) rowView.findViewById(R.id.item_name);
            viewHolder.price = (TextView) rowView.findViewById(R.id.item_price);
            viewHolder.description = (TextView) rowView.findViewById(R.id.item_description);

            rowView.setTag(viewHolder);
        }

        Item currentItem = data.get(position);
        ViewHolder holder = (ViewHolder) rowView.getTag();


        holder.icon.setImageBitmap(
                decodeSampledBitmapFromResource(context.getResources(), currentItem.getIconID(), 80, 80));
        holder.name.setText(currentItem.getItemName());
        holder.price.setText(currentItem.getPrice());
        holder.description.setText(currentItem.getDescription());

        return rowView;
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
}
