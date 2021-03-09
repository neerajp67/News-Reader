package com.adverse.newsreader.api;

import com.adverse.newsreader.model.ResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("top-headlines")
    Call<ResponseModel> getLatestNews(@Query("country") String country, @Query("apiKey") String apiKey);

    @GET("top-headlines")
    Call<ResponseModel> getLatestNewsCategory(@Query("country") String country, @Query("category") String category, @Query("apiKey") String apiKey);

    @GET("everything")
    Call<ResponseModel> getEverything(@Query("q") String query, @Query("apiKey") String apiKey);

    @GET("everything")
    Call<ResponseModel> getEverythingByDate(@Query("q") String category, @Query("from") String dateFrom, @Query("to") String dateTo, @Query("apiKey") String apiKey);
}
