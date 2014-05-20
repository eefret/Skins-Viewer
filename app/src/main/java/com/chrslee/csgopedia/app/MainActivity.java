package com.chrslee.csgopedia.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.chrslee.csgopedia.app.util.Rifle;

import java.util.ArrayList;
import java.util.List;


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

        riflesButton = (Button) findViewById(R.id.rifles_button);
        riflesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RiflesActivity.class);
                startActivity(intent);
            }
        });

        smgsButton = (Button) findViewById(R.id.smgs_button);
        smgsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SMGsActivity.class);
                startActivity(intent);
            }
        });

        heavyButton = (Button) findViewById(R.id.heavy_button);
        heavyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HeavyActivity.class);
                startActivity(intent);
            }
        });

        pistolsButton = (Button) findViewById(R.id.pistols_button);
        pistolsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PistolsActivity.class);
                startActivity(intent);
            }
        });

        knivesButton = (Button) findViewById(R.id.knives_button);
        knivesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, KnivesActivity.class);
                startActivity(intent);
            }
        });

        mapsButton = (Button) findViewById(R.id.maps_button);
        mapsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        casesButton = (Button) findViewById(R.id.cases_button);
        casesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CasesActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
