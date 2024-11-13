package com.xavier.weatherdemo.ui.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.xavier.weatherdemo.data.model.CityResponse;
import com.xavier.weatherdemo.data.model.WeatherResponse;
import com.xavier.weatherdemo.data.repository.WeatherRepository;
import com.xavier.weatherdemo.utils.Constants;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

public class WeatherViewModel extends ViewModel {

    private final WeatherRepository repository;
    private final ExecutorService executorService;

    private MutableLiveData<WeatherResponse> _weather = new MutableLiveData<>(null);
    public LiveData<WeatherResponse> getWeather() {
        return _weather;
    }

    private MutableLiveData<String> _cityName = new MutableLiveData<>(null);
    public LiveData<String> getCityName() {
        return _cityName;
    }

    @Inject
    public WeatherViewModel(WeatherRepository repository) {
        this.repository = repository;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    // Get the last searched city from shared preferences
    public String getLastSearchedCity(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE);
        return sharedPref.getString("last_city", null);
    }

    // Save the last searched city to shared preferences
    public void saveLastSearchedCity(Context context, String city) {
        SharedPreferences sharedPref = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("last_city", city);
        editor.apply();
    }

    // Fetch weather data by city name
    public void fetchWeather(final String city) {
        executorService.execute(() -> {
            try {
                WeatherResponse result = repository.getWeather(city, Constants.API_KEY).execute().body();
                if (result != null) {
                    _weather.postValue(result);
                    _cityName.postValue(result.getCityName());
                }
            } catch (Exception e) {
                // Handle errors
            }
        });
    }

    // Fetch city name by coordinates and automatically fetch weather for that city
    public void fetchCityNameByCoordinates(final double lat, final double lon) {
        executorService.execute(() -> {
            try {
                List<CityResponse> cityResponses = repository.getCityNameByCoordinates(lat, lon, Constants.API_KEY).execute().body();
                if (cityResponses != null && !cityResponses.isEmpty()) {
                    CityResponse cityResponse = cityResponses.get(0); // Get the first city in the list
                    if (cityResponse != null && cityResponse.getName() != null) {
                        String city = cityResponse.getName();
                        _cityName.postValue(city);
                        fetchWeather(city); // Fetch weather for the city
                    }
                }
            } catch (Exception e) {
                // Handle errors
            }
        });
    }

    // Fetch weather by device location
    @SuppressLint("MissingPermission")
    public void fetchWeatherByLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            final double latitude = location.getLatitude();
            final double longitude = location.getLongitude();
            Log.e("mylog", "latitude = " + latitude + " longitude = " + longitude);

            fetchCityNameByCoordinates(latitude, longitude);
        }
    }
}