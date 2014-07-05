package com.chrslee.csgopedia.app;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
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

public class MainActivity extends ActionBarActivity {

    private SharedPreferences prefs;
    private List<Item> myItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean isLightTheme = prefs.getString("theme", "light").equals("light");
        if (isLightTheme) {
            setTheme(R.style.AppThemeLight);
        } else {
            setTheme(R.style.AppThemeDark);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        populateList(); // Show all items
        showChangelog(); // Show update notes once per update

        ListView list = (ListView) findViewById(R.id.list0);
        PerformanceArrayAdapter adapter = new PerformanceArrayAdapter(this, myItems);
        list.setAdapter(adapter);

        // View image/price page
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                int iconID = myItems.get(position).getIconID();

                Intent imageAndPriceIntent = new Intent(MainActivity.this, ImageAndPriceActivity.class);
                imageAndPriceIntent.putExtra("iconID", iconID);

                startActivity(imageAndPriceIntent);
            }
        });

        // Navigation drawer
        final String[] values = getResources().getStringArray(R.array.nav_drawer_items);
        ((ListView) findViewById(R.id.left_drawer0)).setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, values));
        NavigationDrawerSetup nds = new NavigationDrawerSetup((ListView) findViewById(R.id.left_drawer0),
                (DrawerLayout) findViewById(R.id.drawer_layout), values, getSupportActionBar(), this);
        nds.configureDrawer();
    }

    private void populateList() {
        SQLiteDatabase sqlDB = ItemsDatabase.getInstance(this).getReadableDatabase();
        // Select everything that's not a collection type
        Cursor cursor = sqlDB.rawQuery("SELECT * "
                + "FROM Skins "
                + "WHERE Type != ? AND Type != ? AND Skin != ?"
                + "ORDER BY Skin ASC", new String[]{"Map", "Case", "Regular"});
        myItems = new ArrayList<Item>();

        while (cursor.moveToNext()) {
            String skinName = cursor.getString(cursor.getColumnIndex("Skin"));
            // Get reference ID
            int imageRef = this.getResources().getIdentifier(cursor.getString(cursor.getColumnIndex("Image")),
                    "drawable", this.getPackageName());
            String rarity = cursor.getString(cursor.getColumnIndex("Rarity"));
            String wepName = cursor.getString(cursor.getColumnIndex("Name"));

            myItems.add(new Item(skinName, wepName, imageRef, rarity, wepName));
        }
    }

    private void showChangelog() {
        // Show changelog once per update
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int version = pInfo.versionCode;

            if (prefs.getInt("changelogVersion", 0) != version) {
                ChangeLogDialog _ChangelogDialog = new ChangeLogDialog(this);
                _ChangelogDialog.show();

                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("changelogVersion", version);
                editor.apply();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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
