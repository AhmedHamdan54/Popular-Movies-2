package com.example.ahmed.popularmovies2.listeners;

    import com.example.ahmed.popularmovies2.model.Movies;

    public interface OnMovieListFragmentListener {
        void onMoviesSelected(Movies movie);
        void onFavoriteMovieSelected(Movies movie);
        void onUpdateMoviesListVisibility();
        void onUpdateMovieDetails();
    }


