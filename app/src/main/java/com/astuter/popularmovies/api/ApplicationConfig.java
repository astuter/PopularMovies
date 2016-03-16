package com.astuter.popularmovies.api;


import com.activeandroid.app.Application;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by astuter on 04/03/16.
 */
public class ApplicationConfig extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
//        built.setIndicatorsEnabled(true);
//        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }

}
