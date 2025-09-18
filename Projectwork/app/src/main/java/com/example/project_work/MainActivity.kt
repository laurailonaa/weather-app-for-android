package com.example.project_work

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project_work.menu.BottomNavBar
import com.example.project_work.menu.Screen
import com.example.project_work.ui.theme.ProjectworkTheme

/**
 * Shows the navigation system between composables.
 * Displays weather and forecast screens in a bottom nav bar.
 * Uses ViewModel to handle state changes, functions and data flow between views.
 */
class MainActivity : ComponentActivity() {
    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectworkTheme(dynamicColor = false) {
                // make the navigation paths and form a bottom nav bar
                val navController = rememberNavController()
                val weatherModel: WeatherModel = viewModel()
                Scaffold(
                    bottomBar = { BottomNavBar(navController) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // define navigation paths of home/weather and forecast screens
                        composable(Screen.Home.route) {
                            WeatherScreen(viewModel = weatherModel)
                        }

                        composable(Screen.Forecast.route) {
                            ForecastScreen(navController, viewModel = weatherModel)
                        }

                        composable("details/{date}") { backStackEntry ->
                            val date = backStackEntry.arguments?.getString("date")
                            if (date != null) {
                                DetailScreen(date, viewModel = weatherModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
