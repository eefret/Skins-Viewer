package com.chrslee.csgopedia.app;

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
import com.chrslee.csgopedia.app.util.Item;
import java.util.ArrayList;
import java.util.List;


public class ItemsActivity extends ActionBarActivity {
    private List<Item> myItems = new ArrayList<Item>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        Bundle extras = getIntent().getExtras();
        populateListWith(extras.getString("itemType"));
        populateListView();
        registerClickCallback();
    }

    private enum ItemTypes {
        RIFLE,
        SMG,
        HEAVY,
        PISTOL,
        KNIFE,
        MAP,
        CASE
    }
    //Populate the arraylist with all of the rifles
    private void populateListWith(String itemType) {
        ItemTypes type = ItemTypes.valueOf(itemType.toUpperCase());

        switch (type) {
            case RIFLE:
                myItems.add(new Item("AK-47", R.drawable.ak47, "$2700"));
                myItems.add(new Item("AUG", R.drawable.aug, "$3300"));
                myItems.add(new Item("AWP", R.drawable.awp, "$4750"));
                myItems.add(new Item("FAMAS", R.drawable.famas, "$2250"));
                myItems.add(new Item("Galil AR", R.drawable.galil, "$2000"));
                myItems.add(new Item("G3SG1", R.drawable.g3sg1, "$5000"));
                myItems.add(new Item("M4A1-S", R.drawable.m4a1s, "$2900"));
                myItems.add(new Item("M4A4", R.drawable.m4a4, "$3100"));
                myItems.add(new Item("SCAR-20", R.drawable.scar20, "$5000"));
                myItems.add(new Item("SG 553", R.drawable.sg553, "$3000"));
                myItems.add(new Item("SSG 08", R.drawable.scout, "$2000"));
                break;
            case SMG:
                // TODO: fill remaining items
                myItems.add(new Item("Generic SMG", R.drawable.scar20, "$2222"));
                break;
            case HEAVY:
                myItems.add(new Item("Generic Heavy", R.drawable.scar20, "$3333"));
                break;
            case PISTOL:
                myItems.add(new Item("Generic Pistol", R.drawable.scar20, "$4444"));
                break;
            case KNIFE:
                myItems.add(new Item("Generic Knife", R.drawable.scar20, "$5555"));
                break;
            case MAP:
                myItems.add(new Item("Generic Map", R.drawable.scar20, "$6666"));
                break;
            case CASE:
                myItems.add(new Item("Generic Case", R.drawable.scar20, "$7777"));
                break;
        }

    }

    private void populateListView() {
        ArrayAdapter<Item> adapter = new MyListAdapter(); //Array adapter for Item objects
        ListView list = (ListView) findViewById(R.id.items_listView); //Set up list
        list.setAdapter(adapter); //assign adapter to ListView
    }
    //finish this. open new activity when an item is clicked
    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.items_listView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                Item clickedRifle = myItems.get(position);
            }
        });
    }
    //Adapter for listview. Adapter inflates the layout for each row in its getview() method
    //and assigns the data to the individual views in the row. adapter takes an Array and converts the items into View objects to be loaded into the ListView container.
    private class MyListAdapter extends ArrayAdapter<Item> {
        public MyListAdapter() { //constructor
            super(ItemsActivity.this, R.layout.item_view, myItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Make sure we have a view to work with (may have been given null)
            View itemView = convertView; //?
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }

            //Find the rifle to work with
            Item currentRifle = myItems.get(position);

            //Fill the view
            ImageView imageView = (ImageView) itemView.findViewById(R.id.item_icon); //Find icon view
            imageView.setImageResource(currentRifle.getIconID());

            //Rifle name:
            TextView nameText = (TextView) itemView.findViewById(R.id.item_name);
            nameText.setText(currentRifle.getItemName());

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
