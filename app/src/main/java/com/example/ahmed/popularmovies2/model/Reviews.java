package com.example.ahmed.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;
public class Reviews implements Parcelable {

    private String mId;
    private String mAuthor;
    private String mContent;

    public static final Parcelable.Creator<Reviews> CREATOR = new Parcelable.Creator<Reviews>() {
        public Reviews createFromParcel(Parcel in) {
            return new Reviews(in);
        }

        public Reviews[] newArray(int size) {
            return new Reviews[size];
        }
    };

    public Reviews(String id, String author, String content) {
        mId = id;
        mAuthor = author;
        mContent = content;
    }

    private Reviews(Parcel in) {
        mId = in.readString();
        mAuthor = in.readString();
        mContent = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(mId);
        dest.writeString(mAuthor);
        dest.writeString(mContent);
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }
}


