package com.astuter.popularmovies.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.activeandroid.query.Select;
import com.astuter.popularmovies.model.Movies;
import com.astuter.popularmovies.model.Reviews;
import com.astuter.popularmovies.model.Videos;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by astuter on 01/03/16.
 */
public class Config {

    public static final String API_KEY = "700dd36be912a62b6b29327b8aa992f9";
    public static final String API_BASE_URL = "https://api.themoviedb.org/3/";
    public static final String API_MOVIE_DISCOVER = API_BASE_URL + "discover/movie?sort_by=popularity.desc&api_key=" + API_KEY;
    public static final String API_VIDEOS_PREFIX = API_BASE_URL + "movie/";
    public static final String API_VIDEOS_POSTFIX = "/videos?api_key=" + API_KEY;

    public static final String API_REVIEW_PREFIX = API_BASE_URL + "movie/";
    public static final String API_REVIEW_POSTFIX = "/reviews?api_key=" + API_KEY;

    public static final String API_MOVIE_FIND = API_BASE_URL + "find/tt0266543?external_source=imdb_id&api_key=" + API_KEY; // tt0266543 is movie id
    public static final String API_POSTER_PREFIX = "http://image.tmdb.org/t/p/w185";

    public static final String API_POPULARITY_DESC = "popularity.desc";
    public static final String API_RATING_DESC = "vote_average.desc";
    public static final String PREF_SHORT_ORDER = "PREF_SHORT_ORDER";

    public static final String MOVIE_EXTRA = "MOVIE_EXTRA";

    // ActiveAndroid: Table names
    public static final String TABLE_MOVIE = "Movies";
    public static final String TABLE_VIDEO = "Videos";
    public static final String TABLE_REVIEW = "Reviews";

    // ActiveAndroid: Column names for Movies
    public static final String COLM_MOVIE_ID = "mov_id";
    public static final String COLM_MOVIE_TITLE = "mov_title";
    public static final String COLM_MOVIE_OVERVIEW = "mov_overview";
    public static final String COLM_MOVIE_POSTER = "mov_poster";
    public static final String COLM_MOVIE_RELEASE_DATE = "mov_release_date";
    public static final String COLM_MOVIE_POPULARITY = "mov_popularity";
    public static final String COLM_MOVIE_VOTE_AVERAGE = "mov_vote_average";
    public static final String COLM_MOVIE_VOTE_COUNT = "mov_vote_count";
    public static final String COLM_MOVIE_IS_FAVORITE = "mov_is_favorite";

    // ActiveAndroid: Column names for Videos
    public static final String COLM_VIDEO_MOVIE_ID = "vdo_moive_id";
    public static final String COLM_VIDEO_ID = "vdo_id";
    public static final String COLM_VIDEO_NAME = "vdo_name";
    public static final String COLM_VIDEO_URL = "vdo_key";

    // ActiveAndroid: Column names for Reviews
    public static final String COLM_REVIEW_MOVIE_ID = "rview_moive_id";
    public static final String COLM_REVIEW_ID = "rview_id";
    public static final String COLM_REVIEW_AUTHOR = "rview_author";
    public static final String COLM_REVIEW_CONTENT = "rview_content";

    // Check if internet connection is available or not
    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Reads an InputStream and converts it to a String.
    public static String readInputStream(InputStream stream) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean isMovieExists(String id) {
        Movies movie = new Select()
                .from(Movies.class)
                .where(Config.COLM_MOVIE_ID + " = ?", id)
                .executeSingle();
        return movie == null;
    }

    public static List<Movies> getAllMovies(String sortBy) {
        return new Select()
                .from(Movies.class)
                .orderBy(sortBy + " DESC")
                .execute();
    }

    public static List<Movies> getFavoriteMovies(String sortBy) {
        return new Select()
                .from(Movies.class)
                .where(Config.COLM_MOVIE_IS_FAVORITE + " = 1")
                .orderBy(sortBy + " DESC")
                .execute();
    }

    public static boolean isVideoExists(String id) {
        Videos video = new Select()
                .from(Videos.class)
                .where(Config.COLM_VIDEO_ID + " = ?", id)
                .executeSingle();
        return video == null;
    }

    public static List<Videos> getAllVideos(String movieId) {
        return new Select()
                .from(Videos.class)
                .where(Config.COLM_VIDEO_MOVIE_ID + " = ?", movieId)
                .execute();
    }

    public static boolean isReviewExists(String id) {
        Reviews review = new Select()
                .from(Reviews.class)
                .where(Config.COLM_REVIEW_ID + " = ?", id)
                .executeSingle();
        return review == null;
    }

    public static List<Reviews> getAllReviews(String movieId) {
        return new Select()
                .from(Reviews.class)
                .where(Config.COLM_REVIEW_MOVIE_ID + " = ?", movieId)
                .execute();
    }
}
