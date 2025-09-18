package com.example.project_work

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.project_work.data.Forecast

/**
 * Screen to show 7-day weather forecast.
 * Each day is a clickable card which will show more detailed description.
 * In sync with the home screen so it will show 7-day forecast for Tampere by default.
 * When city is changed in home screen, it changes the 7-day forecast for that city.
 */
@Composable
fun ForecastScreen(navController: NavHostController, viewModel: WeatherModel) {
    // use forecast data from the viewModel
    val forecast = viewModel.weatherForecast.value
    val isLoading = viewModel.isLoading
    val errorText = viewModel.errorText

    Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("7-Day Forecast", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium)
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        } else if (forecast.isEmpty()) {
            if(errorText != null) {
                Text(errorText)
            }
        } else {
            Text(viewModel.currentCity, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 20.dp))
            forecast.forEach { day ->
                // go through each day and form ForecastCards with each day's data
                ForecastCard(day = day, viewModel,onClick = {
                    navController.navigate("details/${day.date}")
                })
            }
        }
    }
}

// A singular card for displaying weather info
@Composable
fun ForecastCard(day: Forecast,  viewModel: WeatherModel, onClick: () -> Unit) {
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(viewModel.formatDateWithWeekday(day.date), fontSize = 20.sp, modifier = Modifier
                .weight(1f)
            )
            Text("${day.temperature}°C",
                modifier = Modifier
                    .padding(20.dp)
            )
            day.iconId?.let { painterResource(id = it) }?.let {
                Image(
                    painter = it,
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp, 50.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}
