package com.marvinsyan.csgoskinsviewer;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.chrslee.csgopedia.app.R;

import java.util.ArrayList;

/**
 * Displays weapons/cases/maps after an item in the navigation drawer is clicked.
 */
public class TypeActivity extends ActionBarActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar_sub);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Set up RecyclerView
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_sub);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        TypeRecyclerAdapter mAdapter = new TypeRecyclerAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        // Load database
        String drawerSelection = "";
        switch (getIntent().getExtras().getInt("position")) {
            case 0:
                drawerSelection = "pistol";
                break;
            case 1:
                drawerSelection = "heavy";
                break;
            case 2:
                drawerSelection = "smg";
                break;
            case 3:
                drawerSelection = "rifle";
                break;
            case 4:
                drawerSelection = "knife";
                break;
            case 5:
                drawerSelection = "box";
                break;
            case 6:
                drawerSelection = "map";
                break;
        }
        databaseHelper = DatabaseHelper.getInstance(this);
        Cursor cursor;
        // Display weapons
        if (!drawerSelection.equals("box") && !drawerSelection.equals("map")) {
            cursor = databaseHelper.executeQuery("SELECT * FROM skins " +
                    "WHERE weapon_type = '" + drawerSelection + "' AND skin_name = 'vanilla' ORDER BY skin_name");
            setTitle(getString(R.string.weapons));
        // Display cases/maps
        } else {
            cursor = databaseHelper.executeQuery("SELECT * FROM collections " +
                    "WHERE type = '" + drawerSelection + "' ORDER BY name");
            setTitle(getString(R.string.collections));
        }
        ArrayList<SkinData> data = new ArrayList<>();
        String packageName = getPackageName();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                if (!drawerSelection.equals("box") && !drawerSelection.equals("map")) {
                    data.add(new SkinData(
                        // Hide text under image, display weapon name where skin name normally appears
                        getResources().getIdentifier(cursor.getString(cursor.getColumnIndex("image")), "drawable", packageName),
                        getResources().getIdentifier(cursor.getString(cursor.getColumnIndex("weapon_name")), "string", packageName))
                    );
                } else {
                    data.add(new SkinData(
                        getResources().getIdentifier(cursor.getString(cursor.getColumnIndex("image")), "drawable", packageName),
                        getResources().getIdentifier(cursor.getString(cursor.getColumnIndex("name")), "string", packageName))
                    );
                }
                cursor.moveToNext();
            }
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        mAdapter.addData(data);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sub, menu);
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
