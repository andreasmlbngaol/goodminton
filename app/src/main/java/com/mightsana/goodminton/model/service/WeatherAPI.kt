package com.mightsana.goodminton.model.service

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}