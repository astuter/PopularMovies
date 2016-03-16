package com.astuter.popularmovies.gui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
    private TextView title, overview, releaseDate, userRating, voteCount;
    private ImageView poster;
    private LinearLayout VideoView, reviewView;

    private ListView videoListView, reviewListView;
    private VideosAdapter mVideosAdapter;
    private ReviewAdapter mReviewAdapter;
    private ArrayList<Videos> videoList;
    private ArrayList<Reviews> reviewList;

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
        setHasOptionsMenu(true);
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
            voteCount = ((TextView) rootView.findViewById(R.id.vote_count));
            poster = ((ImageView) rootView.findViewById(R.id.poster));
            videoListView = (ListView) rootView.findViewById(R.id.video_list);
            VideoView = (LinearLayout) rootView.findViewById(R.id.video_view);

            reviewListView = (ListView) rootView.findViewById(R.id.review_list);
            reviewView = (LinearLayout) rootView.findViewById(R.id.review_view);

            title.setText(mMovie.title);
            overview.setText(mMovie.overview);
            releaseDate.setText("Released on: " + mMovie.releaseDate);
            userRating.setText("User Rating: " + mMovie.voteAverage + " out of 10");
            voteCount.setText("Total Votes: " + mMovie.voteCount);

            videoList = new ArrayList<>();
            mVideosAdapter = new VideosAdapter(getContext(), videoList);
            videoListView.setAdapter(mVideosAdapter);

            reviewList = new ArrayList<>();
            mReviewAdapter = new ReviewAdapter(getContext(), reviewList);
            reviewListView.setAdapter(mReviewAdapter);

            fetchVideos(mMovie.movieId);

            fetchReviews(mMovie.movieId);

            Picasso.with(getContext())
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
                            Picasso.with(getContext())
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e("onOptionsItemSelected", "Fragment");
        switch (item.getItemId()) {
            case R.id.action_star:
                // Do Fragment menu item stuff here
                if (mMovie.isFavorite == 1) {
                    item.setIcon(R.drawable.ic_star);
                    mMovie.isFavorite = 0;
                    mMovie.save();
                } else {
                    item.setIcon(R.drawable.ic_star_border);

                    mMovie.isFavorite = 1;
                    mMovie.save();
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
        if (Config.isNetworkAvailable(getContext())) {
            VideoListTask videoTask = new VideoListTask(MovieDetailFragment.this);
            videoTask.execute(Config.API_VIDEOS_PREFIX + movieId + Config.API_VIDEOS_POSTFIX);
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

            videoList = new ArrayList<>(Config.getAllVideos(mMovie.movieId));
            mVideosAdapter = new VideosAdapter(getContext(), videoList);
            videoListView.setAdapter(mVideosAdapter);

            showViewAnimated(VideoView, getContext());
        }
    }

    public void gotVideosData() {
        mVideosAdapter.notifyDataSetChanged();

        if (videoList.size() == 0) {
            hideViewAnimated(VideoView, getContext());
        } else {
            showViewAnimated(VideoView, getContext());
        }
    }

    private void fetchReviews(String movieId) {
        if (Config.isNetworkAvailable(getContext())) {
            ReviewListTask reviewTask = new ReviewListTask(MovieDetailFragment.this);
            reviewTask.execute(Config.API_REVIEW_PREFIX + movieId + Config.API_REVIEW_POSTFIX);
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

            reviewList = new ArrayList<>(Config.getAllReviews(mMovie.movieId));
            mReviewAdapter = new ReviewAdapter(getContext(), reviewList);
            reviewListView.setAdapter(mReviewAdapter);

            showViewAnimated(reviewView, getContext());
        }
    }

    public void gotReviewsData() {
        mReviewAdapter.notifyDataSetChanged();

        if (reviewList.size() == 0) {
            hideViewAnimated(reviewView, getContext());
        } else {
            showViewAnimated(reviewView, getContext());
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
