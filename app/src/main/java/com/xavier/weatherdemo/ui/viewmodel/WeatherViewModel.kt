package com.xavier.weatherdemo.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xavier.weatherdemo.data.model.WeatherResponse
import com.xavier.weatherdemo.data.repository.WeatherRepository
import com.xavier.weatherdemo.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _weather = MutableLiveData<WeatherResponse?>(null)
    val weather: LiveData<WeatherResponse?> get() = _weather

    private val _cityName = MutableStateFlow<String?>(null)  // Store city name
    val cityName: StateFlow<String?> = _cityName

    // Get the last searched city from shared preferences
    fun getLastSearchedCity(context: Context): String? {
        val sharedPref = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("last_city", null)
    }

    // Save the last searched city to shared preferences
    fun saveLastSearchedCity(context: Context, city: String) {
        val sharedPref = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("last_city", city)
            apply()
        }
    }

    // Fetch weather data by city name
    fun fetchWeather(city: String) {
        viewModelScope.launch {
            try {
                val result = repository.getWeather(city, Constants.API_KEY)
                _weather.value = result  // Update weather data
                _cityName.value = result.cityName  // Update city name
            } catch (e: Exception) {
                // Handle errors
            }
        }
    }

    // Fetch city name by coordinates and automatically fetch weather for that city
    fun fetchCityNameByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val cityResponse = repository.getCityNameByCoordinates(lat, lon, Constants.API_KEY)
                cityResponse?.name?.let { city ->
                    _cityName.value = city  // Update city name
                    fetchWeather(city)      // Automatically fetch weather for the city
                }
            } catch (e: Exception) {
                // Handle errors
            }
        }
    }

    // Fetch weather by device location
    @SuppressLint("MissingPermission")  // Suppressing permission warning, make sure permission is handled properly
    fun fetchWeatherByLocation(context: Context) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        location?.let {
            val latitude = it.latitude
            val longitude = it.longitude
            Log.e("mylog", "latitude = $latitude longitude = $longitude")

            // Fetch city name based on coordinates
            viewModelScope.launch {
                val cityResponse = repository.getCityNameByCoordinates(latitude, longitude, Constants.API_KEY)
                cityResponse?.name?.let { cityName ->
                    _cityName.value = cityName  // Update city name
                    Log.e("mylog", "cityName = $cityName")
                    fetchWeather(cityName)  // Fetch weather for the city
                }
            }
        }
    }
}