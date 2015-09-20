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
    protected void onCreate(Bundle savedInstanceState) {// OnCreate is the first method of every activity

        //This instantiates one preference manager is used to store and read values
        //to use in the app, here we can see there is a string value used to
        //Know whether the user selected light or dark theme
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLightTheme = prefs.getString("theme", "light").equals("light");
        if (isLightTheme) {
            setTheme(R.style.AppThemeLight);
            //Selecting theme in XML res/values/styles.xml:AppThemeLight
        } else {
            setTheme(R.style.AppThemeDark);
            //Selecting theme in XML res/values/styles.xml:AppThemeDark
        }

        //Calling this method on parent required by the framework
        super.onCreate(savedInstanceState);
        //Setting this activity layout XML in /res/layout/activity_main.xml
        setContentView(R.layout.activity_main);

        populateList(); // Show all items
        showChangelog(); // Show update notes once per update

        //Making an instance of a listview in the layout with the id list0
        ListView list = (ListView) findViewById(R.id.list0);
        //Creating an instance of PerformanceArrayAdapter adn passing myItems arraylist to it
        //The list is populated in the populateList method
        PerformanceArrayAdapter adapter = new PerformanceArrayAdapter(this, myItems);
        //Setting this adapter to the list, when you set an adapter it takes all items in his data
        //Arraylist and adapt it to the list
        list.setAdapter(adapter);

        // View image/price page
        //This sets the action to be performed when any item in the list is clicked
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //You always pass an instantiated interface and implement his onitemClick method
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Here he is taking the position passed by the listview to this method to
                //Fetch the selected item in the myItems arraylist
                Item item = myItems.get(position);
                int iconID = item.getIconID();

                //Creating an Intent to start another activity ImageAndPriceActivity and pass
                //iconID to it via an extra on the intent, its better if you read more about intents
                //and extras here: http://developer.android.com/guide/components/intents-filters.html
                //and here: http://developer.android.com/reference/android/content/Intent.html
                //Intents are one of the core components so be sure to read about them your
                //teacher will ask you about it
                Intent imageAndPriceIntent = new Intent(MainActivity.this, ImageAndPriceActivity.class);
                imageAndPriceIntent.putExtra("iconID", iconID); //icon id passed to ImageAndPriceActivity
                // Weapon name - Skin name
                imageAndPriceIntent.putExtra("searchQuery", item.getDescription() + " | " + item.getItemName());// SearchQuery passed to ImageAndPriceActivity

                startActivity(imageAndPriceIntent);
                //Starting the ImageAndPriceActivity with the intent
            }
        });

        // Navigation drawer
        //Getting the names of the drawer options from the getResources
        //To know more about the R class and the resources go to:
        // http://knowledgefolders.com/akc/display?url=displaynoteimpurl&ownerUserId=satya&reportId=2883
        final String[] values = getResources().getStringArray(R.array.nav_drawer_items);
        //Setting the adapter to the Listview inside the Drawer
        //Would be better to take the instance of the drawer first and perform findviewbyId from it
        ((ListView) findViewById(R.id.left_drawer0)).setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, values));
        //Creating a new instance of the navigation Drawer using a self created subclass
        // NavigationDrawerSetup, this is sort of a bad practice right here
        NavigationDrawerSetup nds = new NavigationDrawerSetup((ListView) findViewById(R.id.left_drawer0),
                (DrawerLayout) findViewById(R.id.drawer_layout), values, getSupportActionBar(), this);
        //Calling the configureDrawer method inside the self made NavigationDrawer
        nds.configureDrawer();
    }

    //This method populates the list myItems
    private void populateList() {
        //nstantiating the database singleton and getting the readable database
        //Databases in android are sqlite
        SQLiteDatabase sqlDB = ItemsDatabase.getInstance(this).getReadableDatabase();
        //Creating a cursor with a rawQuery not that this can be done better
        //with contentResolvers this will select * from the table skins where the
        // type is different from Map and Case and skin is different from Regular
        Cursor cursor = sqlDB.rawQuery("SELECT * "
                + "FROM Skins "
                + "WHERE Type != ? AND Type != ? AND Skin != ?"
                + "ORDER BY Skin ASC", new String[]{"Map", "Case", "Regular"});
        myItems = new ArrayList<Item>();//Creating a new arraylist instance

        //Iterating through the cursor
        while (cursor.moveToNext()) {
            //Getting the string in the Skin colum in this row
            String skinName = cursor.getString(cursor.getColumnIndex("Skin"));
            // Get reference ID
            //Getting the string in the Image colum in this row and opening a
            //Drawable with his name saved in the drawables folder
            int imageRef = this.getResources().getIdentifier(cursor.getString(cursor.getColumnIndex("Image")),
                    "drawable", this.getPackageName());
            //Getting the string in the Rarity colum in this row
            String rarity = cursor.getString(cursor.getColumnIndex("Rarity"));
            //Getting the string in the Name colum in this row
            String wepName = cursor.getString(cursor.getColumnIndex("Name"));
            //Adding a new Item with all the info obtained from db to the myItems arraylist
            myItems.add(new Item(skinName, wepName, imageRef, rarity, wepName));
        }
    }

    private void showChangelog() {
        // Show changelog once per update
        try {
            //This get the package info from the package manager kinda overkilling as you
            //can pull it out from gradle directly instead of calling the package manager
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            //obtaining the version code
            int version = pInfo.versionCode;

            //Basically he compares the version code with the changelogversion
            //and instantiate a ChangelogDialog saying what changed
            if (prefs.getInt("changelogVersion", 0) != version) {
                ChangeLogDialog _ChangelogDialog = new ChangeLogDialog(this);
                _ChangelogDialog.show();

                //Then after that overrides the new version code
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("changelogVersion", version);
                editor.apply();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    //OncreateOptionsMenu tells this activity if it has a menu
    //If you inflate a menu here and return true it will
    //draw the menu in your actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        //Add the items in the XML /res/menu/settings.xml to the actual activity
        //Actionbar menu
        return true;
    }

    //onOptionsItemSelected is the listener of the menu in your actionbar
    //Whenever a item there is clicked this method is executed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //if we click the setting button then execute this
            case R.id.menu_settings:
                //this is a new intent to open the userSettingsActivity
                Intent i = new Intent(this, UserSettingsActivity.class);
                //startActivityForResult opens an activity and then awaits to
                //its finish for a result, this is not needed since he is not
                //listening for the result anyway
                startActivityForResult(i, 1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
