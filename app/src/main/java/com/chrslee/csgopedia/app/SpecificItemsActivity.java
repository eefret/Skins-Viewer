package com.chrslee.csgopedia.app;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.chrslee.csgopedia.app.util.Item;

import java.util.ArrayList;
import java.util.List;


public class SpecificItemsActivity extends Activity {
    private List<Item> myItems = new ArrayList<Item>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_items);

        Bundle extras = getIntent().getExtras();
        String itemType = extras.getString("itemType"); // eg: Map
        String itemName = extras.getString("itemName"); // eg: Aztec

        populateListWith(itemType, itemName);

        ListView list = (ListView) findViewById(R.id.list2);
        list.setAdapter(new PerformanceArrayAdapter(this, myItems));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                int iconID = myItems.get(position).getIconID();

                Intent fullScreenIntent = new Intent(SpecificItemsActivity.this, FullScreenImage.class);
                fullScreenIntent.putExtra("iconID", iconID);

                startActivity(fullScreenIntent);
            }
        });

        // Change title
        getActionBar().setTitle(itemName + " Skins");
    }

    // Populate the arraylist with all of the skins
    private void populateListWith(String itemType, String itemName) {
        SQLiteDatabase sqlDB = ItemsDatabase.getInstance(this).getReadableDatabase();
        Cursor cursor;

        if (itemType.equals("Map")) {
            cursor = sqlDB.rawQuery("SELECT * FROM Weapons WHERE Map = ? ORDER BY Skin ASC", new String[]{itemName});
        } else if (itemType.equals("Case")) {
            // Can't use "Case" as a field name since it's a restricted word.
            // However, I will still use "Case" as a value for the "Type" field.
            // Optionally, add "OR Box = 'All'" if you want to include the knife skins (drop from all cases)
            cursor = sqlDB.rawQuery("SELECT * FROM Weapons WHERE Box = ? ORDER BY Skin ASC", new String[]{itemName});
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
}
