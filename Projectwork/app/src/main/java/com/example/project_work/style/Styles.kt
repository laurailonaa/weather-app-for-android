package com.example.project_work.style

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Styles for the detail screen
val boxStyle = Modifier
    .fillMaxSize()


// Styles for weather/home screen
val weatherStyle = Modifier
    .fillMaxSize()

// format the sunrise and sunset times for the UI
fun formatTime(rawTime: String): String {
    return rawTime.split("T").getOrNull(1) ?: "Invalid time"
}

