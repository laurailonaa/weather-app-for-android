package com.example.project_work

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate

/*
 The starting point of the app showing the current weather (default in Tampere).
 Main screen has a text field and a button for searching specific city.
 The bottom nav bar has the home screen and 7-day forecast screen based
 on the current city.

 Uses WeatherModel to handle state and UI changes.
 */
@Composable
fun WeatherScreen(viewModel: WeatherModel) {
    // get some values from the WeatherModel
    val city = viewModel.currentCity
    val weather = viewModel.currentWeather
    val isLoading = viewModel.isLoading
    val icon = viewModel.iconId
    val errorText = viewModel.errorText

    val dateToday = LocalDate.now().toString()

    // value for changing current city by textfield
    var textFieldCity by rememberSaveable { mutableStateOf("") }


    // Show header, weather icon, city title and temperature with some styling
    Box(modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center
        ) {

        Text(
            "Weather App",
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            viewModel.formatDateWithWeekday(dateToday),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if(errorText != null) {
                Text(errorText, modifier = Modifier.padding(16.dp))
            } else {
                icon?.let { painterResource(id = it) }?.let {
                    Image(
                        painter = it,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(bottom = 30.dp)
                            .size(200.dp, 200.dp)
                    )
                }
                weather?.let { weather ->
                    Text(
                        city,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text("${weather.temperature}°C",
                        modifier = Modifier
                            .padding(bottom = 20.dp),
                        style = MaterialTheme.typography.headlineSmall)
                }
            }
            // Textfield and button for searching another cities weather
            TextField(
                value = textFieldCity,
                onValueChange = { textFieldCity = it },
                label = { Text("Enter a city: ") },
                singleLine = true,
                modifier = Modifier
                    .padding(bottom = 20.dp)
            )
            Button(colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                onClick = {
                    // update the city weather data based on input
                    viewModel.updateCity(textFieldCity)
                }) {
                Text(text = "Search weather")
            }
        }
        // show loading circle on screen during weather fetching
        if(isLoading) {
            CircularProgressIndicator()
        }
    }
}