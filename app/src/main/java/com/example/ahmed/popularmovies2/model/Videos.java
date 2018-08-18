package com.example.ahmed.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;


public class Videos implements Parcelable {

    private String mId;
    private String mKey;
    private String mName;

    public static final Parcelable.Creator<Videos> CREATOR = new Parcelable.Creator<Videos>() {
        public Videos createFromParcel(Parcel in) {
            return new Videos(in);
        }

        public Videos[] newArray(int size) {
            return new Videos[size];
        }
    };

    public Videos(String id, String key, String name) {
        mId = id;
        mKey = key;
        mName = name;
    }

    private Videos(Parcel in) {
        mId = in.readString();
        mKey = in.readString();
        mName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(mId);
        dest.writeString(mKey);
        dest.writeString(mName);
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}


