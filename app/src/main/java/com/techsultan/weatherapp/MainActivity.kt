package com.techsultan.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.techsultan.weatherapp.ui.presentation.WeatherDetailScreen
import com.techsultan.weatherapp.ui.presentation.WeatherHomeScreen
import com.techsultan.weatherapp.ui.presentation.WeatherViewModel
import com.techsultan.weatherapp.ui.theme.WeatherTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherTheme {
                WeatherAppNavHost()
            }
        }
    }
}

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
