package com.example.ahmed.popularmovies2.ui;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ahmed.popularmovies2.R;
import com.example.ahmed.popularmovies2.listeners.OnMovieListFragmentListener;
import com.example.ahmed.popularmovies2.model.Movies;

import java.util.List;


    // Class that manages the list of movies (RecyclerView)
    public class AdapterRVMovies extends RecyclerView.Adapter<AdapterRVMovies
            .ViewHolder> {

        private static final String LOG_TAG = AdapterRVMovies.class.getSimpleName();

        private final List<Movies> mMoviesList;
        private final OnMovieListFragmentListener mListener;

        public AdapterRVMovies(List<Movies> moviesList, OnMovieListFragmentListener
                listener) {
            mMoviesList = moviesList;
            mListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_movies_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mMoviesList.get(position);

            Glide.with(holder.mPosterView.getContext()).load(holder.mItem.getPosterUri())
                    .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate().into(holder.mPosterView);

            holder.mTitle.setText(holder.mItem.getTitle());
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onMoviesSelected(holder.mItem);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mMoviesList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public Movies mItem;

            public final View mView;
            public final ImageView mPosterView;
            public final TextView mTitle;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mPosterView = (ImageView) view.findViewById(R.id.poster);
                mTitle = (TextView) view.findViewById(R.id.title);
            }

        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            Glide.clear(holder.mPosterView);
        }
        // Method implementation based on http://stackoverflow
        // .com/questions/29978695/remove-all-items-from-recyclerview
        // It resets the list and notifies the adapter
        public void clearRecyclerViewData() {
            int size = mMoviesList.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    mMoviesList.remove(0);
                }
                notifyItemRangeRemoved(0, size);
            }
        }
    }
