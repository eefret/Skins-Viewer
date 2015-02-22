package com.marvinsyan.csgoskinsviewer.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.marvinsyan.csgoskinsviewer.R;

/**
 * Displays the image and price information for a given skin in a pop-up window.
 * https://github.com/kiteflo/Android_Tabbed_Dialog/blob/master/TabbedDialog/src/main/java/com/jooik/tabbeddialog/fragments/FragmentDialog.java
 */
public class InfoDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog, container);

        // tab slider
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        return view;
    }

    /**
     * Used for tab paging...
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                // find first fragment...
                ImageTabFragment ft1 = new ImageTabFragment();
                return ft1;
            } else if (position == 1) {
                // find first fragment...
                PricesTabFragment ft2 = new PricesTabFragment();
                return ft2;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Skin Appearance";
                case 1:
                    return "Market Prices";
            }
            return null;
        }
    }
}
