package com.techsultan.weatherapp.ui.presentation

import androidx.lifecycle.ViewModel
import com.techsultan.weatherapp.domain.HourlyForecast
import com.techsultan.weatherapp.domain.WeatherCondition
import com.techsultan.weatherapp.domain.WeatherInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WeatherViewModel : ViewModel() {

    private val _weatherList = MutableStateFlow<List<WeatherInfo>>(emptyList())
    val weatherList: StateFlow<List<WeatherInfo>> = _weatherList.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        val cities = listOf(
            "Lagos", "London", "Nairobi", "Accra", "Cairo",
            "Dubai", "Toronto", "Berlin", "Tokyo", "Cape Town"
        )
        
        _weatherList.value = cities.mapIndexed { index, city ->
            WeatherInfo(
                cityName = city,
                temperature = 25 + index % 10,
                condition = if (index % 3 == 0) "Sunny" else if (index % 3 == 1) "Cloudy" else "Rainy",
                feelsLike = 27 + index % 10,
                humidity = 40 + index * 2,
                windSpeed = 10 + index,
                pressure = 1010 + index,
                description = "Clear sky",
                lastUpdated = "Updated ${10 + index} min ago",
                isFavorite = index % 4 == 0,
                rainChance = 10 + index * 3,
                hourlyForecast = listOf(
                    HourlyForecast("12pm", 28, WeatherCondition.SUNNY),
                    HourlyForecast("Now", 30, WeatherCondition.SUNNY),
                    HourlyForecast("2pm", 33, WeatherCondition.RAINY),
                    HourlyForecast("4pm", 31, WeatherCondition.CLOUDY),
                    HourlyForecast("6pm", 27, WeatherCondition.THUNDERSTORM)
                ),
                aiInsights = listOf(
                    "Good time for outdoor tasks (now)",
                    "Rain expected later afternoon",
                    "Carry umbrella",
                    "Stay indoors in evening"
                )
            )
        }
    }

    fun toggleFavorite(cityName: String) {
        _weatherList.value = _weatherList.value.map {
            if (it.cityName == cityName) it.copy(isFavorite = !it.isFavorite) else it
        }
    }
}
