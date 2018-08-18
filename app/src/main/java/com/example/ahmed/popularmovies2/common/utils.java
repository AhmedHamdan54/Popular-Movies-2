package com.example.ahmed.popularmovies2.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import com.example.ahmed.popularmovies2.data.FavoriteMoviesContract;
import com.example.ahmed.popularmovies2.R;
import com.example.ahmed.popularmovies2.model.Movies;
import com.example.ahmed.popularmovies2.model.Reviews;
import com.example.ahmed.popularmovies2.model.Videos;

// Utility class
public class utils {

    private static final String LOG_TAG = utils.class.getSimpleName();

    // Method based on http://stackoverflow.com/
    // questions/9113895/how-to-check-if-an-imageview-is-attached-with-image-in-android
    public static boolean hasImage(ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
        }

        return hasImage;
    }

    // Method to retrieve Bitmap from ImageView
    public static Bitmap getBitmapFromImageView(ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);
        Bitmap bitmap = null;

        if (hasImage) {
            if (drawable instanceof GlideBitmapDrawable) {
                bitmap = ((GlideBitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            }
        }

        return bitmap;
    }

    // Method based on http://stackoverflow.com
    // /questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
    public static void saveBitmapToInternalStorage(Context ctx, Bitmap bitmapImage,
                                                   String fileName) {
        FileOutputStream fos = null;
        try {
            fos = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            Log.d(LOG_TAG, "IOException while saving poster to internal storage");
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                Log.d(LOG_TAG, "IOException while saving poster to internal storage");
                e.printStackTrace();
            }
        }
    }

    // Method to delete a file from app internal storage
    public static boolean deleteFileFromInternalStorage(Context ctx, String fileName) {
        File dir = ctx.getFilesDir();
        File file = new File(dir, fileName);
        return file.delete();
    }

    // Method that creates a Movies object from a cursor
    public static Movies createMovieFromCursor(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.MoviesEntry._ID));
        String title = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.MoviesEntry
                .COLUMN_TITLE));
        String releaseDate = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract
                .MoviesEntry.COLUMN_RELEASE_DATE));
        String voteAverage = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract
                .MoviesEntry.COLUMN_VOTE_AVERAGE));
        String overview = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract
                .MoviesEntry.COLUMN_OVERVIEW));
        Uri posterUri = Uri.parse(cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract
                .MoviesEntry.COLUMN_PORTER_URI)));

        return new Movies(id, title, releaseDate, voteAverage, overview, posterUri);
    }

    // Method that creates a Videos array  from a cursor
    public static Videos[] createVideosFromCursor(Cursor cursor) {
        Videos[] videos = null;
        if (cursor != null) {
            videos = new Videos[cursor.getCount()];
            int videoIdColumnIndex = cursor.getColumnIndex(FavoriteMoviesContract.VideosEntry._ID);
            int videoKeyColumnIndex = cursor.getColumnIndex(FavoriteMoviesContract.VideosEntry
                    .COLUMN_KEY);
            int videoNameColumnIndex = cursor.getColumnIndex(FavoriteMoviesContract.VideosEntry
                    .COLUMN_NAME);

            while (cursor.moveToNext()) {
                videos[cursor.getPosition()] =
                        new Videos(cursor.getString(videoIdColumnIndex),
                                cursor.getString(videoKeyColumnIndex),
                                cursor.getString(videoNameColumnIndex));
            }
        }
        return videos;
    }

    // Method that creates a Movies array  from a cursor
    public static Reviews[] createReviewsFromCursor(Cursor cursor) {
        Reviews[] reviews = null;
        if (cursor != null) {
            reviews = new Reviews[cursor.getCount()];
            int reviewIdColumnIndex = cursor.getColumnIndex(FavoriteMoviesContract.ReviewsEntry
                    ._ID);
            int reviewAuthorColumnIndex = cursor.getColumnIndex(FavoriteMoviesContract
                    .ReviewsEntry.COLUMN_AUTHOR);
            int reviewContentColumnIndex = cursor.getColumnIndex(FavoriteMoviesContract
                    .ReviewsEntry.COLUMN_CONTENT);

            while (cursor.moveToNext()) {
                reviews[cursor.getPosition()] =
                        new Reviews(cursor.getString(reviewIdColumnIndex),
                                cursor.getString(reviewAuthorColumnIndex),
                                cursor.getString(reviewContentColumnIndex));
            }
        }
        return reviews;
    }

    // Method that gets current Sort preference
    public static String getSortPref(Context ctx) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(ctx.getString(R.string.pref_sort_order_key),
                ctx.getString(R.string.pref_popular_value));
    }

    // Method that checks if currentSort parameters is Favorite Sort
    public static boolean isFavoriteSort(Context ctx, String currentSort) {
        return TextUtils.equals(currentSort, ctx.getString(R.string.pref_favorites_value));

    }

    // Method that checks if current Sort preference is set to Favorite
    public static boolean isFavoriteSort(Context ctx) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        String currentSort = preferences.getString(ctx.getString(R.string.pref_sort_order_key),
                ctx.getString(R.string.pref_popular_value));
        return TextUtils.equals(currentSort, ctx.getString(R.string.pref_favorites_value));

    }

    /*
     * Method to check if internet connection is available or not.
     * Method from http://stackoverflow.com/questions/16481334/check-network-connection-in-fragment
     */
    public static boolean isInternetConnected(Context ctx) {

        ConnectivityManager connMgr = (ConnectivityManager)
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    // Converts date string with yyyy-MM-dd format to a string in current locale specific format
    public static String formatDateForLocale(String inputDateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date outputDate = inputFormat.parse(inputDateString);
            return DateFormat.getDateInstance().format(outputDate);
        } catch (ParseException e) {
            Log.d(LOG_TAG, "ParseException while parsing result from server." +
                    " Returning original date!");
            e.printStackTrace();
            return inputDateString;
        }
    }
}
