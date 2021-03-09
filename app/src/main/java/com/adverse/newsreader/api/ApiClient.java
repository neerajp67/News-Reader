package com.adverse.newsreader.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL = "https://newsapi.org/v2/";
//    public static final String API_KEY = "650cb05bd80e40aa96f2ca7b72f336d5";
//    public static final String API_KEY = "97689fdcecd74e27b5c130056996376e";
    public static final String API_KEY = "1c4a8c526a0e405287154f057bd1adb9";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
