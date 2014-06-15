package com.chrslee.csgopedia.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

    Button riflesButton;
    Button smgsButton;
    Button heavyButton;
    Button pistolsButton;
    Button knivesButton;
    Button mapsButton;
    Button casesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent intent = new Intent(this, ItemsActivity.class);
        ItemsDatabase.getInstance(this).getReadableDatabase(); // load db in main menu

        riflesButton = (Button) findViewById(R.id.rifles_button);
        riflesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            intent.putExtra("itemType", "Rifle");
            startActivity(intent);
            }
        });

        smgsButton = (Button) findViewById(R.id.smgs_button);
        smgsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            intent.putExtra("itemType", "SMG");
            startActivity(intent);
            }
        });

        heavyButton = (Button) findViewById(R.id.heavy_button);
        heavyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            intent.putExtra("itemType", "Heavy");
            startActivity(intent);
            }
        });

        pistolsButton = (Button) findViewById(R.id.pistols_button);
        pistolsButton.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
            intent.putExtra("itemType", "Pistol");
            startActivity(intent);
            }
        });

        knivesButton = (Button) findViewById(R.id.knives_button);
        knivesButton.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
            intent.putExtra("itemType", "Knife");
            startActivity(intent);
            }
        });

        mapsButton = (Button) findViewById(R.id.maps_button);
        mapsButton.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
            intent.putExtra("itemType", "Map");
            startActivity(intent);
            }
        });

        casesButton = (Button) findViewById(R.id.cases_button);
        casesButton.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
            intent.putExtra("itemType", "Case");
            startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
