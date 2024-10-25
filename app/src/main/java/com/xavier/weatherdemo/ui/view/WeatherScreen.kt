package com.xavier.weatherdemo.ui.view

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.xavier.weatherdemo.ui.viewmodel.WeatherViewModel

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val weather by viewModel.weather.observeAsState()
    val cityName by viewModel.cityName.collectAsState()
    var cityInput by remember { mutableStateOf("") }

    // Handle location permission request
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                Log.e("mylog", "isGranted")
                viewModel.fetchWeatherByLocation(context)
            }
        }
    )

    // Launch effect to fetch weather data based on last searched city or location
    LaunchedEffect(Unit) {
        val lastCity = viewModel.getLastSearchedCity(context)
        Log.e("mylog", "lastCity = $lastCity")
        if (lastCity != null) {
            viewModel.fetchWeather(lastCity)
        } else if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("mylog", "have permission")
            viewModel.fetchWeatherByLocation(context)
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Background gradient
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF89CFF0),  // Light blue
                        Color(0xFF4682B4)   // Steel blue
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display city name if available
            cityName?.let {
                Text(
                    text = "City: $it",
                    style = MaterialTheme.typography.h4,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search input field and button
            TextField(
                value = cityInput,
                onValueChange = { cityInput = it },
                label = { Text("Enter US City") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.fetchWeather(cityInput)
                    viewModel.saveLastSearchedCity(context, cityInput)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF4682B4),  // Steel blue button
                    contentColor = Color.White
                )
            ) {
                Text("Search")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display weather data or loading indicator
            if (weather == null) {
                CircularProgressIndicator(color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Loading weather data...", color = Color.White)
            } else {
                weather?.let {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(color = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Display weather icon and description
                            AsyncImage(
                                model = "https://openweathermap.org/img/wn/${it.weather[0].icon}@2x.png",
                                contentDescription = "Weather Icon",
                                modifier = Modifier.size(100.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = it.weather[0].description.capitalize(),
                                style = MaterialTheme.typography.h5,
                                color = Color(0xFF4682B4)  // Steel blue text
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Display temperature and feels-like temperature
                            Text(text = "Temperature: ${it.main.temp}°C", fontSize = 20.sp)
                            Text(text = "Feels Like: ${it.main.feels_like}°C", fontSize = 16.sp, color = Color.Gray)

                            Spacer(modifier = Modifier.height(8.dp))

                            // Display humidity and pressure
                            Text(text = "Humidity: ${it.main.humidity}%", fontSize = 16.sp, color = Color.Gray)
                            Text(text = "Pressure: ${it.main.pressure} hPa", fontSize = 16.sp, color = Color.Gray)

                            Spacer(modifier = Modifier.height(8.dp))

                            // Display wind speed and visibility
                            Text(text = "Wind Speed: ${it.wind.speed} m/s", fontSize = 16.sp, color = Color.Gray)
                            Text(text = "Visibility: ${it.visibility / 1000} km", fontSize = 16.sp, color = Color.Gray)

                            Spacer(modifier = Modifier.height(8.dp))

                            // Display cloudiness percentage
                            Text(text = "Cloudiness: ${it.clouds.all}%", fontSize = 16.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}