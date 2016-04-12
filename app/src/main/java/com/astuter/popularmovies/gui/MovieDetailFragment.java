package com.astuter.popularmovies.gui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astuter.popularmovies.R;
import com.astuter.popularmovies.adapter.ReviewAdapter;
import com.astuter.popularmovies.adapter.VideosAdapter;
import com.astuter.popularmovies.api.Config;
import com.astuter.popularmovies.api.ReviewListTask;
import com.astuter.popularmovies.api.VideoListTask;
import com.astuter.popularmovies.model.Movies;
import com.astuter.popularmovies.model.Reviews;
import com.astuter.popularmovies.model.Videos;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {

    private Movies mMovie;
    private TextView title, overview, releaseDate, userRating;
    private ImageView poster;
    private LinearLayout VideoView, reviewView;
    private ImageButton movieStar;

    private RecyclerView reviewRecyclerView;
    private RecyclerView.LayoutManager videoLayoutManager, reviewLayoutManager;
    private ReviewAdapter mReviewAdapter;
    private ArrayList<Reviews> reviewList;

    private RecyclerView videoRecyclerView;
    private VideosAdapter mVideosAdapter;
    private ArrayList<Videos> videoList;

    private boolean isTwoPane = false;

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
            isTwoPane = getArguments().getBoolean(Config.IS_TWO_PANE);
            Log.e("MovieDetailFragment", " mMovie.isFavorite:" + mMovie.isFavorite);
//            Activity activity = this.getActivity();
//            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//            if (appBarLayout != null) {
//                appBarLayout.setTitle(mMovie.getTitle());
//            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        setHasOptionsMenu(true);

        // Show the dummy content as text in a TextView.
        if (mMovie != null) {
            title = ((TextView) rootView.findViewById(R.id.title));

            overview = ((TextView) rootView.findViewById(R.id.overview));
            releaseDate = ((TextView) rootView.findViewById(R.id.release_date));
            userRating = ((TextView) rootView.findViewById(R.id.user_rating));
            poster = ((ImageView) rootView.findViewById(R.id.poster));
            VideoView = (LinearLayout) rootView.findViewById(R.id.video_view);
            reviewView = (LinearLayout) rootView.findViewById(R.id.review_view);

            title.setText(mMovie.title);
            overview.setText(mMovie.overview);
            releaseDate.setText("Released on: " + mMovie.releaseDate);
            userRating.setText("User Rating: " + mMovie.voteAverage + " out of 10");

            if(isTwoPane){
                movieStar = ((ImageButton) rootView.findViewById(R.id.moive_star));
                movieStar.setVisibility(View.VISIBLE);
                if (mMovie.isFavorite == 1) {
                    movieStar.setBackgroundResource(R.drawable.ic_star);
                } else {
                    movieStar.setBackgroundResource(R.drawable.ic_star_border);
                }

                movieStar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mMovie.isFavorite == 1) {
                            movieStar.setBackgroundResource(R.drawable.ic_star_border);

                            Intent intent = new Intent(Config.ACTION_MOVIE_FAVORITE);
                            intent.putExtra(Config.MOVIE_IS_FAVORITE, 0);
                            getActivity().sendBroadcast(intent);
                        } else {
                            movieStar.setBackgroundResource(R.drawable.ic_star);

                            Intent intent = new Intent(Config.ACTION_MOVIE_FAVORITE);
                            intent.putExtra(Config.MOVIE_IS_FAVORITE, 1);
                            getActivity().sendBroadcast(intent);
                        }
                    }
                });
            }

            // use a linear layout manager for RecyclerView
            videoLayoutManager = new LinearLayoutManager(getActivity());
            videoLayoutManager.setAutoMeasureEnabled(true);

            videoRecyclerView = (RecyclerView) rootView.findViewById(R.id.video_list);
            videoRecyclerView.setHasFixedSize(true);
            videoRecyclerView.setNestedScrollingEnabled(false);
            videoRecyclerView.setLayoutManager(videoLayoutManager);

            videoList = new ArrayList<>();
            mVideosAdapter = new VideosAdapter(getActivity(), videoList);
            videoRecyclerView.setAdapter(mVideosAdapter);
            fetchVideos(mMovie.movieId);

            // use a linear layout manager for RecyclerView
            reviewLayoutManager = new LinearLayoutManager(getActivity());
            reviewLayoutManager.setAutoMeasureEnabled(true);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            reviewRecyclerView = (RecyclerView) rootView.findViewById(R.id.review_list);
            reviewRecyclerView.setHasFixedSize(true);
            reviewRecyclerView.setNestedScrollingEnabled(false);
            reviewRecyclerView.setLayoutManager(reviewLayoutManager);

            // specify an adapter (see also next example)
            reviewList = new ArrayList<>();
            mReviewAdapter = new ReviewAdapter(getActivity(), reviewList);
            reviewRecyclerView.setAdapter(mReviewAdapter);
            fetchReviews(mMovie.movieId);

            Picasso.with(getActivity())
                    .load(mMovie.poster)
                    .placeholder(R.drawable.ic_movie_placeholder)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(poster, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            //Try again online if cache failed
                            Picasso.with(getActivity())
                                    .load(mMovie.poster)
                                    .placeholder(R.drawable.ic_movie_placeholder)
                                    .error(R.drawable.ic_movie_placeholder)
                                    .into(poster);
                        }
                    });

        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.getItem(0);
        if (mMovie.isFavorite == 1) {
            item.setIcon(R.drawable.ic_star);
        } else {
            item.setIcon(R.drawable.ic_star_border);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_star:
                // Do Fragment menu item stuff here
                Log.e("onOptionsItemSelected", "Fragment");
                if (mMovie.isFavorite == 1) {
                    item.setIcon(R.drawable.ic_star_border);

                    Intent intent = new Intent(Config.ACTION_MOVIE_FAVORITE);
                    intent.putExtra(Config.MOVIE_IS_FAVORITE, 0);
                    getActivity().sendBroadcast(intent);
                } else {
                    item.setIcon(R.drawable.ic_star);

                    Intent intent = new Intent(Config.ACTION_MOVIE_FAVORITE);
                    intent.putExtra(Config.MOVIE_IS_FAVORITE, 1);
                    getActivity().sendBroadcast(intent);
                }
                return true;
            default:
                break;
        }

        return false;
    }

    public ArrayList<Videos> getVideoList() {
        return videoList;
    }

    public ArrayList<Reviews> getReviewList() {
        return reviewList;
    }

    private void fetchVideos(String movieId) {
        if (Config.isNetworkAvailable(getActivity())) {
            VideoListTask videoTask = new VideoListTask(MovieDetailFragment.this);
            videoTask.execute(Config.API_BASE_URL + movieId + Config.MOVIE_VIDEO_POSTFIX);
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

            videoList = new ArrayList<>(Config.getAllVideos(mMovie.movieId));
            mVideosAdapter = new VideosAdapter(getActivity(), videoList);
            videoRecyclerView.setAdapter(mVideosAdapter);

            showViewAnimated(VideoView, getActivity());
        }
    }

    public void gotVideosData() {
        mVideosAdapter.notifyDataSetChanged();

        if (videoList.size() > 0) {
            showViewAnimated(VideoView, getActivity());
        } else {
            hideViewAnimated(VideoView, getActivity());
        }
    }

    private void fetchReviews(String movieId) {
        if (Config.isNetworkAvailable(getActivity())) {
            ReviewListTask reviewTask = new ReviewListTask(MovieDetailFragment.this);
            reviewTask.execute(Config.API_BASE_URL + movieId + Config.MOVIE_REVIEW_POSTFIX);
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

            reviewList = new ArrayList<>(Config.getAllReviews(mMovie.movieId));
            mReviewAdapter = new ReviewAdapter(getActivity(), reviewList);
            reviewRecyclerView.setAdapter(mReviewAdapter);

            showViewAnimated(reviewView, getActivity());
        }
    }

    public void gotReviewsData() {
        mReviewAdapter.notifyDataSetChanged();

        if (reviewList.size() > 0) {
            showViewAnimated(reviewView, getActivity());
        } else {
            hideViewAnimated(reviewView, getActivity());
        }
    }

    public void showViewAnimated(View view, Context context) {
        view.setVisibility(View.VISIBLE);
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_up));
    }

    public void hideViewAnimated(View view, Context context) {
        view.setVisibility(View.GONE);
        //view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_down));
    }
}
