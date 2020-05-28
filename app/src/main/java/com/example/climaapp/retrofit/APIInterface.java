package com.example.climaapp.retrofit;


import com.example.climaapp.model.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIInterface {

    @GET("weather")
    Call<ApiResponse> getWeatherData(
            @Query("q") String city ,
            @Query("appid")String key,
            @Query("units") String units);

    @GET("weather")
    Call<ApiResponse> getWeatherByCoordinates(
            @Query("lat") Double lat,
            @Query("lon") Double lon,
            @Query("appid") String key,
            @Query("units") String units);

}
