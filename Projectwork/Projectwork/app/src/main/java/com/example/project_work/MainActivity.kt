package com.example.project_work

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project_work.menu.BottomNavBar
import com.example.project_work.menu.Screen
import com.example.project_work.ui.theme.ProjectworkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectworkTheme(dynamicColor = false) {
                // make the navigation paths and form a bottom nav bar
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavBar(navController) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // define navigation paths of home/weather, detail and forecast screens
                        composable(Screen.Home.route) {
                            WeatherScreen(navController)
                        }
                        // send city data as a parameter to the detail screen
                        composable("details/{city}") { backStackEntry ->
                            val city = backStackEntry.arguments?.getString("city")
                            DetailScreen(navController = navController, city = city)
                        }
                        composable(Screen.Forecast.route) {
                            ForecastScreen(navController)
                        }
                    }
                }
            }
        }
    }
}
