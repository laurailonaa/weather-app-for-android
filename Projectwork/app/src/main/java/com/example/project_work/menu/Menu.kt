package com.example.project_work.menu

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

// Sealed class defines app's navigation destinations
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home: Screen("home", "Home", Icons.Default.Home)
    object Forecast: Screen("forecast", "Forecast", Icons.Default.DateRange)
}

// Making the bottom nav bar using Home and Forecast screens
@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        Screen.Home,
        Screen.Forecast
    )
    // this tracks the current route and highlights it on the bottom nav bar
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // navigate through routes
    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route)
                    }
                },
                label = { Text(screen.title) },
                icon = { Icon(screen.icon, contentDescription = screen.title) }
            )
        }
    }
}
