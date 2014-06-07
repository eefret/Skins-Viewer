package com.chrslee.csgopedia.app;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chrslee.csgopedia.app.util.Item;

import java.util.List;

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
            rowView = inflater.inflate(R.layout.item_view, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) rowView.findViewById(R.id.item_icon);
            viewHolder.name = (TextView) rowView.findViewById(R.id.item_name);
            viewHolder.price = (TextView) rowView.findViewById(R.id.item_price);
            viewHolder.description = (TextView) rowView.findViewById(R.id.item_description);

            rowView.setTag(viewHolder);
        }

        Item currentItem = data.get(position);
        ViewHolder holder = (ViewHolder) rowView.getTag();

        holder.icon.setImageResource(currentItem.getIconID());
        holder.name.setText(currentItem.getItemName());
        holder.price.setText(currentItem.getPrice());
        holder.description.setText(currentItem.getDescription());

        return rowView;
    }
}
