package com.example.project_work.images

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.project_work.R

// Display weather icons based on the weather code
fun getWeatherIcon(weatherCode: Int): Int {
    return when(weatherCode) {
        0 -> R.drawable.clear
        in 1..3 -> R.drawable.partly_cloudy
        in 45..48 -> R.drawable.fog
        in 51..57 -> R.drawable.dense_drizzle
        in 61..67 -> R.drawable.heavy_rain
        in 71..77 -> R.drawable.snowflake
        in 80..82 -> R.drawable.heavy_rain
        in 85..86 -> R.drawable.heavy_snowfall
        95 -> R.drawable.thunderstorm
        in 96..99 -> R.drawable.thunderstorm_with_hail
        else -> 0
    }
}