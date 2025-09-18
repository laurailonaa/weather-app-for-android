package com.example.project_work.data

import kotlinx.serialization.Serializable

// Data classes for fetching city's latitude and longitude from Open Meteo's Geo API
@Serializable
data class City(
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class Result(
    val results: List<City>
)

// Data classes for defining wanted weather data
@Serializable
data class WeatherResponse(
    val current_weather: CurrentWeather,
    val daily: Daily
)

@Serializable
data class CurrentWeather(
    val temperature: Double,
    val windspeed: Double,
    val weathercode: Int,
    val time: String
)

@Serializable
data class Daily(
    val time: List<String>,
    val temperature_2m_max: List<Double>,
    val relative_humidity_2m_max: List<Double>,
    val windspeed_10m_max: List<Double>,
    val sunrise: List<String>,
    val sunset: List<String>,
    val weathercode: List<Int>
)

data class WantedWeather(
    val temperature: Double,
    val humidity: Double,
    val windspeed: Double,
    val sunrise: String,
    val sunset: String,
    val weatherCode: Int
)

data class Forecast(
    val date: String,
    val temperature: Double,
    val humidity: Double,
    val windspeed: Double,
    val sunrise: String,
    val sunset: String,
    val weatherCode: Int,
    val iconId: Int?
)