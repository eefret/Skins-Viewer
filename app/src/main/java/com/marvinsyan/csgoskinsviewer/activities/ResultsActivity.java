package com.marvinsyan.csgoskinsviewer.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.marvinsyan.csgoskinsviewer.R;
import com.marvinsyan.csgoskinsviewer.SkinData;
import com.marvinsyan.csgoskinsviewer.adapters.ResultsRecyclerAdapter;
import com.marvinsyan.csgoskinsviewer.utils.DatabaseHelper;

import java.util.ArrayList;

/**
 * Created by Marvin on 2/15/2015.
 */
public class ResultsActivity extends ActionBarActivity {
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
            setContentView(R.layout.activity_results);

            findViewById(R.id.app_bar_results).setBackgroundColor(
                    getResources().getColor(R.color.colorPrimary));
        } else {
            setTheme(R.style.AppTheme_Base_Dark);

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_results);

            findViewById(R.id.app_bar_results).setBackgroundColor(
                    getResources().getColor(R.color.darkColorPrimary));
        }

        // Show toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar_results);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Set up RecyclerView
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_results);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ResultsRecyclerAdapter mAdapter = new ResultsRecyclerAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        // Load database
        // http://www.tutorialspoint.com/android/android_sqlite_database.htm
        databaseHelper = DatabaseHelper.getInstance(this);
        String requestType = getIntent().getExtras().getString("request_type");
        String skinOrCollectionName = getIntent().getExtras().getString("skin_or_collection_name");
        Cursor cursor;
        String queryType;
        if (requestType.equals("box") || requestType.equals("map")) {
            queryType = "collection";
        }
        else {
            queryType = "weapon_name";
        }
        cursor = databaseHelper.executeQuery("SELECT * FROM skins WHERE " + queryType + " = '"
                + skinOrCollectionName + "' ORDER BY skin_name");

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
        // Raw string ("ak_47") -> R.string (R.string.ak_47) -> getString(R.string.ak_47)
        setTitle(getString(getResources().getIdentifier(skinOrCollectionName, "string", getPackageName())) + " Skins");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            return true;
        } else if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivityForResult(i, 1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        this.finish();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }
}
