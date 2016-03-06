package com.astuter.popularmovies.api;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.astuter.popularmovies.R;
import com.astuter.popularmovies.gui.MovieListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by astute on 01/03/16.
 */
public class MovieListTask extends AsyncTask<String, Void, JSONObject> {

    private MovieListActivity mMovieListActivity;
    private ProgressDialog dialog;


    public MovieListTask(MovieListActivity instance){
        mMovieListActivity = instance;
        dialog = new ProgressDialog(instance);
    }

    @Override
    protected JSONObject doInBackground(String... params) {

        InputStream in = null;
        int resCode = -1;
        try {
            Log.e("doInBackground","URL: "+ params[0]);

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
            return new JSONObject(Config.readInputStream(in));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject data) {
        super.onPostExecute(data);
        if (data != null) {
            try {
                JSONArray movieList = data.getJSONArray("results");
                mMovieListActivity.getMovieList(movieList);
            } catch (JSONException je) {
                je.printStackTrace();
            }
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog.setMessage(mMovieListActivity.getResources().getString(R.string.movie_task_dialog));
        this.dialog.show();
    }

    public interface MovieListTaskListener {
        public void getMovieList(JSONArray result);
    }
}
