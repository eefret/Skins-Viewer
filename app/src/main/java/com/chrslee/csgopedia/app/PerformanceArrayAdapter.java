package com.chrslee.csgopedia.app;

import android.app.Activity;
import android.graphics.Color;
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
    private final List<Item> data;
    private Activity context;

    public PerformanceArrayAdapter(Activity context, List<Item> data) {
        super(context, R.layout.item_view, data);
        this.context = context;
        this.data = data;
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

            convertView.setTag(viewHolder);
        }

        Item currentItem = data.get(position);
        viewHolder = (ViewHolder) convertView.getTag();

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
                viewHolder.price.setTextColor(Color.rgb(181, 181, 181)); // grey
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

        return convertView;
    }

    class ViewHolder {
        ImageView icon;
        TextView name;
        TextView price;
        TextView description;
        int position;
    }
}
