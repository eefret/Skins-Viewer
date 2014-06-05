package com.chrslee.csgopedia.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chrslee.csgopedia.app.util.Item;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ItemsActivity extends ActionBarActivity {
    private List<Item> myItems = new ArrayList<Item>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        Bundle extras = getIntent().getExtras();
        String itemType = extras.getString("itemType");

        populateListWith(itemType);
        populateListView();

        // Change title
        getSupportActionBar().setTitle(itemType);
        registerClickCallback();
    }

    //Populate the arraylist with all of the weapons
    // TODO: Understand SQLiteOpenHelper class
    private void populateListWith(String itemType) {
        SQLiteDatabase sqlDB = ItemsDatabase.getInstance(this).getReadableDatabase();

        // Add values to String[] to avoid query concatenation
        Cursor cursor = sqlDB.rawQuery("SELECT * FROM Weapons WHERE Type = ? AND Skin = ? ORDER BY Name ASC",
                new String[]{itemType, "Regular"});

        while (cursor.moveToNext()) {
            String weaponName = cursor.getString(cursor.getColumnIndex("Name"));
            // Get reference ID
            int imageRef = this.getResources().getIdentifier(cursor.getString(cursor.getColumnIndex("Image")),
                    "drawable", this.getPackageName());
            String price = cursor.getString(cursor.getColumnIndex("Price"));

            myItems.add(new Item(weaponName, imageRef, price));
        }
        cursor.close();
    }

    private void populateListView() {
        ArrayAdapter<Item> adapter = new MyListAdapter(); //Array adapter for Item objects
        ListView list = (ListView) findViewById(R.id.items_listView); //Set up list
        list.setAdapter(adapter); //assign adapter to ListView
    }

    //finish this. open new activity when an item is clicked
    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.items_listView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                Item clickedRifle = myItems.get(position);
            }
        });
    }

    //Adapter for listview. Adapter inflates the layout for each row in its getview() method
    //and assigns the data to the individual views in the row. adapter takes an Array and converts the items into View objects to be loaded into the ListView container.
    private class MyListAdapter extends ArrayAdapter<Item> {
        public MyListAdapter() { //constructor
            super(ItemsActivity.this, R.layout.item_view, myItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Make sure we have a view to work with (may have been given null)
            View itemView = convertView; //?
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }

            //Find the rifle to work with
            Item currentItem = myItems.get(position);

            //Fill the view
            ImageView imageView = (ImageView) itemView.findViewById(R.id.item_icon); //Find icon view
            imageView.setImageResource(currentItem.getIconID());

            // or imageView.setImageDrawable(getResources().getDrawable(currentItem.getIconID()));
            // setImageResource reads and decodes Bitmap on the UI thread

            //Rifle name:
            TextView nameText = (TextView) itemView.findViewById(R.id.item_name);
            nameText.setText(currentItem.getItemName());

            //Rifle price:
            TextView priceText = (TextView) itemView.findViewById(R.id.item_price);
            priceText.setText(currentItem.getPrice());

            return itemView;
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
