package com.example.nubes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {
    @GET("weather")
    Call<WeatherResponse> getWeather(
            @Query("key") String apiKey,
            @Query("city_name") String city
    );
}
