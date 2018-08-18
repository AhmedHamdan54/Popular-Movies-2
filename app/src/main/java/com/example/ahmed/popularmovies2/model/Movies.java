package com.example.ahmed.popularmovies2.model;


    import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

    // Movie class that is used to store Movie data
    public class Movies implements Parcelable {

        private String mId;
        private String mTitle;
        private String mReleaseDate;
        private String mVoteAverage;
        private String mOverview;
        private Uri mPosterUri;
        private Videos[] mVideos;
        private Reviews[] mReviews;

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
            public Movies createFromParcel(Parcel in) {
                return new Movies(in);
            }

            public Movies[] newArray(int size) {
                return new Movies[size];
            }
        };

        public Movies(String id, String title, String releaseDate, String voteAverage, String
                overview, Uri posterUri) {
            mId = id;
            mTitle = title;
            mReleaseDate = releaseDate;
            mVoteAverage = voteAverage;
            mOverview = overview;
            mPosterUri = posterUri;
        }

        private Movies(Parcel in) {
            mId = in.readString();
            mTitle = in.readString();
            mReleaseDate = in.readString();
            mVoteAverage = in.readString();
            mOverview = in.readString();

            Object tempObj = in.readValue(Movies.class.getClassLoader());
            if (tempObj instanceof Uri) {
                mPosterUri = (Uri) tempObj;
            } else {
                mPosterUri = null;
            }

            mVideos = in.createTypedArray(Videos.CREATOR);
            mReviews = in.createTypedArray(Reviews.CREATOR);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int i) {
            dest.writeString(mId);
            dest.writeString(mTitle);
            dest.writeString(mReleaseDate);
            dest.writeString(mVoteAverage);
            dest.writeString(mOverview);
            dest.writeValue(mPosterUri);
            if (mVideos != null) {
                dest.writeTypedArray(mVideos, 0);
            }
            if (mReviews != null) {
                dest.writeTypedArray(mReviews, 0);
            }
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(String title) {
            mTitle = title;
        }

        public String getId() {
            return mId;
        }

        public void setId(String id) {
            mId = id;
        }

        public String getReleaseDate() {
            return mReleaseDate;
        }

        public void setReleaseDate(String releaseDate) {
            mReleaseDate = releaseDate;
        }

        public String getVoteAverage() {
            return mVoteAverage;
        }

        public void setVoteAverage(String voteAverage) {
            mVoteAverage = voteAverage;
        }

        public String getOverview() {
            return mOverview;
        }

        public void setOverview(String overview) {
            mOverview = overview;
        }

        public Uri getPosterUri() {
            return mPosterUri;
        }

        public void setPosterUri(Uri posterUri) {
            mPosterUri = posterUri;
        }

        public Videos[] getVideos() {
            return mVideos;
        }

        public void setVideos(Videos[] videos) {
            mVideos = videos;
        }

        public Reviews[] getReviews() {
            return mReviews;
        }

        public void setReviews(Reviews[] reviews) {
            mReviews = reviews;
        }
    }
