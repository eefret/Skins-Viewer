package com.marvinsyan.csgoskinsviewer;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chrslee.csgopedia.app.R;

import java.util.ArrayList;

/**
 * Created by Marvin on 2/15/2015.
 */
public class TypeRecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private ArrayList<SkinData> mSkinData = new ArrayList<>();
    private Context context;
    private final int IMAGE_DIMENS = 100; // Increase to improve image quality at the cost of performance
    private Class nextClass;

    public TypeRecyclerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.list_item, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        final SkinData current = mSkinData.get(position);
        holder.icon.setImageBitmap(
                decodeSampledBitmapFromResource(context.getResources(), current.imageId, IMAGE_DIMENS, IMAGE_DIMENS));
        holder.skinName.setText(context.getString(current.titleId));
        holder.gunName.setText("");
        holder.rarity.setText("");
        holder.collection.setText("");
        holder.special.setText("");
        // Hide the "|"
        holder.divider.setText("");
        holder.setClickListener(new RecyclerViewHolder.ClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent i = new Intent(context, nextClass);
                i.putExtra("weapon_name", current.gunNameId);
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