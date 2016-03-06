package com.astuter.popularmovies.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by astute on 04/03/16.
 */
public interface TheMovieDbInterface {

    @GET("movie/{id}/videos")
    Call<ResponseBody> getMovieVideos(@Path("id") String movieId, @Query("api_key") String api_key);
}
