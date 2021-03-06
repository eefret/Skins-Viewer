package com.chrslee.csgopedia.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.chrslee.csgopedia.app.util.Item;

import java.util.ArrayList;
import java.util.List;


public class SpecificItemsActivity extends ActionBarActivity {
    PerformanceArrayAdapter adapter;
    String weaponName;
    private List<Item> myItems = new ArrayList<Item>();
    private int listType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean isLightTheme = prefs.getString("theme", "light").equals("light");
        if (isLightTheme) {
            setTheme(R.style.AppThemeLight);
        } else {
            setTheme(R.style.AppThemeDark);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_items);

        final Bundle extras = getIntent().getExtras();
        final String itemType = extras.getString("itemType"); // eg: Map
        final String itemName = extras.getString("itemName"); // eg: Aztec
        listType = extras.getInt("listType");
        weaponName = extras.getString("weaponName");

        populateListWith(itemType, itemName);

        ListView list = (ListView) findViewById(R.id.list2);
        adapter = new PerformanceArrayAdapter(this, myItems);
        list.setAdapter(adapter);

        // View image/price page
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Item item = myItems.get(position);
                int iconID = item.getIconID();

                Intent imageAndPriceIntent = new Intent(SpecificItemsActivity.this, ImageAndPriceActivity.class);
                imageAndPriceIntent.putExtra("iconID", iconID);

                if (!itemType.equals("Knife") && item.getItemName().equals("Regular")) {
                    imageAndPriceIntent.putExtra("searchQuery", "-1"); // Regular non-knife skins are not in marketplace
                    imageAndPriceIntent.putExtra("regularName", weaponName);
                } else if (itemType.equals("Knife") && item.getItemName().equals("Regular")) {
                    imageAndPriceIntent.putExtra("searchQuery", weaponName); // Regular knife skins do not have "regular" in skin name
                } else {
                    // Map/box selected
                    if (listType == 2) {
                        // Weapon name - Skin name
                        imageAndPriceIntent.putExtra("searchQuery", item.getDescription() + " | " + item.getItemName());
                    } else {
                        // Weapon selected
                        imageAndPriceIntent.putExtra("searchQuery", weaponName + " | " + item.getItemName());
                    }
                }
                startActivity(imageAndPriceIntent);
            }
        });

        // Change title
        ActionBar bar = getSupportActionBar();
        bar.setTitle(itemName + " Skins");

        // Navigation drawer
        final String[] values = getResources().getStringArray(R.array.nav_drawer_items);
        ((ListView) findViewById(R.id.left_drawer2)).setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, values));
        NavigationDrawerSetup nds = new NavigationDrawerSetup((ListView) findViewById(R.id.left_drawer2),
                (DrawerLayout) findViewById(R.id.drawer_layout), values, getSupportActionBar(), this);
        nds.configureDrawer();
    }

    // Populate the arraylist with all of the skins
    private void populateListWith(String itemType, String itemName) {
        SQLiteDatabase sqlDB = ItemsDatabase.getInstance(this).getReadableDatabase();
        Cursor cursor;

        if (itemType.equals("Map")) {
            cursor = sqlDB.rawQuery("SELECT * FROM Skins WHERE Map = ? ORDER BY Skin ASC", new String[]{itemName});
        } else if (itemType.equals("Case")) {
            // Can't use "Case" as a field name since it's a restricted word.
            // However, I will still use "Case" as a value for the "Type" field.
            // Optionally, add "OR Box = 'All'" if you want to include the knife skins (drop from all cases)
            cursor = sqlDB.rawQuery("SELECT * FROM Skins WHERE Box = ? ORDER BY Skin ASC", new String[]{itemName});
        } else {
            cursor = sqlDB.rawQuery("SELECT * FROM Skins WHERE Name = ? ORDER BY Skin ASC", new String[]{itemName});
        }

        while (cursor.moveToNext()) {
            String skinName = cursor.getString(cursor.getColumnIndex("Skin"));
            // Get reference ID
            int imageRef = this.getResources().getIdentifier(cursor.getString(cursor.getColumnIndex("Image")),
                    "drawable", this.getPackageName());
            String rarity = cursor.getString(cursor.getColumnIndex("Rarity"));
            String wepName = "";
            String mapOrBox = "";

            // Don't need to show weapon names for a list of skins of 1 weapon, show map/case instead.
            if (listType == 2) {
                wepName = cursor.getString(cursor.getColumnIndex("Name"));
            } else {
                mapOrBox = cursor.getString(cursor.getColumnIndex("Map"));
                if (mapOrBox == null || mapOrBox.equals("")) {
                    mapOrBox = cursor.getString(cursor.getColumnIndex("Box"));
                }
            }

            // Note: Item's params are String itemName, String description, int iconID, String price, String weaponName (explicit)
            // skinName and weaponName are swapped here for visual purposes
            if (listType == 2) {
                myItems.add(new Item(skinName, wepName, imageRef, rarity, wepName));
            } else {
                myItems.add(new Item(skinName, mapOrBox, imageRef, rarity, weaponName));
            }
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
