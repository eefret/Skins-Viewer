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


public class ItemsActivity extends ListActivity {
    private List<Item> myItems = new ArrayList<Item>();
    private String itemType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        itemType = extras.getString("itemType");

        populateListWith(itemType);

        setListAdapter(new MobileArrayAdapter(this));

        // Change title
        getActionBar().setTitle(itemType + " List");
    }

    // Populate the arraylist with all of the weapons
    // TODO: Understand SQLiteOpenHelper class
    private void populateListWith(String input) {
        SQLiteDatabase sqlDB = ItemsDatabase.getInstance(this).getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("SELECT * FROM Weapons WHERE Type = ? AND Skin = ? ORDER BY Name ASC", new String[]{input, "Regular"});

        while (cursor.moveToNext()) {
            String weaponName = cursor.getString(cursor.getColumnIndex("Name"));
            // Get reference ID
            int imageRef = this.getResources().getIdentifier(cursor.getString(cursor.getColumnIndex("Image")),
                    "drawable", this.getPackageName());
            String price = cursor.getString(cursor.getColumnIndex("Price"));

            myItems.add(new Item(weaponName, "", imageRef, price));
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

            icon.setImageResource(currentItem.getIconID());
            name.setText(currentItem.getItemName());
            price.setText(currentItem.getPrice());

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
        Intent intent = new Intent(this, SpecificItemsActivity.class);
        intent.putExtra("itemName", myItems.get(position).getItemName());
        intent.putExtra("itemType", itemType);

        startActivity(intent);
    }
}
