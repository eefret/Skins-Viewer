package com.chrslee.csgopedia.app;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.marvinsyan.csgoskinsviewer.CustomListAdapter;
import com.marvinsyan.csgoskinsviewer.CustomRecyclerAdapter;
import com.marvinsyan.csgoskinsviewer.DatabaseHelper;
import com.marvinsyan.csgoskinsviewer.SimpleDividerItemDecoration;
import com.marvinsyan.csgoskinsviewer.SkinData;
import com.marvinsyan.csgoskinsviewer.SubActivity;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Show Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        String[] mDrawerListItems = getResources().getStringArray(R.array.drawer_list);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);

        // Drawer row icons
        int[] imageIds = {R.drawable.desert_eagle_vanilla, R.drawable.negev_vanilla,
                R.drawable.p90_vanilla, R.drawable.ak_47_vanilla, R.drawable.bayonet_vanilla,
                R.drawable.chroma_case, R.drawable.the_dust_2_collection};
        mDrawerList.setAdapter(new CustomListAdapter(this, mDrawerListItems, imageIds));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("click", "Clicked " + (position + 1));
                Toast.makeText(MainActivity.this, "You selected " + (position + 1), Toast.LENGTH_SHORT).show();
                mDrawerLayout.closeDrawer(mDrawerList);
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
        CustomRecyclerAdapter mAdapter = new CustomRecyclerAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        // Load database
        // http://www.tutorialspoint.com/android/android_sqlite_database.htm
        databaseHelper = new DatabaseHelper(this);
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
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }

    // When device is rotated...
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
            startActivity(new Intent(this, SubActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
