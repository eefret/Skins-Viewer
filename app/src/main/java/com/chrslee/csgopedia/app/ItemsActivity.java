package com.chrslee.csgopedia.app;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.chrslee.csgopedia.app.util.Item;

import java.util.ArrayList;
import java.util.List;


public class ItemsActivity extends ActionBarActivity {
    private List<Item> myItems = new ArrayList<Item>();
    private String itemType;
    private int listType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        Bundle extras = getIntent().getExtras();
        itemType = extras.getString("itemType");

        if (itemType.equals("Map") || itemType.equals("Case")) {
            listType = 2;
        } else {
            listType = 1;
        }

        populateListWith(itemType);

        ListView list = (ListView) findViewById(R.id.list1);
        list.setAdapter(new PerformanceArrayAdapter(this, myItems));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(ItemsActivity.this, SpecificItemsActivity.class);

                intent.putExtra("itemName", myItems.get(position).getItemName());
                intent.putExtra("itemType", itemType);
                intent.putExtra("listType", listType);

                // TODO: Very messy architecture, needs to be cleaned up in ItemsActivity and SpecificItemsActivity
                if (listType == 1) {
                    intent.putExtra("weaponName", myItems.get(position).getItemName());
                }

                startActivity(intent);
            }
        });

        // Change title
        getSupportActionBar().setTitle(itemType + " List");
    }

    // Populate the arraylist with all of the weapons
    // TODO: Understand SQLiteOpenHelper class
    private void populateListWith(String input) {
        SQLiteDatabase sqlDB = ItemsDatabase.getInstance(this).getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("SELECT * FROM Skins WHERE Type = ? AND Skin = ? ORDER BY Name ASC", new String[]{input, "Regular"});

        while (cursor.moveToNext()) {
            String weaponName = cursor.getString(cursor.getColumnIndex("Name"));
            // Get reference ID
            int imageRef = this.getResources().getIdentifier(cursor.getString(cursor.getColumnIndex("Image")),
                    "drawable", this.getPackageName());
            String price = cursor.getString(cursor.getColumnIndex("Price"));

            // Note: Item's params are String itemName, String description, int iconID, String price, int listType
            myItems.add(new Item(weaponName, "", imageRef, price, itemType));
        }
        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent i = new Intent(this, UserSettingsActivity.class);
                startActivityForResult(i, 1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
