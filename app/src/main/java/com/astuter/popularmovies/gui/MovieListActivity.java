package com.astuter.popularmovies.gui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.Toast;

import com.astuter.popularmovies.R;
import com.astuter.popularmovies.adapter.SimpleMovieListAdapter;
import com.astuter.popularmovies.api.ApplicationConfig;
import com.astuter.popularmovies.api.Config;
import com.astuter.popularmovies.api.MovieListTask;
import com.astuter.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity implements MovieListTask.MovieListTaskListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private MovieListTask mMovieListTask;
    private List<Movie> movieList;
    private GridView movieGridView;
    private SimpleMovieListAdapter mSimpleMovieListAdapter;
    private SharedPreferences preferences;

    private static boolean loadingMore = true;
    private static int CURRENT_PAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        toolbar.setBackgroundColor(ContextCompat.getColor(MovieListActivity.this, R.color.black));

        // Get the SharedPreferences for user choice of movie short order
        preferences = PreferenceManager.getDefaultSharedPreferences(MovieListActivity.this);

        // This will hold list of movie objects
        movieList = new ArrayList<>();

        // Fetch Moive list from server
        fetchMovieList();

        movieGridView = (GridView) findViewById(R.id.movie_list);
        movieGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (movieGridView.getLastVisiblePosition() >= movieGridView.getCount() - 4) {

                        CURRENT_PAGE++;
                        fetchMovieList();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                int lastInScreen = firstVisibleItem + visibleItemCount;
//                if ((lastInScreen == totalItemCount) && !(loadingMore)) {
//
//                    mMovieListTask.execute(Config.API_MOVIE_DISCOVER + "&page=" + CURRENT_PAGE);
//                    CURRENT_PAGE++;
//                }
            }
        });

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        mSimpleMovieListAdapter = new SimpleMovieListAdapter(MovieListActivity.this, movieList, mTwoPane);
        movieGridView.setAdapter(mSimpleMovieListAdapter);
    }

    private void fetchMovieList() {

        if (Config.isNetworkAvailable(MovieListActivity.this)) {
            // Get the movie list from themoviedb.org API
            String sortBy = preferences.getString(Config.PREF_SHORT_ORDER, Config.API_POPULARITY_DESC);
            mMovieListTask = new MovieListTask(MovieListActivity.this);
            mMovieListTask.execute(Config.API_MOVIE_DISCOVER + "&page=" + CURRENT_PAGE + "&sort_by=" + sortBy);
        } else {
            Toast.makeText(MovieListActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void getMovieList(JSONArray result) {

        for (int index = 0; index < result.length(); index++) {
            try {
                JSONObject movieData = result.getJSONObject(index);

                Movie movie = new Movie();
                movie.setId(movieData.getString("id"));
                movie.setTitle(movieData.getString("title"));
                movie.setOverview(movieData.getString("overview"));
                movie.setPoster(Config.API_POSTER_PREFIX + movieData.getString("poster_path"));
                movie.setReleaseDate(movieData.getString("release_date"));
                movie.setPopularity(movieData.getLong("popularity"));
                movie.setVoteCount(movieData.getLong("vote_count"));
                movie.setVoteAverage(movieData.getLong("vote_average"));

                Call<ResponseBody> call = ApplicationConfig.getRetrofit().getMovieVideos(movieData.getString("id"), Config.API_KEY);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            Log.e("onResponse", " Body: " + response.body().string());
                            JSONObject video = new JSONObject(response.body().string());
                            JSONArray results = video.getJSONArray("results");

                            String[] videoLinks = new String[results.length()];
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject videoData = results.getJSONObject(0);
                                videoLinks[i] = videoData.getString("key");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("onFailure", t.toString());
                    }
                });

                movieList.add(movie);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        Log.e("getMovieList", "movieList: " + movieList.toString());
        mSimpleMovieListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        SharedPreferences.Editor editor = preferences.edit();

        int id = item.getItemId();
        switch (id) {
            case R.id.action_popularity:

                editor.putString(Config.PREF_SHORT_ORDER, Config.API_POPULARITY_DESC);
                editor.commit();

                CURRENT_PAGE = 1;
                movieList.removeAll(movieList);
                fetchMovieList();
                return true;
            case R.id.action_rating:

                editor.putString(Config.PREF_SHORT_ORDER, Config.API_RATING_DESC);
                editor.commit();

                CURRENT_PAGE = 1;
                movieList.removeAll(movieList);
                fetchMovieList();
                return true;
            case R.id.action_favorite:

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
