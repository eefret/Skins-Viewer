package com.chrslee.csgopedia.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;


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

        showChangelog();

        final Intent intent = new Intent(this, ItemsActivity.class);

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

    public void showChangelog() {
        // Show changelog once
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int version = pInfo.versionCode;

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if (prefs.getInt("changelogVersion", 0) != version) {
                ChangeLogDialog _ChangelogDialog = new ChangeLogDialog(this);
                _ChangelogDialog.show();

                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("changelogVersion", version);
                editor.commit();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent i = new Intent(this, UserSettingsActivity.class);
                startActivityForResult(i, 1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
