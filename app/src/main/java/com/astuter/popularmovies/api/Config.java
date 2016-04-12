package com.astuter.popularmovies.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.view.View;
import android.view.ViewGroup;



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

    public static final String API_KEY = "";
    public static final String API_BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static final String API_MOVIE_POPULAR = API_BASE_URL + "popular?api_key=" + API_KEY;
    public static final String API_MOVIE_TOP_RATED = API_BASE_URL + "top_rated?api_key=" + API_KEY;
    public static final String MOVIE_VIDEO_POSTFIX = "/videos?api_key=" + API_KEY;
    public static final String MOVIE_REVIEW_POSTFIX = "/reviews?api_key=" + API_KEY;
    public static final String API_POSTER_PREFIX = "http://image.tmdb.org/t/p/w185";

    public static final String PREF_MOVIE_SORT_TYPE = "PREF_MOVIE_SORT_TYPE";
    public static final String SORT_TYPE_POPULAR = "POPULAR";
    public static final String SORT_TYPE_TOP_RATED = "TOP_RATED";

    public static final String MOVIE_EXTRA = "MOVIE_EXTRA";
    public static final String IS_TWO_PANE = "IS_TWO_PANE";
    public static final String MOVIE_IS_FAVORITE = "MOVIE_IS_FAVORITE";

    public static final String ACTION_MOVIE_FAVORITE = "ACTION_MOVIE_FAVORITE";

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


    public static Movies isMovieExists(String id) {
        return new Select()
                .from(Movies.class)
                .where(Config.COLM_MOVIE_ID + " = ?", id)
                .executeSingle();
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
                .where(Config.COLM_MOVIE_IS_FAVORITE + " = ? ", 1)
                .orderBy(sortBy + " DESC")
                .execute();
    }

    public static Videos isVideoExists(String id) {
        return new Select()
                .from(Videos.class)
                .where(Config.COLM_VIDEO_ID + " = ?", id)
                .executeSingle();
    }

    public static List<Videos> getAllVideos(String movieId) {
        return new Select()
                .from(Videos.class)
                .where(Config.COLM_VIDEO_MOVIE_ID + " = ?", movieId)
                .execute();
    }

    public static Reviews isReviewExists(String id) {
        return new Select()
                .from(Reviews.class)
                .where(Config.COLM_REVIEW_ID + " = ?", id)
                .executeSingle();

    }

    public static List<Reviews> getAllReviews(String movieId) {
        return new Select()
                .from(Reviews.class)
                .where(Config.COLM_REVIEW_MOVIE_ID + " = ?", movieId)
                .execute();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
