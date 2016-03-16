package com.astuter.popularmovies.api;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.astuter.popularmovies.R;
import com.astuter.popularmovies.gui.MovieDetailFragment;
import com.astuter.popularmovies.model.Videos;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by astute on 01/03/16.
 */
public class VideoListTask extends AsyncTask<String, Void, Boolean> {

    private MovieDetailFragment mMovieDetailFragment;
    private ProgressDialog dialog;


    public VideoListTask(MovieDetailFragment instance) {
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
            JSONArray videoList = response.getJSONArray("results");
            String movieId = response.getString("id");
            try {
                ActiveAndroid.beginTransaction();
                for (int index = 0; index < videoList.length(); index++) {

                    JSONObject videoData = videoList.getJSONObject(index);

                    if (!Config.isVideoExists(videoData.getString("id"))) {
                        new Delete().from(Videos.class).where(Config.COLM_VIDEO_ID + " = ?", videoData.getString("id")).execute();
                    }

                    Videos video = new Videos();
                    video.movieId = movieId;
                    video.videoId = videoData.getString("id");
                    video.name = videoData.getString("name");
                    video.videoLink = mMovieDetailFragment.getResources().getString(R.string.video_link_prefix) + videoData.getString("key");

                    video.save();

                    mMovieDetailFragment.getVideoList().add(video);
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
                mMovieDetailFragment.gotVideosData();
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
