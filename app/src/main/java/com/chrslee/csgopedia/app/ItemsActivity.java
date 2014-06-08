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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        Bundle extras = getIntent().getExtras();
        itemType = extras.getString("itemType");

        populateListWith(itemType);

        ListView list = (ListView) findViewById(R.id.list1);
        list.setAdapter(new PerformanceArrayAdapter(this, myItems));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(ItemsActivity.this, SpecificItemsActivity.class);
                intent.putExtra("itemName", myItems.get(position).getItemName());
                intent.putExtra("itemType", itemType);

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
        Cursor cursor = sqlDB.rawQuery("SELECT * FROM Weapons WHERE Type = ? AND Skin = ? ORDER BY Name ASC", new String[]{input, "Regular"});

        while (cursor.moveToNext()) {
            String weaponName = cursor.getString(cursor.getColumnIndex("Name"));
            // Get reference ID
            int imageRef = this.getResources().getIdentifier(cursor.getString(cursor.getColumnIndex("Image")),
                    "drawable", this.getPackageName());
            String price = cursor.getString(cursor.getColumnIndex("Price"));

            myItems.add(new Item(weaponName, "", imageRef, price));
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
