package com.astuter.popularmovies.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.astuter.popularmovies.api.Config;

/**
 * Created by Astuter on 10/03/16.
 */

@Table(name = Config.TABLE_VIDEO)
public class Videos extends Model implements Parcelable {

    @Column(name = Config.COLM_VIDEO_MOVIE_ID, index = true)
    public String movieId;

    @Column(name = Config.COLM_VIDEO_ID, index = true)
    public String videoId;

    @Column(name = Config.COLM_VIDEO_URL)
    public String videoLink;

    @Column(name = Config.COLM_VIDEO_NAME)
    public String name;

    public Videos() {
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(movieId);
        out.writeString(videoId);
        out.writeString(videoLink);
        out.writeString(name);
    }

    public static final Parcelable.Creator<Videos> CREATOR = new Parcelable.Creator<Videos>() {
        public Videos createFromParcel(Parcel in) {
            return new Videos(in);
        }

        public Videos[] newArray(int size) {
            return new Videos[size];
        }
    };

    private Videos(Parcel in) {
        movieId = in.readString();
        videoId = in.readString();
        videoLink = in.readString();
        name = in.readString();
    }
}
