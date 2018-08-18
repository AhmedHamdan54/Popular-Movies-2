package com.example.ahmed.popularmovies2.ui;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ahmed.popularmovies2.R;
import com.example.ahmed.popularmovies2.common.Constants;
import com.example.ahmed.popularmovies2.data.FavoriteMoviesContract;
import com.example.ahmed.popularmovies2.listeners.OnLoadingFragmentListener;
import com.example.ahmed.popularmovies2.listeners.OnMovieListFragmentListener;
import com.example.ahmed.popularmovies2.model.Movies;
import com.example.ahmed.popularmovies2.service.MoviesIntentService;
import com.example.ahmed.popularmovies2.common.utils;

import java.util.ArrayList;
import java.util.Collections;


// Class containing a list of movies
public class MoviesFragmentList extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MoviesActivity.class.getSimpleName();
    private static final String STATE_LAYOUT_MANAGER = "state_recycler_view";
    private static final String STATE_MOVIES_LIST = "state_movies_list";
    private static final String SAVE_LAST_UPDATE_ORDER = "save_last_update_order";
    private static final String SAVE_LAST_SORT_ORDER = "save_last_sort_order";
    private static final int LOADER_FAVORITE_MOVIES = 1;

    private final ResponseReceiver mReceiver = new ResponseReceiver();
    private Context mContext;
    private OnMovieListFragmentListener mOnMovieListFragmentListener;
    private OnLoadingFragmentListener mOnLoadingFragmentListener;
    private FavoriteMoviesRVAdapter mFavoriteMoviesRVAdapter;
    private AdapterRVMovies mAdapterRVMovies;
    private ArrayList<Movies> mMoviesList;
    private String mLastUpdateOrder;
    private String mLastSortOrder;
    private DynamicSpanCountRV mRecyclerView;
    private Parcelable mLayoutManager;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MoviesFragmentList() {
    }

    // Create new Fragment instance
    public static MoviesFragmentList newInstance() {
        MoviesFragmentList fragment = new MoviesFragmentList();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!utils.isFavoriteSort(mContext)) {
            if (savedInstanceState == null) {
                updateMoviesList();
            }
        }
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movies_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_settings:
                mContext.startActivity(new Intent(mContext, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_LAST_UPDATE_ORDER, mLastUpdateOrder);
        outState.putString(SAVE_LAST_SORT_ORDER, mLastSortOrder);

        if (mRecyclerView != null) {
            outState.putParcelable(STATE_LAYOUT_MANAGER, mRecyclerView.getLayoutManager()
                    .onSaveInstanceState());
        }
        if (mMoviesList != null) {
            outState.putParcelableArrayList(STATE_MOVIES_LIST, mMoviesList);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        String currentSortOrder = utils.getSortPref(mContext);
        chooseAdapter(currentSortOrder);
        updateMoviesListVisibility(currentSortOrder);
        mLastSortOrder = currentSortOrder;

        if (!TextUtils.equals(mLastUpdateOrder, utils.getSortPref(mContext))) {
            updateMoviesList();
        }

        if (utils.isFavoriteSort(mContext)) {
            if (mOnLoadingFragmentListener != null) {
                mOnLoadingFragmentListener.onLoadingDisplay(false, false);
            }
        }
    }

    private void updateMoviesListVisibility(String currentSortOrder) {
        if (!TextUtils.equals(mLastSortOrder, currentSortOrder)
                || !TextUtils.equals(mLastUpdateOrder, currentSortOrder)) {
            if (mOnMovieListFragmentListener != null) {
                mOnMovieListFragmentListener.onUpdateMoviesListVisibility();
            }
        }
    }

    // Starts AsyncTask to fetch The Movies DB API
    public void updateMoviesList() {
        if (utils.isInternetConnected(getActivity()) && !utils.isFavoriteSort(mContext)) {
            String currentSortOrder = utils.getSortPref(mContext);
            mLastUpdateOrder = currentSortOrder;
            Intent intent = new Intent(mContext, MoviesIntentService.class);
            intent.setAction(Constants.ACTION_MOVIES_REQUEST);
            intent.putExtra(MoviesIntentService.EXTRA_MOVIES_SORT, currentSortOrder);
            mContext.startService(intent);

            if (mOnLoadingFragmentListener != null) {
                mOnLoadingFragmentListener.onLoadingDisplay(false, true);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies_list, container, false);

        if (view instanceof DynamicSpanCountRV) {
            mRecyclerView = (DynamicSpanCountRV) view;
            mMoviesList = new ArrayList<>();

            if (savedInstanceState != null) {
                mLastUpdateOrder = savedInstanceState.getString(SAVE_LAST_UPDATE_ORDER);
                mLastSortOrder = savedInstanceState.getString(SAVE_LAST_SORT_ORDER);
                mLayoutManager = savedInstanceState.getParcelable(STATE_LAYOUT_MANAGER);
                mMoviesList = savedInstanceState.getParcelableArrayList(STATE_MOVIES_LIST);
                mRecyclerView.getLayoutManager().onRestoreInstanceState(mLayoutManager);
            }

            chooseAdapter(utils.getSortPref(mContext));
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnMovieListFragmentListener) {
            mOnMovieListFragmentListener = (OnMovieListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMovieListFragmentListener");
        }

        if (context instanceof OnLoadingFragmentListener) {
            mOnLoadingFragmentListener = (OnLoadingFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoadingFragmentListener");
        }

        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnMovieListFragmentListener = null;
        mOnLoadingFragmentListener = null;
    }


    public class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                return;
            }

            if (intent.getAction().equals(Constants.ACTION_MOVIES_RESULT) && intent.hasExtra
                    (MoviesIntentService.EXTRA_MOVIES_RESULT)) {
                Movies[] movies = (Movies[]) intent.getParcelableArrayExtra(MoviesIntentService
                        .EXTRA_MOVIES_RESULT);

                if (mAdapterRVMovies != null && mMoviesList != null && movies != null) {
                    mAdapterRVMovies.clearRecyclerViewData();
                    Collections.addAll(mMoviesList, movies);
                    mAdapterRVMovies.notifyItemRangeInserted(0, movies.length);
                }
            } else {
                Toast.makeText(mContext, R.string.toast_failed_to_retrieve_data, Toast.LENGTH_SHORT)
                        .show();
            }

            if (mOnLoadingFragmentListener != null) {
                mOnLoadingFragmentListener.onLoadingDisplay(false, false);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(mContext)
                    .registerReceiver(mReceiver, new IntentFilter(Constants.ACTION_MOVIES_RESULT));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mReceiver);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_FAVORITE_MOVIES:
                return new CursorLoader(mContext, FavoriteMoviesContract.MoviesEntry
                        .CONTENT_URI,
                        null, null, null, null);
            default:
                Log.d(LOG_TAG, "Couldn't find loader");
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mFavoriteMoviesRVAdapter != null) {
            mFavoriteMoviesRVAdapter.swapCursor(data);
            if (data != null && data.getCount() <= 0) {
                Toast.makeText(mContext, R.string.favorites_empty, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mFavoriteMoviesRVAdapter != null) {
            mFavoriteMoviesRVAdapter.swapCursor(null);
        }
    }

    private void chooseAdapter(String currentSortOrder) {

        if (!TextUtils.equals(mLastUpdateOrder, currentSortOrder) && mAdapterRVMovies
                != null) {
            mAdapterRVMovies.clearRecyclerViewData();
        }

        RecyclerView.Adapter currentAdapter = mRecyclerView.getAdapter();

        if (utils.isFavoriteSort(mContext, currentSortOrder)
                && !(currentAdapter instanceof FavoriteMoviesRVAdapter)) {
            mLastUpdateOrder = currentSortOrder;
            mFavoriteMoviesRVAdapter = new FavoriteMoviesRVAdapter
                    (mOnMovieListFragmentListener);
            mRecyclerView.setAdapter(mFavoriteMoviesRVAdapter);

            getLoaderManager().initLoader(LOADER_FAVORITE_MOVIES, null, this);


        } else if (!utils.isFavoriteSort(mContext, currentSortOrder)
                && !(currentAdapter instanceof AdapterRVMovies)) {

            mAdapterRVMovies = new AdapterRVMovies(mMoviesList,
                    mOnMovieListFragmentListener);
            mRecyclerView.setAdapter(mAdapterRVMovies);

        }

        // Instantiate an empty Details Fragment if sort order has changed
        if (!TextUtils.equals(mLastSortOrder, currentSortOrder)
                && mOnMovieListFragmentListener != null) {
            mOnMovieListFragmentListener.onUpdateMovieDetails();
        }
    }
}