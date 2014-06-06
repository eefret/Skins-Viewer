package com.chrslee.csgopedia.app;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chrslee.csgopedia.app.util.Item;

import java.util.ArrayList;
import java.util.List;


public class SpecificItemsActivity extends ListActivity {
    private List<Item> myItems = new ArrayList<Item>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        String itemType = extras.getString("itemType"); // eg: Map
        String itemName = extras.getString("itemName"); // eg: Aztec

        populateListWith(itemType, itemName);

        setListAdapter(new MobileArrayAdapter(this));

        // Change title
        getActionBar().setTitle(itemName + " Skins");
    }

    // Populate the arraylist with all of the skins
    private void populateListWith(String itemType, String itemName) {
        SQLiteDatabase sqlDB = ItemsDatabase.getInstance(this).getReadableDatabase();
        Cursor cursor;

        if (itemType.equals("Map")) {
            cursor = sqlDB.rawQuery("SELECT * FROM Weapons WHERE Map = ? ORDER BY Name ASC", new String[]{itemName});
        } else if (itemType.equals("Case")) {
            // Can't use "Case" as a field name since it's a restricted word.
            // However, I will still use "Case" as a value for the "Type" field.
            // Optionally, add "OR Box = 'All'" if you want to include the knife skins (drop from all boxes)
            cursor = sqlDB.rawQuery("SELECT * FROM Weapons WHERE Box = ? ORDER BY Name ASC", new String[]{itemName});
        } else {
            cursor = sqlDB.rawQuery("SELECT * FROM Weapons WHERE Name = ? ORDER BY Skin ASC", new String[]{itemName});
        }

        while (cursor.moveToNext()) {
            String skinName = cursor.getString(cursor.getColumnIndex("Skin"));
            // Get reference ID
            int imageRef = this.getResources().getIdentifier(cursor.getString(cursor.getColumnIndex("Image")),
                    "drawable", this.getPackageName());
            String rarity = cursor.getString(cursor.getColumnIndex("Rarity"));
            String weaponName;

            // Don't need to show weapon names for a list of skins of 1 weapon, show map/case instead.
            if (itemType.equals("Map") || itemType.equals("Case")) {
                weaponName = cursor.getString(cursor.getColumnIndex("Name"));
            } else {
                weaponName = cursor.getString(cursor.getColumnIndex("Map"));
                if (weaponName.equals("")) {
                    weaponName = cursor.getString(cursor.getColumnIndex("Box"));
                }
            }

            // Put rarity in price field (temporary?)
            myItems.add(new Item(skinName, weaponName, imageRef, rarity));
        }
        cursor.close();
    }

    /**
     * http://www.mkyong.com/android/android-listview-example/
     */
    public class MobileArrayAdapter extends ArrayAdapter<Item> {
        private final Context context;

        public MobileArrayAdapter(Context context) {
            super(context, R.layout.item_view, myItems);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            Item currentItem = myItems.get(position);
            View rowView = inflater.inflate(R.layout.item_view, parent, false);

            ImageView icon = (ImageView) rowView.findViewById(R.id.item_icon);
            TextView name = (TextView) rowView.findViewById(R.id.item_name);
            TextView price = (TextView) rowView.findViewById(R.id.item_price);
            TextView description = (TextView) rowView.findViewById(R.id.item_description);

            icon.setImageResource(currentItem.getIconID());
            name.setText(currentItem.getItemName());
            price.setText(currentItem.getPrice());
            description.setText(currentItem.getDescription());

            return rowView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rifles, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        int iconID = myItems.get(position).getIconID();

        Intent fullScreenIntent = new Intent(this, FullScreenImage.class);
        fullScreenIntent.putExtra("iconID", iconID);

        startActivity(fullScreenIntent);
    }
}
