package com.astuter.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.astuter.popularmovies.R;
import com.astuter.popularmovies.model.Videos;

import java.util.List;

/**
 * Created by astute on 03/03/16.
 */


public class VideosAdapter extends ArrayAdapter<Videos> {

    Context mContext;

    public VideosAdapter(Context context, List<Videos> objects) {
        super(context, 0, objects);

        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_video_list, parent, false);
        }

        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
        final Videos video = getItem(position);

        TextView name = (TextView) convertView.findViewById(R.id.name);
        name.setText(video.name);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(video.videoLink)));
            }
        });

        return convertView;
    }
}