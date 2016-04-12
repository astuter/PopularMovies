package com.astuter.popularmovies.api;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.astuter.popularmovies.R;
import com.astuter.popularmovies.gui.MovieListActivity;
import com.astuter.popularmovies.model.Movies;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by astute on 01/03/16.
 */
public class MovieListTask extends AsyncTask<String, Void, Boolean> {

    private MovieListActivity mMovieListActivity;
    private ProgressDialog dialog;


    public MovieListTask(MovieListActivity instance) {
        mMovieListActivity = instance;
        dialog = new ProgressDialog(instance);
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
            JSONArray movieList = response.getJSONArray("results");
            try {
                ActiveAndroid.beginTransaction();
                for (int index = 0; index < movieList.length(); index++) {

                    JSONObject movieData = movieList.getJSONObject(index);

                    Movies oldMovie = Config.isMovieExists(movieData.getString("id"));
                    int isFavorite = oldMovie != null ? oldMovie.isFavorite : 0;
                    if (oldMovie != null) {
                        new Delete().from(Movies.class).where(Config.COLM_MOVIE_ID + " = ?", movieData.getString("id")).execute();
                    }

                    Movies movie = new Movies();
                    movie.movieId = movieData.getString("id");
                    movie.title = movieData.getString("title");
                    movie.overview = movieData.getString("overview");
                    movie.poster = Config.API_POSTER_PREFIX + movieData.getString("poster_path");
                    movie.releaseDate = movieData.getString("release_date");
                    movie.popularity = movieData.getLong("popularity");
                    movie.voteCount = movieData.getLong("vote_count");
                    movie.voteAverage = movieData.getLong("vote_average");
                    movie.isFavorite = isFavorite;

                    movie.save();

                    mMovieListActivity.getMovieList().add(movie);
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
                mMovieListActivity.gotMovieData();
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
        this.dialog.setMessage(mMovieListActivity.getResources().getString(R.string.movie_task_dialog));
        this.dialog.show();
    }

    public interface MovieListTaskListener {
        public void gotMovieData();
    }
}
