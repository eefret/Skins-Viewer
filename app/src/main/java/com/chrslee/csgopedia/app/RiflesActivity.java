package com.chrslee.csgopedia.app;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chrslee.csgopedia.app.util.Rifle;

import java.util.ArrayList;
import java.util.List;


public class RiflesActivity extends ActionBarActivity {
    private List<Rifle> myRifles = new ArrayList<Rifle>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rifles);

        populateRifleList();
        populateListView();
        registerClickCallback();
    }
    //Populate the arraylist with all of the rifles
    private void populateRifleList() {
        myRifles.add(new Rifle("AK-47", R.drawable.ak47, "$2700"));
        myRifles.add(new Rifle("AUG", R.drawable.aug, "$3300"));
        myRifles.add(new Rifle("AWP", R.drawable.awp, "$4750"));
        myRifles.add(new Rifle("FAMAS", R.drawable.famas, "$2250"));
        myRifles.add(new Rifle("GALIL AR", R.drawable.galil, "$2000"));
        myRifles.add(new Rifle("G3SG1", R.drawable.g3sg1, "$5000"));
        myRifles.add(new Rifle("M4A1-S", R.drawable.m4a1s, "$2900"));
        myRifles.add(new Rifle("M4A4", R.drawable.m4a4, "$3100"));
        myRifles.add(new Rifle("SCAR-20", R.drawable.scar20, "$5000"));
        myRifles.add(new Rifle("SG 553", R.drawable.sg553, "$3000"));
        myRifles.add(new Rifle("SSG 08", R.drawable.scout, "$2000"));
    }

    private void populateListView() {
        ArrayAdapter<Rifle> adapter = new MyListAdapter(); //Array adapter for Rifle objects
        ListView list = (ListView) findViewById(R.id.rifles_listView); //Set up list
        list.setAdapter(adapter); //assign adapter to ListView
    }
    //finish this. open new activity when a rifle is clicked
    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.rifles_listView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                Rifle clickedRifle = myRifles.get(position);
            }
        });
    }
    //Adapter for listview. Adapter inflates the layout for each row in its getview() method
    //and assigns the data to the individual views in the row. adapter takes an Array and converts the items into View objects to be loaded into the ListView container.
    private class MyListAdapter extends ArrayAdapter<Rifle> {
        public MyListAdapter() { //constructor
            super(RiflesActivity.this, R.layout.item_view, myRifles);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Make sure we have a view to work with (may have been given null)
            View itemView = convertView; //?
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }

            //Find the rifle to work with
            Rifle currentRifle = myRifles.get(position);

            //Fill the view
            ImageView imageView = (ImageView) itemView.findViewById(R.id.item_icon); //Find icon view
            imageView.setImageResource(currentRifle.getIconID());

            //Rifle name:
            TextView nameText = (TextView) itemView.findViewById(R.id.item_name);
            nameText.setText(currentRifle.getRifleType());

            //Rifle price:
            TextView priceText = (TextView) itemView.findViewById(R.id.item_price);
            priceText.setText(currentRifle.getPrice());

            return itemView;
        }
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
