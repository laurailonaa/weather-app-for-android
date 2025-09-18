package com.example.project_work

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    // Create a NavController to handle navigation between composables
    val navController = rememberNavController()

    // Define a navigation host that controls the navigation within this composable
    NavHost(navController = navController, startDestination = "weather") {
        // Define a composable associated with "screen1" route
        composable("weather") {
            // This is where you define what Screen1 looks like and its behavior
            // navController is passed to manage navigation from Screen1
            WeatherScreen(navController)
        }
        // Define another composable associated with "screen2" route
        composable("details/{city}") { backStackEntry ->
            // This is where you define what Screen2 looks like and its behavior
            // Again, navController is passed to manage navigation from Screen2
            val city = backStackEntry.arguments?.getString("city")
            DetailScreen(navController, city)
        }
        composable("forecast") {
            ForecastScreen(navController)
        }
    }
}