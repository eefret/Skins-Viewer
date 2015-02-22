package com.marvinsyan.csgoskinsviewer.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marvinsyan.csgoskinsviewer.R;

/**
 * Created by Marvin on 2/21/2015.
 */
public class ImageTabFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_image, container, false);
    }
}
