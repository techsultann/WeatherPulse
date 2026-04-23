package com.techsultan.weatherapp.data.api

import com.techsultan.weatherapp.data.remote.forecast.ForecastDto
import com.techsultan.weatherapp.data.remote.geocoding.GeocodingDto
import com.techsultan.weatherapp.data.remote.weather.WeatherDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("/data/2.5/weather")
    suspend fun getCurrentWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherDto

    @GET("/data/2.5/forecast")
    suspend fun getHourlyForecast(
        @Query("lat")   lat: Double,
        @Query("lon")   lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("cnt")   count: Int = 40
    ): ForecastDto

    @GET("/data/2.5/forecast/daily")
    suspend fun getDailyForecast(
        @Query("lat")   lat: Double,
        @Query("lon")   lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("cnt")   count: Int = 7    // 7 days (free plan max)
    ): ForecastDto

    @GET("forecast")
    suspend fun getForecast(
        @Query("q")      cityName: String,
        @Query("appid")  apiKey: String,
        @Query("units")  units: String = "metric",
        @Query("cnt")    count: Int = 40
    ): ForecastDto

    @GET("forecast")
    suspend fun getForecastByCoord(
        @Query("lat")    lat: Double,
        @Query("lon")    lon: Double,
        @Query("appid")  apiKey: String,
        @Query("units")  units: String = "metric",
        @Query("cnt")    count: Int = 40
    ): ForecastDto

    @GET("/geo/1.0/direct")
    suspend fun geocodeCity(
        @Query("q")     query: String,
        @Query("appid") apiKey: String,
        @Query("limit") limit: Int = 5
    ): List<GeocodingDto>

    @GET("/geo/1.0/reverse")
    suspend fun reverseGeocode(
        @Query("lat")   lat: Double,
        @Query("lon")   lon: Double,
        @Query("appid") apiKey: String,
        @Query("limit") limit: Int = 1
    ): List<GeocodingDto>

}