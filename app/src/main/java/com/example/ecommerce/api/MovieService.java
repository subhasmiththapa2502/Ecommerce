package com.example.ecommerce.api;




import com.example.ecommerce.model.NowPlaying;
import com.example.ecommerce.model.TopRatedMovies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Pagination
 * Created by Subhasmith Thapa on 19,October,2021
 */

public interface MovieService {

    @GET("movie/upcoming")
    Call<TopRatedMovies> getUpComingMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int pageIndex
    );

    @GET("movie/now_playing")
    Call<NowPlaying> getNowPlaying(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int pageIndex
    );

    @GET("movie/latest")
    Call<TopRatedMovies> getLatest(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int pageIndex
    );

}
