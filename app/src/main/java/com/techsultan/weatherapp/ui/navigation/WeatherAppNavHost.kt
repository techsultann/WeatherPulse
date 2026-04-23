package com.techsultan.weatherapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.techsultan.weatherapp.ui.presentation.WeatherDetailScreen
import com.techsultan.weatherapp.ui.presentation.WeatherHomeScreen
import com.techsultan.weatherapp.ui.presentation.WeatherViewModel

@Composable
fun WeatherAppNavHost() {
    val navController = rememberNavController()
    val viewModel: WeatherViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            WeatherHomeScreen(
                viewModel = viewModel,
                onCityClick = { weather ->
                    navController.navigate("detail/${weather.cityName}")
                }
            )
        }
        composable("detail/{cityName}") { backStackEntry ->
            val cityName = backStackEntry.arguments?.getString("cityName")
            val weatherList by viewModel.weatherList.collectAsState()
            val weather = weatherList.find { it.cityName == cityName }
            if (weather != null) {
                WeatherDetailScreen(
                    weather = weather,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}