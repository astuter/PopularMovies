package com.astuter.popularmovies.api;

import android.app.Application;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by astute on 04/03/16.
 */
public class ApplicationConfig extends Application {

    private static TheMovieDbInterface service;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static TheMovieDbInterface getRetrofit() {

        if (service == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Config.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service = retrofit.create(TheMovieDbInterface.class);
        }
        return service;
    }
}
