package com.techsultan.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather ORDER BY is_favorite DESC, city_name ASC")
    fun getAllWeather(): Flow<List<WeatherEntity>>

    @Query("SELECT * FROM weather ORDER BY is_favorite DESC, city_name ASC")
    suspend fun getAllWeatherOnce(): List<WeatherEntity>

    @Query("SELECT * FROM weather WHERE city_name LIKE :query ORDER BY is_favorite DESC, city_name ASC")
    fun searchByCity(query: String): Flow<List<WeatherEntity>>

    @Query("SELECT * FROM weather WHERE is_favorite = 1 ORDER BY city_name ASC")
    fun getFavorites(): Flow<List<WeatherEntity>>

    @Query("SELECT * FROM weather WHERE city_name = :cityName LIMIT 1")
    suspend fun getCityByName(cityName: String): WeatherEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entities: List<WeatherEntity>)

    @Query("""
        UPDATE weather SET
            temperature         = :temperature,
            feels_like          = :feelsLike,
            humidity            = :humidity,
            pressure            = :pressure,
            wind_speed          = :windSpeed,
            condition           = :condition,
            description         = :description,
            rain_chance         = :rainChance,
            hourly_forecast_json = :hourlyForecastJson,
            last_updated        = :lastUpdated,
            ai_insights_json    = :aiInsightsJson
        WHERE city_name = :cityName
    """)
    suspend fun updateWeatherData(
        cityName: String,
        temperature: Int?,
        feelsLike: Int?,
        humidity: Int?,
        pressure: Int?,
        windSpeed: Int,
        condition: String?,
        description: String,
        rainChance: Int,
        hourlyForecastJson: String,
        lastUpdated: String,
        aiInsightsJson: String?
    )

    @Query("UPDATE weather SET is_favorite = :isFavorite WHERE city_name = :cityName")
    suspend fun updateFavorite(cityName: String, isFavorite: Boolean)

    @Query("DELETE FROM weather")
    suspend fun deleteAll()
}