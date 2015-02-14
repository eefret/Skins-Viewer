package com.marvinsyan.csgoskinsviewer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chrslee.csgopedia.app.R;

import java.util.ArrayList;

/**
 * Created by Marvin on 2/12/2015.
 */
public class CustomRecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private ArrayList<SkinData> mSkinData = new ArrayList<>();
    private Context context;
    private final int IMAGE_DIMENS = 100; // Increase to improve image quality at the cost of performance

    public CustomRecyclerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.list_item, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        SkinData current = mSkinData.get(position);
        holder.icon.setImageBitmap(
                decodeSampledBitmapFromResource(context.getResources(), current.imageId, IMAGE_DIMENS, IMAGE_DIMENS));
        holder.gunName.setText(context.getString(current.gunNameId));
        holder.skinName.setText(context.getString(current.skinNameId));

        String rarity = context.getString(current.rarityId);
        holder.rarity.setText(rarity);
        // Assign rarity colors TODO: Change rgb to hex (reused code from v1)
        if (rarity.equals(context.getString(R.string.consumer_grade))) {
            holder.rarity.setTextColor(Color.rgb(181, 181, 181)); // grey
        } else if (rarity.equals(context.getString(R.string.industrial_grade))) {
            holder.rarity.setTextColor(Color.rgb(176, 195, 217)); // light blue
        } else if (rarity.equals(context.getString(R.string.mil_spec))) {
            holder.rarity.setTextColor(Color.rgb(75, 105, 255)); // blue
        } else if (rarity.equals(context.getString(R.string.restricted))) {
            holder.rarity.setTextColor(Color.rgb(136, 71, 255)); // purple
        } else if (rarity.equals(context.getString(R.string.classified))) {
            holder.rarity.setTextColor(Color.rgb(211, 44, 230)); // pinkish-purple
        } else if (rarity.equals(context.getString(R.string.covert))) {
            holder.rarity.setTextColor(Color.rgb(235, 75, 75)); // red
        } else if (rarity.equals(context.getString(R.string.contraband))) {
            holder.rarity.setTextColor(Color.rgb(255, 165, 0)); // light orange
        } else if (rarity.equals(context.getString(R.string.no_rarity))) {
            holder.rarity.setTextColor(Color.rgb(215,215,215)); // greyish
        }

        holder.collection.setText(context.getString(current.collectionId));

        String special = context.getString(current.specialId);
        holder.special.setText(special);
        // Assign StatTrak/Souvenir colors
        if (special.equals(context.getString(R.string.stattrak_available))) {
            holder.special.setTextColor(Color.parseColor("#cf6a32"));
        } else if (special.equals(context.getString(R.string.souvenir_available))) {
            holder.special.setTextColor(Color.parseColor("#ffd700"));
        }

        holder.setClickListener(new RecyclerViewHolder.ClickListener() {
            @Override
            public void onClick(View v, int position) {
                Toast.makeText(context, "Selected item " + (position + 1), Toast.LENGTH_SHORT).show();
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
        return BitmapFactory.decodeResource(res, resId, options);
    }
}
