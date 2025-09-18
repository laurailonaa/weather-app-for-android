package com.example.project_work

import android.net.http.HttpResponseCache.install
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.border
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.project_work.images.getWeatherIcon
import com.example.project_work.style.boxStyle
import com.example.project_work.style.formatTime

@Composable
fun DetailScreen(navController: NavController, city: String?) {

    // states for defining searched city's latitude and longitude
    var lat : Double by remember {
        mutableDoubleStateOf(0.0)
    }
    var long : Double by remember {
        mutableDoubleStateOf(0.0)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    // state to store wanted weather data
    var localWeather by remember {
        mutableStateOf<WantedWeather?>(null)
    }

    val iconId = localWeather?.let { getWeatherIcon(it.weatherCode) }

    val cityUrl = "https://geocoding-api.open-meteo.com/v1/search?name=${city}&format=json"

    val client = remember {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    // when city is changed fetch the new city's geo information and update lat and long
    LaunchedEffect(city) {
        try {
            val response = client.get(cityUrl)
            val result = response.body<Result>()
            val currentCity = result.results.firstOrNull()
            if (currentCity != null) {
                lat = currentCity.latitude
                long = currentCity.longitude
            }
        } catch (e: Exception) {
            Log.e("City", "Error: ${e.message}", e)
        }
    }

    // when city coordinates change, fetch the new city's weather data
    LaunchedEffect(lat, long) {
        isLoading = true

        try {
            val weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${long}&daily=sunrise,sunset&current=temperature_2m,relative_humidity_2m,wind_speed_10m,weather_code&timezone=Europe/Helsinki"
            val response = client.get(weatherUrl)
            val fetchedWeather = response.body<CurrentWeather>()

            val weather = WantedWeather(
                temperature = fetchedWeather.current.temperature_2m,
                humidity = fetchedWeather.current.relative_humidity_2m,
                windspeed = fetchedWeather.current.wind_speed_10m,
                sunrise = fetchedWeather.daily.sunrise.firstOrNull() ?: "",
                sunset = fetchedWeather.daily.sunset.firstOrNull() ?: "",
                weatherCode = fetchedWeather.current.weather_code

            )

            localWeather = weather
        } catch (e: Exception) {
            Log.e("City", "Error: ${e.message}", e)
        }
    }

    // Display current city's weather information
    Box(modifier = boxStyle
        .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                onClick = {
                    navController.popBackStack()
                }) {
                Text(text = "Back to home screen")
            }
            iconId?.let { painterResource(id = it) }?.let {
                Image(
                    painter = it,
                    contentDescription = null
                )
            }
            Text("This will show searched weather")
            Text("Current city is " + (city ?: "no city found"))
            localWeather?.let { weather ->
                Text("Temperature: ${weather.temperature}")
                Text("Humidity: ${weather.humidity}")
                Text("Windspeed: ${weather.windspeed}")
                Text("Sunrise: ${formatTime(weather.sunrise)}")
                Text("Sunset: ${formatTime(weather.sunset)}")
                isLoading = false
            }
        }
        // show loading circle on screen during fetching
        if(isLoading) {
            CircularProgressIndicator()
        }
    }
}