package com.example.ahmed.popularmovies2.ui;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ahmed.popularmovies2.R;
import com.example.ahmed.popularmovies2.data.FavoriteMoviesContract;
import com.example.ahmed.popularmovies2.listeners.OnMovieListFragmentListener;
import com.example.ahmed.popularmovies2.model.Movies;
import com.example.ahmed.popularmovies2.common.utils;

public class FavoriteMoviesRVAdapter extends RecyclerView
        .Adapter<FavoriteMoviesRVAdapter.ViewHolder> {

    private static final String LOG_TAG = FavoriteMoviesRVAdapter.class.getSimpleName();

    private final OnMovieListFragmentListener mOnMovieListFragmentListener;
    private Cursor mCursor;

    @Override
    public final void onBindViewHolder(final ViewHolder holder, final int position) {
        final Cursor cursor = getItem(position);
        onBindViewHolder(holder, cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_movies_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final ImageView mPosterView;
        public final TextView mTitle;

        public int mCursorPosition;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPosterView = (ImageView) view.findViewById(R.id.poster);
            mTitle = (TextView) view.findViewById(R.id.title);
            mCursorPosition = -1;
        }
    }

    public Cursor getItem(final int position) {
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.moveToPosition(position);
        }

        return mCursor;
    }

    public FavoriteMoviesRVAdapter(
            OnMovieListFragmentListener listener) {
        mOnMovieListFragmentListener = listener;
    }

    public void swapCursor(Cursor cursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = cursor;
        notifyDataSetChanged();
    }

    private void onBindViewHolder(final ViewHolder holder, final Cursor cursor) {
        String movieTitle = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract
                .MoviesEntry.COLUMN_TITLE));
        String posterUri = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract
                .MoviesEntry.COLUMN_PORTER_URI));
        int cursorPosition = cursor.getPosition();

        holder.mCursorPosition = cursorPosition;
        holder.mTitle.setText(movieTitle);

        Glide.with(holder.mPosterView.getContext()).load(posterUri)
                .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate().into(holder.mPosterView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnMovieListFragmentListener != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    cursor.moveToPosition(holder.mCursorPosition);
                    Movies movies = utils.createMovieFromCursor(cursor);
                    mOnMovieListFragmentListener.onFavoriteMovieSelected(movies);
                }
            }
        });
    }
}
