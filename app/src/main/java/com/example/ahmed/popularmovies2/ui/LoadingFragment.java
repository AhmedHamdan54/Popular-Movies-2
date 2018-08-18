package com.example.ahmed.popularmovies2.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import com.example.ahmed.popularmovies2.R;
public class LoadingFragment extends Fragment {

    public static final String FRAGMENT_TAG = LoadingFragment.class.getSimpleName();
    private static final String LOG_TAG = LoadingFragment.class.getSimpleName();

    public LoadingFragment() {
        // Required empty public constructor
    }

    public static LoadingFragment newInstance() {
        LoadingFragment fragment = new LoadingFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading, container, false);
        return view;
    }
}
