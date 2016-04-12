package com.astuter.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astuter.popularmovies.R;
import com.astuter.popularmovies.model.Videos;

import java.util.List;

/**
 * Created by astute on 03/03/16.
 */


public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {

    private Context mContext;
    private List<Videos> videosList;

    public VideosAdapter(Context context, List<Videos> objects) {
        mContext = context;
        videosList = objects;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public View contentView;

        public ViewHolder(View v) {
            super(v);
            contentView = v;
            name = (TextView) v.findViewById(R.id.name);
        }
    }

    @Override
    public int getItemCount() {
        return videosList.size();
    }

    @Override
    public VideosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_video_list, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.name.setText(videosList.get(position).name);

        holder.contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videosList.get(position).videoLink)));
            }
        });

    }
}