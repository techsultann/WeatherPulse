package com.techsultan.weatherapp.domain

import kotlinx.serialization.Serializable

@Serializable
data class WeatherInfo(
    val cityName: String,
    val temperature: Int?,
    val condition: String?,
    val feelsLike: Int?,
    val humidity: Int?,
    val windSpeed: Int,
    val pressure: Int?,
    val description: String,
    val lastUpdated: String,
    val isFavorite: Boolean = false,
    val rainChance: Int = 0,
    val hourlyForecast: List<HourlyForecast> = emptyList(),
    val aiInsights: List<String> = emptyList()
)

@Serializable
data class HourlyForecast(
    val time: String,
    val temp: Int,
    val condition: WeatherCondition
)

@Serializable
enum class WeatherCondition {
    SUNNY, CLOUDY, RAINY, THUNDERSTORM
}
