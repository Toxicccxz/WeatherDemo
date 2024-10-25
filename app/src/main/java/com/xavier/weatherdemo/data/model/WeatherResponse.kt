package com.xavier.weatherdemo.data.model

import com.squareup.moshi.Json

data class WeatherResponse(
    @Json(name = "coord") val coord: Coord,
    @Json(name = "weather") val weather: List<Weather>,
    @Json(name = "main") val main: Main,
    @Json(name = "wind") val wind: Wind,
    @Json(name = "clouds") val clouds: Clouds,
    @Json(name = "visibility") val visibility: Int,
    @Json(name = "dt") val timestamp: Long,
    @Json(name = "sys") val sys: Sys,
    @Json(name = "timezone") val timezone: Int,
    @Json(name = "name") val cityName: String,
    @Json(name = "cod") val cod: Int
)

data class Coord(
    @Json(name = "lon") val lon: Double,
    @Json(name = "lat") val lat: Double
)

data class Weather(
    @Json(name = "id") val id: Int,
    @Json(name = "main") val main: String,
    @Json(name = "description") val description: String,
    @Json(name = "icon") val icon: String
)

data class Main(
    @Json(name = "temp") val temp: Double,
    @Json(name = "feels_like") val feels_like: Double,
    @Json(name = "temp_min") val temp_min: Double,
    @Json(name = "temp_max") val temp_max: Double,
    @Json(name = "pressure") val pressure: Int,
    @Json(name = "humidity") val humidity: Int,
    @Json(name = "sea_level") val sea_level: Int?,
    @Json(name = "grnd_level") val grnd_level: Int?
)

data class Wind(
    @Json(name = "speed") val speed: Double,
    @Json(name = "deg") val deg: Int,
    @Json(name = "gust") val gust: Double?
)

data class Clouds(
    @Json(name = "all") val all: Int
)

data class Sys(
    @Json(name = "type") val type: Int?,
    @Json(name = "id") val id: Int?,
    @Json(name = "country") val country: String,
    @Json(name = "sunrise") val sunrise: Long,
    @Json(name = "sunset") val sunset: Long
)

data class CityResponse(
    @Json(name = "name") val name: String,
    @Json(name = "local_names") val localNames: Map<String, String>?,
    @Json(name = "lat") val latitude: Double,
    @Json(name = "lon") val longitude: Double,
    @Json(name = "country") val country: String,
    @Json(name = "state") val state: String?
)