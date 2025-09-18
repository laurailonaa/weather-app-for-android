package com.example.project_work.data

import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

// Data classes for fetching city's latitude and longitude
@Serializable
data class Result(val results: List<City>)

@Serializable
data class City(val latitude: Double, val longitude: Double)

// Data classes for defining wanted weather data
@Serializable
data class Weather(val temperature_2m: Double, val relative_humidity_2m: Int, val wind_speed_10m: Double, val weather_code: Int)

@Serializable
data class Daily(val sunrise: List<String>, val sunset: List<String>)

@Serializable
data class CurrentWeather(val current: Weather, val daily: Daily)

@Serializable
data class WantedWeather(val temperature: Double, val humidity: Int, val windspeed: Double, val sunrise: String, val sunset: String, val weatherCode: Int)