package com.astuter.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astuter.popularmovies.R;
import com.astuter.popularmovies.model.Reviews;

import java.util.List;

/**
 * Created by astute on 03/03/16.
 */


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    Context mContext;
    List<Reviews> reviewsList;

    public ReviewAdapter(Context context, List<Reviews> objects) {
        mContext = context;
        reviewsList = objects;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView author;
        public TextView content;

        public ViewHolder(View v) {
            super(v);
            author = (TextView) v.findViewById(R.id.author);
            content = (TextView) v.findViewById(R.id.content);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_review_list, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String author = reviewsList.get(position).author == null ? "Unknown" : reviewsList.get(position).author;
        holder.author.setText((position + 1) + ") " + author + ":");
        holder.content.setText(reviewsList.get(position).content);
    }
}