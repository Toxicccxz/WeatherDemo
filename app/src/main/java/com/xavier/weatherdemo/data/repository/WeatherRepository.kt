package com.xavier.weatherdemo.data.repository

import android.util.Log
import com.xavier.weatherdemo.data.api.WeatherApi
import com.xavier.weatherdemo.data.model.CityResponse
import com.xavier.weatherdemo.data.model.WeatherResponse
import retrofit2.Call
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val api: WeatherApi) {

    // Fetch weather data by city name
    fun getWeather(city: String, apiKey: String): Call<WeatherResponse> {
        return api.getWeatherByCity(city, apiKey)
    }

    // Fetch city name by coordinates (latitude, longitude)
    fun getCityNameByCoordinates(lat: Double, lon: Double, apiKey: String): Call<List<CityResponse>> {
        Log.e("mylog", "getCityNameByCoordinates")
        return api.getCityNameByCoordinates(lat, lon, limit = 1, apiKey = apiKey)
    }
}