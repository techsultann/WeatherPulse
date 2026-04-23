package com.techsultan.weatherapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import com.techsultan.weatherapp.worker.NotificationScheduler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            NotificationScheduler.schedule(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermissionIfNeeded()
        enableEdgeToEdge()
        setContent {
            WeatherTheme {
                WeatherAppNavHost()
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
            if (granted) {
                NotificationScheduler.schedule(this)
            } else {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            NotificationScheduler.schedule(this)
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
