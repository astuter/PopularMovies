package com.astuter.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.astuter.popularmovies.R;
import com.astuter.popularmovies.api.Config;
import com.astuter.popularmovies.gui.MovieDetailActivity;
import com.astuter.popularmovies.gui.MovieDetailFragment;
import com.astuter.popularmovies.gui.MovieListActivity;
import com.astuter.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by astute on 03/03/16.
 */


public class SimpleMovieListAdapter extends ArrayAdapter<Movie> {

    MovieListActivity mMovieListActivity;
    boolean isTwoPane;

    public SimpleMovieListAdapter(Context context, List<Movie> objects, boolean twoPane) {
        super(context, 0, objects);

        if (context instanceof MovieListActivity) {
            mMovieListActivity = (MovieListActivity) context;
        }
        this.isTwoPane = twoPane;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_grid_content, parent, false);
        }

        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
        final Movie movie = getItem(position);

        ImageView mPosterView = (ImageView) convertView.findViewById(R.id.poster);
        Picasso.with(mMovieListActivity).load(movie.getPoster()).placeholder(R.drawable.ic_movie_placeholder).into(mPosterView);

        mPosterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(Config.MOVIE_EXTRA, movie);
                    MovieDetailFragment fragment = new MovieDetailFragment();
                    fragment.setArguments(arguments);
                    mMovieListActivity.getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, fragment).commit();
                } else {

                    Intent intent = new Intent(mMovieListActivity, MovieDetailActivity.class);
                    intent.putExtra(Config.MOVIE_EXTRA, movie);
                    mMovieListActivity.startActivity(intent);
                }
            }
        });
        return convertView;
    }
}