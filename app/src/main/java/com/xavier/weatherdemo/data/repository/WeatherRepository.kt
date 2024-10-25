package com.xavier.weatherdemo.data.repository

import android.util.Log
import com.xavier.weatherdemo.data.model.WeatherResponse
import com.xavier.weatherdemo.data.api.WeatherApi
import com.xavier.weatherdemo.data.model.CityResponse
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val api: WeatherApi) {

    // Fetch weather data by city name
    suspend fun getWeather(city: String, apiKey: String): WeatherResponse {
        return api.getWeatherByCity(city, apiKey)
    }

    // Fetch city name by coordinates (latitude, longitude)
    suspend fun getCityNameByCoordinates(lat: Double, lon: Double, apiKey: String): CityResponse? {
        Log.e("mylog", "getCityNameByCoordinates")
        val cityResponseList = api.getCityNameByCoordinates(lat, lon, limit = 1, apiKey = apiKey)
        Log.e("mylog", "cityResponseList = $cityResponseList")
        return cityResponseList.firstOrNull()  // Return the first result, if any
    }
}