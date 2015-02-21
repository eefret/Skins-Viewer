package com.marvinsyan.csgoskinsviewer;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Marvin on 2/12/2015.
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public ImageView icon;
    public TextView gunName;
    public TextView skinName;
    public TextView rarity;
    public TextView collection;
    public TextView special;
    public TextView divider;
    private ClickListener clickListener;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        icon = (ImageView) itemView.findViewById(R.id.list_icon);
        gunName = (TextView) itemView.findViewById(R.id.list_gun_name);
        skinName = (TextView) itemView.findViewById(R.id.list_skin_name);
        rarity = (TextView) itemView.findViewById(R.id.list_rarity);
        collection = (TextView) itemView.findViewById(R.id.list_collection);
        special = (TextView) itemView.findViewById(R.id.list_special);
        divider = (TextView) itemView.findViewById(R.id.list_divider);
        itemView.setOnClickListener(this);
    }

    // Setting up click listener
    // http://blog.lovelyhq.com/creating-lists-with-recyclerview-in-android-part-2/
    public interface ClickListener {
        public void onClick(View v, int position);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View view) {
        clickListener.onClick(view, getPosition());
    }
}
