package com.astuter.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.astuter.popularmovies.api.Config;

/**
 * Created by Astuter on 13/03/16.
 */

@Table(name = Config.TABLE_MOVIE)
public class Movies extends Model implements Parcelable {

    @Column(name = Config.COLM_MOVIE_ID, index = true)
    public String movieId;

    @Column(name = Config.COLM_MOVIE_TITLE)
    public String title;

    @Column(name = Config.COLM_MOVIE_POSTER)
    public String poster;

    @Column(name = Config.COLM_MOVIE_RELEASE_DATE)
    public String releaseDate;

    @Column(name = Config.COLM_MOVIE_OVERVIEW)
    public String overview;

    @Column(name = Config.COLM_MOVIE_POPULARITY)
    public float popularity;

    @Column(name = Config.COLM_MOVIE_VOTE_AVERAGE)
    public float voteAverage;

    @Column(name = Config.COLM_MOVIE_VOTE_COUNT)
    public long voteCount;

    @Column(name = Config.COLM_MOVIE_IS_FAVORITE)
    public int isFavorite;

    public Movies() {

    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(movieId);
        out.writeString(title);
        out.writeString(poster);
        out.writeString(releaseDate);
        out.writeString(overview);
        out.writeFloat(popularity);
        out.writeFloat(voteAverage);
        out.writeLong(voteCount);
        out.writeInt(isFavorite);
    }

    public static final Parcelable.Creator<Movies> CREATOR = new Parcelable.Creator<Movies>() {
        public Movies createFromParcel(Parcel in) {
            return new Movies(in);
        }

        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };

    private Movies(Parcel in) {
        movieId = in.readString();
        title = in.readString();
        poster = in.readString();
        releaseDate = in.readString();
        overview = in.readString();
        popularity = in.readFloat();
        voteAverage = in.readFloat();
        voteCount = in.readLong();
        isFavorite = in.readInt();
    }
}
