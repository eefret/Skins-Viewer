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
    private final List<Item> data; //inner data List of Item
    private Activity context; //saving the context of the calling activity

    public PerformanceArrayAdapter(Activity context, List<Item> data) {
        super(context, R.layout.item_view, data);
        this.context = context; //setting data to fields
        this.data = data; //setting data to fields
    }

    //getView will be executed everytime a list item from our list enter to the screen
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder; //viewholder variable
        //This is called the viewholder pattern basically what it does
        // is to save the instace of every widget so you dont have to
        //Create them everytime a row is created but reuse them
        //Is a best practice used for performance, now the new
        //RecyclerView implements it by default
        if (convertView == null) {
            //if the convertview is null it means is the first time the row is drawed so we have to instantiate
            //and inflate the widgets inside it
            LayoutInflater inflater = context.getLayoutInflater();
            //A layout inflater is the object that takes an layout xml and makes it a visible screen
            convertView = inflater.inflate(R.layout.item_view, null);
            //xml in /res/layout/item_view.xml

            viewHolder = new ViewHolder(); //creating the viewholder to store the widgets instances
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.item_icon);
            viewHolder.name = (TextView) convertView.findViewById(R.id.item_name);
            viewHolder.price = (TextView) convertView.findViewById(R.id.item_price);
            viewHolder.description = (TextView) convertView.findViewById(R.id.item_description);
            //then we add it into the tag, this basically inserts this object inside the view
            //in the list view so we can reuse it everywhere we have access to a row of the list
            convertView.setTag(viewHolder);
        }
        //getting the current item
        Item currentItem = data.get(position);
        //getting the viewholder from the tag, this means that the if above wasnt
        viewHolder = (ViewHolder) convertView.getTag();
        //Setting the image to the ImageView on the ViewHolder
        viewHolder.icon.setImageBitmap(
                BitmapResizer.decodeSampledBitmapFromResource(context.getResources(), currentItem.getIconID(), 90, 90));
        //Setting the text to the TextView in the viewholder
        viewHolder.name.setText(currentItem.getItemName());

        //Not sure if a typo or an error but he sets the rarity from the price o.O
        String rarity = currentItem.getPrice();
        viewHolder.price.setText(currentItem.getPrice());

        //Assinging the textcolor self explanatory
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
        //After you finish with the view you return it
        //in this case as you took all the widgets from the initial convertview
        //and assigned them to the viewholder that means any change you do to
        //the widgets in the viewholder will change the convertView widgets
        //since the vars are pointers to those, that means that by returning this
        //we are setting the initial convertview modified as the next row item
        return convertView;
    }

    //ViewHolder object it holds all the widgets in every row of the
    //Listview.
    class ViewHolder {
        ImageView icon;
        TextView name;
        TextView price;
        TextView description;
        int position;
    }
}
