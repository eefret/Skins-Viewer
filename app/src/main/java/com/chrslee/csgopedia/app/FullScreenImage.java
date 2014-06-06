package com.chrslee.csgopedia.app;

import android.app.Activity;
import android.os.Bundle;

/**
 * Shows a full screen image of the selected skin.
 * http://stackoverflow.com/questions/4915312/making-image-full-screen-on-android-tutorial-app
 */
public class FullScreenImage extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.full_screen_image);
        // Retrieve icon ID passed from SpecificItemsActivity
        int iconID = getIntent().getExtras().getInt("iconID");

        // TouchImageView allows additional functionality to ImageView (incl. zooming)
        TouchImageView iv = (TouchImageView) findViewById(R.id.fullImage);
        iv.setImageResource(iconID);
    }
}
