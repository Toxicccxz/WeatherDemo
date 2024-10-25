package com.xavier.weatherdemo.data.api

import com.xavier.weatherdemo.data.model.CityResponse
import com.xavier.weatherdemo.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    // Get weather information by city name
    @GET("data/2.5/weather")
    suspend fun getWeatherByCity(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"  // Use metric units by default
    ): WeatherResponse

    // Get weather information by geographical coordinates
    @GET("data/2.5/weather")
    suspend fun getWeatherByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"  // Use metric units by default
    ): WeatherResponse

    // Get city name based on geographical coordinates
    @GET("geo/1.0/reverse")
    suspend fun getCityNameByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("limit") limit: Int = 1,  // Limit the number of results
        @Query("appid") apiKey: String
    ): List<CityResponse>
}