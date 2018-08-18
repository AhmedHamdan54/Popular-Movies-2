package com.example.ahmed.popularmovies2.data;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;



public class FavoriteMoviesProvider extends ContentProvider {

    private static final int MOVIES = 100;
    private static final int MOVIES_ITEM = 101;
    private static final int VIDEOS = 200;
    private static final int VIDEOS_ITEM = 201;
    private static final int REVIEWS = 300;
    private static final int REVIEWS_ITEM = 301;
    private static final UriMatcher mUriMatcher = getUriMatcher();
    private FavoriteMoviesDB mFavoriteMoviesDB;

    @Override
    public boolean onCreate() {
        mFavoriteMoviesDB = new FavoriteMoviesDB(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[]
            selectionArgs,
                        String sortOrder) {

        Cursor cursor;
        String id;
        switch (mUriMatcher.match(uri)) {
            case MOVIES: {
                cursor = mFavoriteMoviesDB.getReadableDatabase().query(
                        FavoriteMoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case MOVIES_ITEM: {
                id = uri.getPathSegments().get(1);
                cursor = getItem(FavoriteMoviesContract.MoviesEntry.TABLE_NAME,
                        id, projection, selection, selectionArgs, sortOrder);
                break;
            }

            case VIDEOS: {
                cursor = mFavoriteMoviesDB.getReadableDatabase().query(
                        FavoriteMoviesContract.VideosEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case VIDEOS_ITEM: {
                id = uri.getPathSegments().get(1);
                cursor = getItem(FavoriteMoviesContract.VideosEntry.TABLE_NAME,
                        id, projection, selection, selectionArgs, sortOrder);
                break;
            }
            case REVIEWS: {
                cursor = mFavoriteMoviesDB.getReadableDatabase().query(
                        FavoriteMoviesContract.ReviewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case REVIEWS_ITEM: {
                id = uri.getPathSegments().get(1);
                cursor = getItem(FavoriteMoviesContract.MoviesEntry.TABLE_NAME,
                        id, projection, selection, selectionArgs, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    private Cursor getItem(String tableName, String id, String[] projection, String selection,
                           String[]
                                   selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
        sqliteQueryBuilder.setTables(tableName);

        if (id != null) {
            sqliteQueryBuilder.appendWhere("_id" + " = " + id);
        }

        Cursor cursor = sqliteQueryBuilder.query(mFavoriteMoviesDB.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case MOVIES:
                return FavoriteMoviesContract.MoviesEntry.CONTENT_TYPE;
            case MOVIES_ITEM:
                return FavoriteMoviesContract.MoviesEntry.CONTENT_ITEM_TYPE;
            case VIDEOS:
                return FavoriteMoviesContract.VideosEntry.CONTENT_TYPE;
            case VIDEOS_ITEM:
                return FavoriteMoviesContract.VideosEntry.CONTENT_ITEM_TYPE;
            case REVIEWS:
                return FavoriteMoviesContract.ReviewsEntry.CONTENT_TYPE;
            case REVIEWS_ITEM:
                return FavoriteMoviesContract.ReviewsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {

        final SQLiteDatabase db = mFavoriteMoviesDB.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES: {
                long _id = db.insert(FavoriteMoviesContract.MoviesEntry.TABLE_NAME, null,
                        contentValues);
                if (_id > 0)
                    returnUri = FavoriteMoviesContract.MoviesEntry.buildMoviesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case VIDEOS: {
                long _id = db.insert(FavoriteMoviesContract.VideosEntry.TABLE_NAME, null,
                        contentValues);
                if (_id > 0)
                    returnUri = FavoriteMoviesContract.VideosEntry.buildVideosUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEWS: {
                long _id = db.insert(FavoriteMoviesContract.ReviewsEntry.TABLE_NAME, null,
                        contentValues);
                if (_id > 0)
                    returnUri = FavoriteMoviesContract.ReviewsEntry.buildReviewsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mFavoriteMoviesDB.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case MOVIES:
                rowsDeleted = db.delete(FavoriteMoviesContract.MoviesEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case VIDEOS:
                rowsDeleted = db.delete(FavoriteMoviesContract.VideosEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case REVIEWS:
                rowsDeleted = db.delete(FavoriteMoviesContract.ReviewsEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = mFavoriteMoviesDB.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIES:
                rowsUpdated = db.update(FavoriteMoviesContract.MoviesEntry.TABLE_NAME, values,
                        selection,
                        selectionArgs);
                break;
            case VIDEOS:
                rowsUpdated = db.update(FavoriteMoviesContract.VideosEntry.TABLE_NAME, values,
                        selection,
                        selectionArgs);
                break;
            case REVIEWS:
                rowsUpdated = db.update(FavoriteMoviesContract.ReviewsEntry.TABLE_NAME, values,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    private static UriMatcher getUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavoriteMoviesContract.CONTENT_AUTHORITY, FavoriteMoviesContract
                .PATH_MOVIES, MOVIES);
        uriMatcher.addURI(FavoriteMoviesContract.CONTENT_AUTHORITY, FavoriteMoviesContract
                        .PATH_MOVIES + "/#",
                MOVIES_ITEM);
        uriMatcher.addURI(FavoriteMoviesContract.CONTENT_AUTHORITY, FavoriteMoviesContract
                .PATH_VIDEOS, VIDEOS);
        uriMatcher.addURI(FavoriteMoviesContract.CONTENT_AUTHORITY, FavoriteMoviesContract
                        .PATH_VIDEOS + "/#",
                VIDEOS_ITEM);
        uriMatcher.addURI(FavoriteMoviesContract.CONTENT_AUTHORITY, FavoriteMoviesContract
                .PATH_REVIEWS, REVIEWS);
        uriMatcher.addURI(FavoriteMoviesContract.CONTENT_AUTHORITY, FavoriteMoviesContract
                        .PATH_REVIEWS + "/#",
                REVIEWS_ITEM);
        return uriMatcher;
    }
}