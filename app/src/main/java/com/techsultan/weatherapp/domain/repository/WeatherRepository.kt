package com.techsultan.weatherapp.domain.repository

import com.techsultan.weatherapp.data.remote.GeoLocation
import com.techsultan.weatherapp.domain.WeatherInfo
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    fun getWeatherForCities(cities: List<String>): Flow<List<WeatherInfo>>
    fun getFavoriteCities(): Flow<List<WeatherInfo>>
    suspend fun toggleFavorite(cityName: String)
    fun searchCities(query: String): Flow<List<WeatherInfo>>
    suspend fun getCityWeather(cityName: String): WeatherInfo?

    suspend fun getGeoCodeCity(query: String, limit: Int): List<GeoLocation>

}