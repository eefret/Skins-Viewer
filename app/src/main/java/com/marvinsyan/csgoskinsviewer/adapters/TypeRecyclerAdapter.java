package com.marvinsyan.csgoskinsviewer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marvinsyan.csgoskinsviewer.R;
import com.marvinsyan.csgoskinsviewer.RecyclerViewHolder;
import com.marvinsyan.csgoskinsviewer.SkinData;
import com.marvinsyan.csgoskinsviewer.activities.ResultsActivity;

import java.util.ArrayList;

/**
 * Created by Marvin on 2/15/2015.
 */
public class TypeRecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private ArrayList<SkinData> mSkinData = new ArrayList<>();
    private Context context;
    private final int IMAGE_DIMENS = 100; // Increase to improve image quality at the cost of performance
    private String requestType;

    public TypeRecyclerAdapter(Context context, String requestType) {
        this.context = context;
        this.requestType = requestType; // "pistol", "box", "map", etc
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.list_item, parent, false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isLightTheme = prefs.getString("theme", "light").equals("light");
        if (isLightTheme) {
            itemView.setBackgroundColor(context.getResources().getColor(R.color.cardview_light_background));
        } else {
            itemView.setBackgroundColor(context.getResources().getColor(R.color.cardview_dark_background));
        }

        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        final SkinData current = mSkinData.get(position);
        holder.icon.setImageBitmap(
                decodeSampledBitmapFromResource(context.getResources(), current.imageId, IMAGE_DIMENS, IMAGE_DIMENS));
        // current.title holds the raw skin name (as-is from database)
        holder.skinName.setText(context.getResources().getIdentifier(current.title, "string", context.getPackageName()));
        holder.gunName.setText("");
        holder.rarity.setText("");
        holder.collection.setText("");
        holder.special.setText("");
        // Hide the "|"
        holder.divider.setText("");

        holder.setClickListener(new RecyclerViewHolder.ClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent resultActivityIntent = new Intent(context, ResultsActivity.class);
                resultActivityIntent.putExtra("request_type", requestType);
                resultActivityIntent.putExtra("skin_or_collection_name", current.title);
                Activity activity = (Activity) context;
                activity.startActivity(resultActivityIntent);
                activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });
    }

    public void addData(ArrayList<SkinData> data) {
        mSkinData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mSkinData.size();
    }


    /**
     * http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     * Solves the issue with scroll lag in RecyclerView due to image resizing.
     */
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
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
        return BitmapFactory.decodeResource(res, resId, options);
    }
}
