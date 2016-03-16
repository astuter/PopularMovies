package com.astuter.popularmovies.api;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.astuter.popularmovies.gui.MovieDetailFragment;
import com.astuter.popularmovies.model.Reviews;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by astute on 01/03/16.
 */
public class ReviewListTask extends AsyncTask<String, Void, Boolean> {

    private MovieDetailFragment mMovieDetailFragment;
    private ProgressDialog dialog;


    public ReviewListTask(MovieDetailFragment instance) {
        mMovieDetailFragment = instance;
        dialog = new ProgressDialog(instance.getActivity());
    }

    @Override
    protected Boolean doInBackground(String... params) {
        InputStream in = null;
        int resCode = -1;
        try {
            Log.e("doInBackground", "URL: " + params[0]);

            URL url = new URL(params[0]);
            URLConnection urlConn = url.openConnection();

            HttpURLConnection httpConn = (HttpURLConnection) urlConn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            resCode = httpConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }

            JSONObject response = new JSONObject(Config.readInputStream(in));
            JSONArray reviewList = response.getJSONArray("results");
            String movieId = response.getString("id");
            try {
                ActiveAndroid.beginTransaction();
                for (int index = 0; index < reviewList.length(); index++) {

                    JSONObject reviewData = reviewList.getJSONObject(index);

                    if (!Config.isReviewExists(reviewData.getString("id"))) {
                        new Delete().from(Reviews.class).where(Config.COLM_REVIEW_ID + " = ?", reviewData.getString("id")).execute();
                    }

                    Reviews review = new Reviews();
                    review.movieId = movieId;
                    review.reviewId = reviewData.getString("id");
                    review.author = reviewData.getString("author");
                    review.content = reviewData.getString("content");

                    review.save();

                    mMovieDetailFragment.getReviewList().add(review);
                }
                ActiveAndroid.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ActiveAndroid.endTransaction();
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if (success) {
            try {
                mMovieDetailFragment.gotReviewsData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
