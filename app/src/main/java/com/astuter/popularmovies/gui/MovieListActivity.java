package com.astuter.popularmovies.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.activeandroid.query.Update;
import com.astuter.popularmovies.R;
import com.astuter.popularmovies.adapter.MovieListAdapter;
import com.astuter.popularmovies.api.Config;
import com.astuter.popularmovies.api.MovieListTask;
import com.astuter.popularmovies.model.Movies;

import java.util.ArrayList;
import java.util.List;

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
    private List<Movies> movieList;
    private GridView movieGridView;
    private MovieListAdapter mMovieListAdapter;
    private SharedPreferences preferences;

    private static boolean loadingMore = true;
    private static int CURRENT_PAGE = 1;

    private IntentFilter movieFavFilter;
    private BroadcastReceiver moiveFavReceiver;
    private static int selectedMovieIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
//        toolbar.setBackgroundColor(ContextCompat.getColor(MovieListActivity.this, R.color.black));

        // Get the SharedPreferences for user choice of movie short order
        preferences = PreferenceManager.getDefaultSharedPreferences(MovieListActivity.this);

        movieFavFilter = new IntentFilter(Config.ACTION_MOVIE_FAVORITE);
        moiveFavReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    Log.e("onReceive in list", "position: " + selectedMovieIndex + " val: " + intent.getIntExtra(Config.MOVIE_IS_FAVORITE, 0));
                    Movies movie = movieList.get(selectedMovieIndex);
                    movie.isFavorite = intent.getIntExtra(Config.MOVIE_IS_FAVORITE, 0);
                    movie.save();

                    new Update(Movies.class)
                            .set(Config.COLM_MOVIE_IS_FAVORITE + " = ? ", intent.getIntExtra(Config.MOVIE_IS_FAVORITE, 0))
                            .where(Config.COLM_MOVIE_ID + " = ? ", movie.movieId)
                            .execute();
                }
            }
        };
        registerReceiver(moiveFavReceiver, movieFavFilter);

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
            }
        });



        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        // This will hold list of movie objects
        movieList = new ArrayList<>();
        mMovieListAdapter = new MovieListAdapter(MovieListActivity.this, movieList, mTwoPane);
        movieGridView.setAdapter(mMovieListAdapter);

        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMovieIndex = position;
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(Config.MOVIE_EXTRA, movieList.get(position));
                    arguments.putBoolean(Config.IS_TWO_PANE, mTwoPane);
                    MovieDetailFragment fragment = new MovieDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, fragment).commit();
                } else {
                    Intent intent = new Intent(MovieListActivity.this, MovieDetailActivity.class);
                    intent.putExtra(Config.MOVIE_EXTRA, movieList.get(position));
                    intent.putExtra(Config.IS_TWO_PANE, mTwoPane);
                    startActivity(intent);
                }
            }
        });

        // Fetch Moive list from server
        fetchMovieList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(moiveFavReceiver, movieFavFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(moiveFavReceiver);
    }

    private void fetchMovieList() {
        String sortType = preferences.getString(Config.PREF_MOVIE_SORT_TYPE, Config.SORT_TYPE_POPULAR);

        if (Config.isNetworkAvailable(MovieListActivity.this)) {
            // Get the movie list from themoviedb.org API
            mMovieListTask = new MovieListTask(MovieListActivity.this);
            String url = sortType.equalsIgnoreCase(Config.SORT_TYPE_POPULAR) ? Config.API_MOVIE_POPULAR : Config.API_MOVIE_TOP_RATED;
            mMovieListTask.execute(url + "&page=" + CURRENT_PAGE);

        } else {
            Toast.makeText(MovieListActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            // No internet connection, fetch data from local storage
            if (sortType.equalsIgnoreCase("POPULAR")) {
                movieList = new ArrayList<>(Config.getAllMovies(Config.COLM_MOVIE_POPULARITY));
            } else {
                movieList = new ArrayList<>(Config.getAllMovies(Config.COLM_MOVIE_VOTE_AVERAGE));
            }
            mMovieListAdapter = new MovieListAdapter(MovieListActivity.this, movieList, mTwoPane);
            movieGridView.setAdapter(mMovieListAdapter);
        }
    }

    public List<Movies> getMovieList() {
        return movieList;
    }

    @Override
    public void gotMovieData() {
        mMovieListAdapter.notifyDataSetChanged();

        if(mTwoPane){
            movieGridView.performItemClick(movieGridView, 0, movieGridView.getAdapter().getItemId(0));
        }
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

                editor.putString(Config.PREF_MOVIE_SORT_TYPE, Config.SORT_TYPE_POPULAR);
                editor.commit();

                CURRENT_PAGE = 1;
                movieList.removeAll(movieList);
                fetchMovieList();
                return true;
            case R.id.action_rating:

                editor.putString(Config.PREF_MOVIE_SORT_TYPE, Config.SORT_TYPE_TOP_RATED);
                editor.commit();

                CURRENT_PAGE = 1;
                movieList.removeAll(movieList);
                fetchMovieList();
                return true;
            case R.id.action_favorite:
                movieList.removeAll(movieList);
                movieList = new ArrayList<>(Config.getFavoriteMovies(Config.COLM_MOVIE_VOTE_AVERAGE));
                mMovieListAdapter = new MovieListAdapter(MovieListActivity.this, movieList, mTwoPane);
                movieGridView.setAdapter(mMovieListAdapter);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
