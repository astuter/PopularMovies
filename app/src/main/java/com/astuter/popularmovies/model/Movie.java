package com.astuter.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by astute on 01/03/16.
 */
public class Movie implements Parcelable {
    private String id;
    private String title;
    private String poster;
    private String releaseDate;
    private String overview;
    private float popularity;
    private float voteAverage;
    private long voteCount;
    private String[] video;

    public Movie(){

    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(title);
        out.writeString(poster);
        out.writeString(releaseDate);
        out.writeString(overview);
        out.writeFloat(popularity);
        out.writeFloat(voteAverage);
        out.writeLong(voteCount);
        out.writeStringArray(video);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel in) {
        id = in.readString();
        title = in.readString();
        poster = in.readString();
        releaseDate = in.readString();
        overview = in.readString();
        popularity = in.readFloat();
        voteAverage = in.readFloat();
        voteCount = in.readLong();
        video = in.createStringArray();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(long voteCount) {
        this.voteCount = voteCount;
    }

    public String[] getVideos() {
        return video;
    }

    public void setVideos(String[] videos) {
        this.video = videos;
    }

    public void setVideoListSize(int size){
            video = new String[size];
    }
}
