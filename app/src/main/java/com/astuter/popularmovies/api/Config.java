package com.astuter.popularmovies.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by astute on 01/03/16.
 */
public class Config {

    public static final String API_KEY = "700dd36be912a62b6b29327b8aa992f9";
    public static final String API_BASE_URL = "https://api.themoviedb.org/3/";
    public static final String API_MOVIE_DISCOVER = API_BASE_URL + "discover/movie?sort_by=popularity.desc&api_key=" + API_KEY;
    public static final String API_MOVIE_VIDEOS = API_BASE_URL + "movie/id/videos?api_key=" + API_KEY;
    public static final String API_MOVIE_FIND = API_BASE_URL + "find/tt0266543?external_source=imdb_id&api_key=" + API_KEY; // tt0266543 is movie id
    public static final String API_POSTER_PREFIX = "http://image.tmdb.org/t/p/w185";

    public static final String API_POPULARITY_DESC = "popularity.desc";
    public static final String API_RATING_DESC = "vote_average.desc";
    public static final String PREF_SHORT_ORDER = "PREF_SHORT_ORDER";

    public static final String MOVIE_EXTRA = "MOVIE_EXTRA";

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
}
