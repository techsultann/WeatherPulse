package com.techsultan.weatherapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey
    @ColumnInfo(name = "city_name")
    val cityName: String,
    @ColumnInfo(name = "temperature")
    val temperature: Int?,
    @ColumnInfo(name = "feels_like")
    val feelsLike: Int?,
    @ColumnInfo(name = "humidity")
    val humidity: Int?,
    @ColumnInfo(name = "pressure")
    val pressure: Int?,
    @ColumnInfo(name = "wind_speed")
    val windSpeed: Int,
    @ColumnInfo(name = "condition")
    val condition: String?,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "rain_chance")
    val rainChance: Int,
    @ColumnInfo(name = "hourly_forecast_json")
    val hourlyForecastJson: String,
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,
    @ColumnInfo(name = "last_updated")
    val lastUpdated: String = "",
    // Nullable — not every city will have AI insights cached
    @ColumnInfo(name = "ai_insights_json")
    val aiInsightsJson: String? = null
)