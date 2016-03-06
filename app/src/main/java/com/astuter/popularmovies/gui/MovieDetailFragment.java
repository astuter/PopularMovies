package com.astuter.popularmovies.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuter.popularmovies.R;
import com.astuter.popularmovies.api.ApplicationConfig;
import com.astuter.popularmovies.api.Config;
import com.astuter.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {

    private Movie mMovie;
    private TextView title, overview, releaseDate, userRating;
    private ImageView poster;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(Config.MOVIE_EXTRA)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mMovie = getArguments().getParcelable(Config.MOVIE_EXTRA);

//            Activity activity = this.getActivity();
//            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//            if (appBarLayout != null) {
//                appBarLayout.setTitle(mMovie.getTitle());
//            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mMovie != null) {
            title = ((TextView) rootView.findViewById(R.id.title));
            overview = ((TextView) rootView.findViewById(R.id.overview));
            releaseDate = ((TextView) rootView.findViewById(R.id.release_date));
            userRating = ((TextView) rootView.findViewById(R.id.user_rating));
            poster = ((ImageView) rootView.findViewById(R.id.poster));

            title.setText(mMovie.getTitle());
            overview.setText(mMovie.getOverview());
            releaseDate.setText("Released on: " + mMovie.getReleaseDate());
            userRating.setText("User Rating: " + mMovie.getVoteAverage() + " out of 10");

            Picasso.with(getContext()).load(mMovie.getPoster()).placeholder(R.drawable.ic_movie_placeholder).into(poster);


        }

        return rootView;
    }
}
