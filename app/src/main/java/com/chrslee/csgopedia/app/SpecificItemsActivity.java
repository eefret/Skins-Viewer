package com.chrslee.csgopedia.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.chrslee.csgopedia.app.util.Item;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.ArrayList;
import java.util.List;


public class SpecificItemsActivity extends ActionBarActivity {
    private List<Item> myItems = new ArrayList<Item>();
    private int listType;
    PerformanceArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_items);

        Bundle extras = getIntent().getExtras();
        final String itemType = extras.getString("itemType"); // eg: Map
        final String itemName = extras.getString("itemName"); // eg: Aztec
        listType = extras.getInt("listType");

        populateListWith(itemType, itemName);

        ListView list = (ListView) findViewById(R.id.list2);
        adapter = new PerformanceArrayAdapter(this, myItems);
        list.setAdapter(adapter);

        // View full screen image
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                int iconID = myItems.get(position).getIconID();

                Intent fullScreenIntent = new Intent(SpecificItemsActivity.this, FullScreenImage.class);
                fullScreenIntent.putExtra("iconID", iconID);

                startActivity(fullScreenIntent);
            }
        });

        // Show marketplace page in browser
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                String skinName = myItems.get(position).getItemName();
                String weaponName;

                // Regular knives don't have a skin name
                if (skinName.equals("Regular")) {
                    skinName = "";
                }
                // For map/case lists, weapon name is placed in the description field of Item
                if (listType == 2) {
                    weaponName = myItems.get(position).getDescription();
                    // Otherwise, weapon name is itemName
                } else {
                    weaponName = itemName;
                }

                String URL = "http://steamcommunity.com/market/search?q=appid%3A730+" + weaponName +
                        "+" + skinName;
                URL = URL.replace(" ", "+");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
                startActivity(browserIntent);

                // True prevents normal click event from occurring as well
                return true;
            }
        });

        // Change title
        getSupportActionBar().setTitle(itemName + " Skins");

        // Show tutorial once
        // https://github.com/amlcurran/ShowcaseView
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {
            new ShowcaseView.Builder(this, true)
                    .setTarget(new ViewTarget(findViewById(R.id.list2)))
                    .setContentTitle("View Steam Marketplace")
                    .setContentText("Tap and hold to see a skin on the Steam Marketplace.")
                    .setStyle(R.style.CustomShowcaseTheme)
                    .build();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }
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
            String weaponName = "";
            String mapOrBox = "";

            // Don't need to show weapon names for a list of skins of 1 weapon, show map/case instead.
            if (listType == 2) {
                weaponName = cursor.getString(cursor.getColumnIndex("Name"));
            } else {
                mapOrBox = cursor.getString(cursor.getColumnIndex("Map"));
                if (mapOrBox.equals("")) {
                    mapOrBox = cursor.getString(cursor.getColumnIndex("Box"));
                }
            }

            // Note: Item's params are String itemName, String description, int iconID, String price
            // skinName and weaponName are swapped here for visual purposes
            if (listType == 2) {
                myItems.add(new Item(skinName, weaponName, imageRef, rarity, weaponName));
            } else {
                myItems.add(new Item(skinName, mapOrBox, imageRef, rarity, weaponName));
            }
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
