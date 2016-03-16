package com.astuter.popularmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.astuter.popularmovies.R;
import com.astuter.popularmovies.model.Reviews;

import java.util.List;

/**
 * Created by astute on 03/03/16.
 */


public class ReviewAdapter extends ArrayAdapter<Reviews> {

    Context mContext;

    public ReviewAdapter(Context context, List<Reviews> objects) {
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_review_list, parent, false);
        }

        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
        final Reviews review = getItem(position);

        TextView author = (TextView) convertView.findViewById(R.id.author);
        author.setText(position + ") " + review.author == null ? "Unknown" : review.author);

        TextView content = (TextView) convertView.findViewById(R.id.content);
        content.setText(review.content);


        return convertView;
    }
}