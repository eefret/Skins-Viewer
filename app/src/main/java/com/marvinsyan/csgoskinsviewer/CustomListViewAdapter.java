package com.marvinsyan.csgoskinsviewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chrslee.csgopedia.app.R;

/**
 * Created by Marvin on 2/11/2015.
 */
public class CustomListViewAdapter extends BaseAdapter {
    private String[] titles;
    private int[] images;
    private static LayoutInflater inflater = null;

    public CustomListViewAdapter(Context context, String[] drawerListItems, int[] imageIds) {
        titles = drawerListItems;
        images = imageIds;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        ImageView imageView;
        TextView textView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView = inflater.inflate(R.layout.drawer_row, null);
        holder.imageView = (ImageView) rowView.findViewById(R.id.list_icon);
        holder.textView = (TextView) rowView.findViewById(R.id.list_text);
        holder.imageView.setImageResource(images[position]); // Get hardcoded images
        holder.textView.setText(titles[position]); // Get titles from strings.xml
        return rowView;
    }
}
