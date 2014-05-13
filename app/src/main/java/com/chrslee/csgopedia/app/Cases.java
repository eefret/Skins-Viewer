package com.chrslee.csgopedia.app;

import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Cases extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cases);

        LinearLayout scrollChild = (LinearLayout) findViewById(R.id.casesScrollChild);
        scrollChild.addView(getCaseView("CS:GO Weapon Case", R.drawable.csgo_weapon_case));
        scrollChild.addView(getCaseView("ESports 2013 Case", R.drawable.esports_2013_case));
        scrollChild.addView(getCaseView("Bravo Weapon Case", R.drawable.bravo_case));
        scrollChild.addView(getCaseView("Weapon Case 2", R.drawable.weapon_case_two));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cases, menu);
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

    // Returns a view with the text and image provided
    // Used for creating views for the cases
    private View getCaseView(String caseName, int drawableId) {
        View caseView = getLayoutInflater().inflate(R.layout.item_case, null);

        TextView caseItemName = (TextView) caseView.findViewById(R.id.case_item_name);
        caseItemName.setText(caseName);

        ImageView caseItemImage = (ImageView) caseView.findViewById(R.id.case_item_image);
        Drawable drawable = getResources().getDrawable(drawableId);
        caseItemImage.setImageDrawable(drawable);

        return caseView;
    }
}
