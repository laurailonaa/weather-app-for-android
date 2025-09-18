package com.example.project_work

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.example.project_work.style.weatherStyle
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.material3.ButtonDefaults
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.call.body
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import com.example.project_work.data.CurrentWeather
import com.example.project_work.data.WantedWeather
import com.example.project_work.data.Result
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.painterResource
import com.example.project_work.images.getWeatherIcon
import com.example.project_work.style.formatTime

/*
 The starting point of the app showing the current weather in Tampere.
 Main screen has a text field and a button for searching specific city.
 The bottom nav bar has the home/weather screen and another for 7-day forecast based
 on the current city.
 */
@Composable
fun WeatherScreen(navController: NavHostController) {
    var weatherState by rememberSaveable { mutableStateOf("") }

    // state to store wanted weather data
    var defaultWeather by remember {
        mutableStateOf<WantedWeather?>(null)
    }

    val iconId = defaultWeather?.let { getWeatherIcon(it.weatherCode) }

    // using Tampere as a default city which weather pops up when opening the app
    val defaultCityUrl = "https://geocoding-api.open-meteo.com/v1/search?name=Tampere&format=json"

    val client = remember {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    // fetch the weather when app is launched
    LaunchedEffect(true) {
        try {
            val result = client.get(defaultCityUrl).body<Result>()
            val currentCity = result.results.firstOrNull()
            val lat = currentCity?.latitude
            val long = currentCity?.longitude
            val defaultWeatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${long}&daily=sunrise,sunset&current=temperature_2m,relative_humidity_2m,wind_speed_10m,weather_code&timezone=Europe/Helsinki"
            val response = client.get(defaultWeatherUrl)
            val fetchedWeather = response.body<CurrentWeather>()
            defaultWeather = WantedWeather(
                temperature = fetchedWeather.current.temperature_2m,
                humidity = fetchedWeather.current.relative_humidity_2m,
                windspeed = fetchedWeather.current.wind_speed_10m,
                sunrise = fetchedWeather.daily.sunrise.firstOrNull() ?: "",
                sunset = fetchedWeather.daily.sunset.firstOrNull() ?: "",
                weatherCode = fetchedWeather.current.weather_code
            )
        } catch (e: Exception) {
            Log.e("City", "Error: ${e.message}", e)
        }
    }

    Box(modifier = weatherStyle
        .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Welcome to Weather Api!")
            iconId?.let { painterResource(id = it) }?.let {
                Image(
                    painter = it,
                    contentDescription = null
                )
            }
            // show weather icon, temperature and windspeed in the main screen
            defaultWeather?.let { weather ->
                Text("Temperature: ${weather.temperature}")
                Text("Windspeed: ${weather.windspeed}")
            }
            TextField(
                value = weatherState,
                onValueChange = { weatherState = it },
                label = { Text("Enter a city: ") },
                singleLine = true
            )
            Button(colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                onClick = {
                navController.navigate("details/$weatherState")
            }) {
                Text(text = "Search weather")
            }
        }
    }
}