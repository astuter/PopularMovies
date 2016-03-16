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

@Table(name = Config.TABLE_REVIEW)
public class Reviews extends Model implements Parcelable {

    @Column(name = Config.COLM_REVIEW_MOVIE_ID, index = true)
    public String movieId;

    @Column(name = Config.COLM_REVIEW_ID, index = true)
    public String reviewId;

    @Column(name = Config.COLM_REVIEW_AUTHOR)
    public String author;

    @Column(name = Config.COLM_REVIEW_CONTENT)
    public String content;

    public Reviews() {
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(movieId);
        out.writeString(reviewId);
        out.writeString(author);
        out.writeString(content);
    }

    public static final Parcelable.Creator<Reviews> CREATOR = new Parcelable.Creator<Reviews>() {
        public Reviews createFromParcel(Parcel in) {
            return new Reviews(in);
        }

        public Reviews[] newArray(int size) {
            return new Reviews[size];
        }
    };

    private Reviews(Parcel in) {
        movieId = in.readString();
        reviewId = in.readString();
        author = in.readString();
        content = in.readString();
    }

}
