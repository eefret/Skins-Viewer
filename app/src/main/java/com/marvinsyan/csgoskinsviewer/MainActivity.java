package com.marvinsyan.csgoskinsviewer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Marvin on 2/5/2015.
 */
public class MainActivity extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Light/Dark theme
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLightTheme = prefs.getString("theme", "light").equals("light");
        if (isLightTheme) {
            setTheme(R.style.AppTheme_Base_Light);

            // Needs to be after setTheme
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            findViewById(R.id.drawer_list).setBackgroundColor(
                    getResources().getColor(R.color.cardview_light_background));
            findViewById(R.id.app_bar).setBackgroundColor(
                    getResources().getColor(R.color.colorPrimary));
        } else {
            setTheme(R.style.AppTheme_Base_Dark);

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            findViewById(R.id.drawer_list).setBackgroundColor(
                    getResources().getColor(R.color.cardview_dark_background));
            findViewById(R.id.app_bar).setBackgroundColor(
                    getResources().getColor(R.color.darkColorPrimary));
        }

        // Show toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Set up nav drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        String[] mDrawerListItems = getResources().getStringArray(R.array.drawer_list);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);

        // Drawer row icons
        int[] imageIds = {R.drawable.desert_eagle_vanilla, R.drawable.negev_vanilla,
                R.drawable.p90_vanilla, R.drawable.ak_47_vanilla, R.drawable.bayonet_vanilla,
                R.drawable.chroma_case, R.drawable.the_dust_2_collection};

        mDrawerList.setAdapter(new DrawerListAdapter(this, mDrawerListItems, imageIds));
        final Intent typeActivityIntent = new Intent(this, TypeActivity.class);

        // Drawer row click listener
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                typeActivityIntent.putExtra("position", position);
                mDrawerLayout.closeDrawer(mDrawerList);
                // Wait for navigation drawer to close completely first.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(typeActivityIntent);
                        // Pull new activity into view, push old activity out of view
                        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                    }
                }, 250);
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close) {
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
                invalidateOptionsMenu();
                syncState();
            }

            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
                invalidateOptionsMenu();
                syncState();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        // Set up RecyclerView
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        MainRecyclerAdapter mAdapter = new MainRecyclerAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        // Load database
        // http://www.tutorialspoint.com/android/android_sqlite_database.htm
        databaseHelper = DatabaseHelper.getInstance(this);
        Cursor cursor = databaseHelper.executeQuery("SELECT * FROM skins ORDER BY skin_name");

        ArrayList<SkinData> data = new ArrayList<>();
        String packageName = getPackageName();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                data.add(new SkinData(
                    // TODO: Better way to do this?
                    getResources().getIdentifier(cursor.getString(cursor.getColumnIndex("image")), "drawable", packageName),
                    getResources().getIdentifier(cursor.getString(cursor.getColumnIndex("weapon_name")), "string", packageName),
                    getResources().getIdentifier(cursor.getString(cursor.getColumnIndex("skin_name")), "string", packageName),
                    getResources().getIdentifier(cursor.getString(cursor.getColumnIndex("rarity")), "string", packageName),
                    getResources().getIdentifier(cursor.getString(cursor.getColumnIndex("collection")), "string", packageName),
                    getResources().getIdentifier(cursor.getString(cursor.getColumnIndex("special")), "string", packageName))
                );

                cursor.moveToNext();
            }
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        mAdapter.addData(data);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        mDrawerToggle.onConfigurationChanged(config);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivityForResult(i, 1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
