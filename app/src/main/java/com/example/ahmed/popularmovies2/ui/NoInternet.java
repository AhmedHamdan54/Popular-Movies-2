package com.example.ahmed.popularmovies2.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ahmed.popularmovies2.R;
import com.example.ahmed.popularmovies2.listeners.OnNoInternetFragmentListener;


public class NoInternet extends android.support.v4.app.Fragment {

    private static final String LOG_TAG = NoInternet.class.getSimpleName();

    private OnNoInternetFragmentListener mOnNoInternetFragmentListener;

    public NoInternet() {
        // Required empty public constructor
    }

    public static NoInternet newInstance() {
        NoInternet fragment = new NoInternet();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_no_internet, container, false);
        Button retryButton = (Button) view.findViewById(R.id.retry_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRetryButtonPressed();
            }
        });
        return view;
    }

    // Calls listener implemented by host Activity
    private void onRetryButtonPressed() {
        if (mOnNoInternetFragmentListener != null) {
            mOnNoInternetFragmentListener.onRetry();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNoInternetFragmentListener) {
            mOnNoInternetFragmentListener = (OnNoInternetFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnNoInternetFragmentListener = null;
    }

}