/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chrslee.csgopedia.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.astuetz.PagerSlidingTabStrip;

// Note: ActionBarActivity also extends FragmentActivity
public class ImageAndPriceActivity extends ActionBarActivity {

    private static final int RESULT_SETTINGS = 1;
    private final Handler handler = new Handler();//Handler to controll the drawable callbacks
    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        //Callback of a drawable used to control the states of the drawable
        @Override
        public void invalidateDrawable(Drawable who) {
            //Triggered when the drawable is invalidated (taken out of screen)
            getSupportActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            //triggered when the drawable is being updated(setting a new image)
            handler.postAtTime(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            //Triggered When the prior action is canceled
            handler.removeCallbacks(what);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //same as in MainActivity
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLightTheme = prefs.getString("theme", "light").equals("light");
        if (isLightTheme) {
            setTheme(R.style.AppThemeLight);
        } else {
            setTheme(R.style.AppThemeDark);
        }

        //setting the layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_and_price);


        //Here in this activity we have a ViewPager this is basically
        //tabbed layout, each tab will contain a Fragment, a Fragment is like a
        //sub activity (a portion of an activity) and has its own view, it has also a lifecycle
        //You can read more about it here http://developer.android.com/guide/components/fragments.html
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);//the tabs of the viewpager casted to a Library custom tab class
        //The Viepager pulled from the XML to the pager var
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        //The Viewpager adapter, like the Listviews the viewpagers also need
        //An adapter to know which fragment will be in each tab
        //Here we pass the supporFragmentManager as we are dealing with fragments
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter);// setting the adapter to the viewpager

        //adding a little space between the tabs
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        // TODO: Bug - Black tab background completely overlaps indicator on dark theme
        if (!isLightTheme) {//Hack to change the bg of the tab
            tabs.setTabBackground(R.color.tab_background_black);
            tabs.setTextColor(Color.WHITE);
        }

        tabs.setViewPager(pager);//the tab layout must contain an instance of the viewpager to know which View will present with each tab
        tabs.setIndicatorColorResource(R.color.tab_indicator_cyan);//just the press action color

        // Navigation drawer
        //Same as in MainActivity
        final String[] values = getResources().getStringArray(R.array.nav_drawer_items);
        ((ListView) findViewById(R.id.left_drawer3)).setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, values));
        NavigationDrawerSetup nds = new NavigationDrawerSetup((ListView) findViewById(R.id.left_drawer3),
                (DrawerLayout) findViewById(R.id.drawer_layout), values, getSupportActionBar(), this);
        nds.configureDrawer();

        //Changing the title of the actionbar dinamically
        ActionBar bar = getSupportActionBar();
        String title = getIntent().getExtras().getString("searchQuery");
        if (!title.equals("-1")) {
            bar.setTitle(title);
        } else {
            bar.setTitle(getIntent().getExtras().getString("regularName"));
        }
    }

    //inflating the menu of this activity in /res/menu/settings_prices_fragment.xml
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_prices_fragment, menu);
        return true;
    }

    //actions for menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent settings = new Intent(this, UserSettingsActivity.class);
                startActivityForResult(settings, RESULT_SETTINGS);
                break;
            case R.id.open_market:
                String URL = "http://steamcommunity.com/market/search?q=appid%3A730+"
                        + getIntent().getExtras().getString("searchQuery");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
                startActivity(browserIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Not being used just overriden
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    //Not being used just overriden
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    //Not being used just overriden
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    //This is the pager adapter here we can see that he have the same fragment for each tab
    //which is kind of bad but anyway the tabs are Image and Prices
    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"Image", "Prices"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return CardFragment.newInstance(position);
        }

    }
}
