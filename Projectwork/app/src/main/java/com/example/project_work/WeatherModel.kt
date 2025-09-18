package com.example.project_work

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_work.data.Forecast
import com.example.project_work.data.Result
import com.example.project_work.data.WantedWeather
import com.example.project_work.data.WeatherResponse
import com.example.project_work.images.getWeatherIcon
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * ViewModel to handle states and functions that affects UI and multiple screens
 */
class WeatherModel : ViewModel() {
    // city is Tampere by default
    var currentCity by mutableStateOf("Tampere")
        private set

    var currentWeather by mutableStateOf<WantedWeather?>(null)
        private set

    var iconId by mutableStateOf<Int?>(null)
        private set

    val weatherForecast = mutableStateOf<List<Forecast>>(emptyList())

    var isLoading by mutableStateOf(false)
        private set

    // add errorText to be shown in the UI if city is not found or other error happens
    var errorText by mutableStateOf<String?>(null)
        private set

    private val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
    }

    // When app mounts fetch the weather for Tampere
    init {
        fetchWeather("Tampere")
    }

    fun updateCity(city: String) {
        currentCity = city
        fetchWeather(city)
    }

    // format the fetched date into more western style (Mon May 26, 2025)
    // AI consulted with understanding the parsing logic
    // using Java's LocalDate
    fun formatDateWithWeekday(date: String): String {
        val parsedDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
        val formatter = DateTimeFormatter.ofPattern("EEE MMMM d, yyyy", Locale.getDefault())
        return parsedDate.format(formatter)
    }

    // Function to fetch weather based on the city
    private fun fetchWeather(city: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                // First we are fetching latitude and longitude of the current city
                val cityUrl = "https://geocoding-api.open-meteo.com/v1/search?name=$city&format=json"
                val result = client.get(cityUrl).body<Result>()
                val currentCity = result.results.firstOrNull()

                if(currentCity != null) {
                    val lat = currentCity.latitude
                    val long = currentCity.longitude
                    // Then fetching the weather using coordinates and getting data we want
                    val defaultWeatherUrl = "https://api.open-meteo.com/v1/forecast?" +
                            "latitude=${lat}&longitude=${long}" +
                            "&current_weather=true" +
                            "&daily=temperature_2m_max,relative_humidity_2m_max,windspeed_10m_max,sunrise,sunset,weathercode" +
                            "&timezone=Europe/Helsinki"
                    val response = client.get(defaultWeatherUrl)
                    val fetchedWeather = response.body<WeatherResponse>()
                    // initializing fetched data to match data classes
                    // this is for singular weather info
                    currentWeather = WantedWeather(
                        temperature = fetchedWeather.current_weather.temperature,
                        humidity = fetchedWeather.daily.relative_humidity_2m_max.firstOrNull() ?: 0.0,
                        windspeed = fetchedWeather.current_weather.windspeed,
                        sunrise = fetchedWeather.daily.sunrise.firstOrNull() ?: "",
                        sunset = fetchedWeather.daily.sunset.firstOrNull() ?: "",
                        weatherCode = fetchedWeather.current_weather.weathercode
                    )

                    iconId = getWeatherIcon(fetchedWeather.current_weather.weathercode)

                    // this is for forecast values, it gets 7-day forecast by default
                    weatherForecast.value = fetchedWeather.daily.time.indices.map { day ->
                        Forecast(
                            date = fetchedWeather.daily.time[day],
                            temperature = fetchedWeather.daily.temperature_2m_max[day],
                            humidity = fetchedWeather.daily.relative_humidity_2m_max[day],
                            windspeed = fetchedWeather.daily.windspeed_10m_max[day],
                            sunrise = fetchedWeather.daily.sunrise[day],
                            sunset = fetchedWeather.daily.sunset[day],
                            weatherCode = fetchedWeather.daily.weathercode[day],
                            iconId = getWeatherIcon(fetchedWeather.daily.weathercode[day])
                        )
                    }
                    errorText = null
                }
            } catch (e: Exception) {
                Log.e("City", "Error: ${e.message}", e)
                errorText = "City not found. Please try again with different city name."
                // make the weather slots be empty and show only the error text
                currentWeather = null
                weatherForecast.value = emptyList()
                iconId = null

            }
            isLoading = false
        }
    }
}