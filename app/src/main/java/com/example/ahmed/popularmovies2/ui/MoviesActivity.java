package com.example.ahmed.popularmovies2.ui;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.ahmed.popularmovies2.R;
import com.example.ahmed.popularmovies2.common.Constants;
import com.example.ahmed.popularmovies2.common.utils;
import com.example.ahmed.popularmovies2.data.FavoriteMoviesContract;
import com.example.ahmed.popularmovies2.listeners.OnMovieListFragmentListener;
import com.example.ahmed.popularmovies2.listeners.OnNoInternetFragmentListener;
import com.example.ahmed.popularmovies2.listeners.OnLoadingFragmentListener;
import com.example.ahmed.popularmovies2.model.Movies;

public class MoviesActivity extends AppCompatActivity implements
        OnMovieListFragmentListener,
        OnLoadingFragmentListener,
        OnNoInternetFragmentListener {

    private static final String LOG_TAG = MoviesActivity.class.getSimpleName();
    private boolean mIsTwoPane;
    private View mMoviesFragmentContainer;
    private View mDetailsFragmentContainer;
    private View mNoInternetConnectionFragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        mMoviesFragmentContainer = findViewById(R.id.movies_fragment_container);
        mDetailsFragmentContainer = findViewById(R.id.details_fragment_container);
        mNoInternetConnectionFragmentContainer = findViewById(R.id.no_internet_container);

        mIsTwoPane = mDetailsFragmentContainer != null;

        if (savedInstanceState == null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            MoviesFragmentList moviesFragmentList = MoviesFragmentList.newInstance();
            NoInternet noInternet = NoInternet.newInstance();
            fragmentTransaction.add(R.id.movies_fragment_container, moviesFragmentList)
                    .add(R.id.no_internet_container, noInternet);

            if (mIsTwoPane) {
                DetailsFragment detailFragment = DetailsFragment.newInstance();
                fragmentTransaction.add(R.id.details_fragment_container, detailFragment);
            }

            fragmentTransaction.commit();
        }
    }

    // Change visibility of fragment according to current internet connection state
    private void changeNoInternetVisibility(boolean isInternetConnected) {

        if (isInternetConnected || utils.isFavoriteSort(this)) {
            mNoInternetConnectionFragmentContainer.setVisibility(View.GONE);
            mMoviesFragmentContainer.setVisibility(View.VISIBLE);

            if (mIsTwoPane) {
                mDetailsFragmentContainer.setVisibility(View.VISIBLE);
            }
        } else {
            mNoInternetConnectionFragmentContainer.setVisibility(View.VISIBLE);
            mMoviesFragmentContainer.setVisibility(View.GONE);

            if (mIsTwoPane) {
                mDetailsFragmentContainer.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRetry() {
        boolean isInternetConnected = utils.isInternetConnected(this);

        changeNoInternetVisibility(utils.isInternetConnected(this));

        if (!isInternetConnected) {
            Toast.makeText(this, R.string.toast_no_internet_connection, Toast.LENGTH_SHORT).show();

        } else {
            MoviesFragmentList moviesFragmentList = (MoviesFragmentList)
                    getSupportFragmentManager().findFragmentById(R.id.movies_fragment_container);
            moviesFragmentList.updateMoviesList();
        }
    }

    @Override
    public void onMoviesSelected(Movies moviesItem) {
        if (moviesItem != null) {
            if (!utils.isInternetConnected(this)) {
                Toast.makeText(this, R.string.toast_check_your_internet_connection,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (mIsTwoPane) {
                // In two-pane mode, show the detail view in this activity by
                // adding or replacing the detail fragment using a
                // fragment transaction.
                DetailsFragment detailsFragment = DetailsFragment.newInstance(moviesItem);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.details_fragment_container, detailsFragment).commit();
            } else {
                Intent intent = new Intent(this, DetailsActivity.class).putExtra(Constants
                                .EXTRA_MOVIE,
                        moviesItem);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onFavoriteMovieSelected(Movies moviesItem) {
        if (moviesItem != null) {
            int movieID = Integer.parseInt(moviesItem.getId());

            // Videos query
            Cursor videosCursor = getContentResolver().query(FavoriteMoviesContract.VideosEntry
                            .CONTENT_URI, null,
                    FavoriteMoviesContract.VideosEntry.COLUMN_MOVIE_ID + " = " + movieID, null,
                    null);
            if (videosCursor != null) {
                try {
                    moviesItem.setVideos(utils.createVideosFromCursor(videosCursor));
                } finally {
                    if (videosCursor != null) {
                        videosCursor.close();
                    }
                }
            }

            // Reviews query
            Cursor reviewsCursor = getContentResolver().query(FavoriteMoviesContract.ReviewsEntry
                            .CONTENT_URI, null,
                    FavoriteMoviesContract.ReviewsEntry.COLUMN_MOVIE_ID + " = " + movieID, null,
                    null);
            if (reviewsCursor != null) {
                try {
                    moviesItem.setReviews(utils.createReviewsFromCursor(reviewsCursor));
                } finally {
                    if (reviewsCursor != null) {
                        reviewsCursor.close();
                    }
                }
            }

            if (mIsTwoPane) {
                // In two-pane mode, show the detail view in this activity by
                // adding or replacing the detail fragment using a
                // fragment transaction.
                DetailsFragment detailsFragment = DetailsFragment.newInstance(moviesItem);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.details_fragment_container, detailsFragment).commit();
            } else {
                Intent intent = new Intent(this, DetailsActivity.class).putExtra(Constants
                        .EXTRA_MOVIE, moviesItem);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onUpdateMoviesListVisibility() {
        changeNoInternetVisibility(utils.isInternetConnected(this));
    }

    @Override
    public void onUpdateMovieDetails() {
        if (mIsTwoPane) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            DetailsFragment detailFragment = DetailsFragment.newInstance();
            fragmentTransaction.replace(R.id.details_fragment_container, detailFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onLoadingDisplay(boolean fromDetails, boolean display) {
        Fragment loadingFragment = getSupportFragmentManager()
                .findFragmentByTag(LoadingFragment.FRAGMENT_TAG);
        if (display && loadingFragment == null) {
            loadingFragment = LoadingFragment.newInstance();
            if (fromDetails) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.details_fragment_container,
                                loadingFragment, LoadingFragment.FRAGMENT_TAG).commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.movies_fragment_container,
                                loadingFragment, LoadingFragment.FRAGMENT_TAG).commit();
            }
        } else if (!display && loadingFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(loadingFragment).commit();
        }
    }
}

