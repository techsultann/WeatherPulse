package com.techsultan.weatherapp.data.repository

import android.util.Log
import com.techsultan.weatherapp.BuildConfig
import com.techsultan.weatherapp.core.network.NetworkMonitor
import com.techsultan.weatherapp.data.mapper.WeatherMapper
import com.techsultan.weatherapp.data.api.WeatherApiService
import com.techsultan.weatherapp.data.local.WeatherDao
import com.techsultan.weatherapp.data.remote.GeoLocation
import com.techsultan.weatherapp.di.IoDispatcher
import com.techsultan.weatherapp.domain.WeatherInfo
import com.techsultan.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherDao: WeatherDao,
    private val apiService:  WeatherApiService,
    private val mapper: WeatherMapper,
    private val networkMonitor: NetworkMonitor,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : WeatherRepository {

    private val TAG = "WeatherRepo"

    override fun getWeatherForCities(cities: List<String>): Flow<List<WeatherInfo>> = channelFlow {
        Log.d(TAG, "getWeatherForCities: Starting for ${cities.size} cities")

        // Phase 1 — Seed stub rows for any city not yet in Room
        val cached = weatherDao.getAllWeatherOnce()
        val cachedNames = cached.map { it.cityName }.toSet()
        val unseeded = cities.filter { it !in cachedNames }
        
        if (unseeded.isNotEmpty()) {
            Log.d(TAG, "Seeding ${unseeded.size} cities into database")
            weatherDao.insertAll(unseeded.map { cityName ->
                mapper.domainToEntity(
                    WeatherInfo(
                        cityName = cityName,
                        temperature = 0,
                        feelsLike = 0,
                        humidity = 0,
                        pressure = 0,
                        windSpeed = 0,
                        condition = "Loading",
                        description = "Fetching weather…",
                        rainChance = 0,
                        hourlyForecast = emptyList(),
                        lastUpdated = ""
                    )
                )
            })
        }

        // Phase 2 — Refresh from network in parallel (non-blocking)
        if (networkMonitor.isOnline()) {
            Log.d(TAG, "Network is online, launching background refreshes")
            cities.forEach { city ->
                launch { fetchAndStore(city) }
            }
        } else {
            Log.w(TAG, "Network is offline, skipping refresh")
        }

        // Phase 3 — stay subscribed to Room; re-emits on any change
        Log.d(TAG, "Subscribing to Room database Flow")
        weatherDao.getAllWeather()
            .map { entities -> 
                Log.d(TAG, "Room emitted ${entities.size} items")
                entities.map { mapper.entityToDomain(it) } 
            }
            .collect { 
                Log.d(TAG, "Sending ${it.size} mapped items to channel")
                send(it) 
            }

    }.flowOn(ioDispatcher)

    override fun getFavoriteCities(): Flow<List<WeatherInfo>> =
        weatherDao.getFavorites()
            .map { entities -> entities.map { mapper.entityToDomain(it) } }
            .flowOn(ioDispatcher)

    override suspend fun toggleFavorite(cityName: String) {
        withContext(ioDispatcher) {
            val current = weatherDao.getCityByName(cityName)
            current?.let {
                weatherDao.updateFavorite(cityName, !it.isFavorite)
            }
        }
    }

    override fun searchCities(query: String): Flow<List<WeatherInfo>> =
        weatherDao.searchByCity("%$query%")
            .map { entities -> entities.map { mapper.entityToDomain(it) } }
            .flowOn(ioDispatcher)

    override suspend fun getCityWeather(cityName: String): WeatherInfo? =
        withContext(ioDispatcher) {
            weatherDao.getCityByName(cityName)?.let { mapper.entityToDomain(it) }
        }

    override suspend fun getGeoCodeCity(query: String, limit: Int): List<GeoLocation> =
        withContext(ioDispatcher) {
            try {
                apiService.geocodeCity(query, BuildConfig.API_KEY, limit)
                    .map { mapper.geocodingDtoToDomain(it) }
            } catch (e: Exception) {
                emptyList()
            }
        }

    private suspend fun fetchAndStore(cityName: String) {
        Log.d(TAG, "fetchAndStore: Fetching for $cityName")
        try {
            // Step 1 — resolve name to coordinates
            val geoResults = apiService.geocodeCity(cityName, BuildConfig.API_KEY, limit = 1)
            val geo = geoResults.firstOrNull() ?: run {
                Log.w(TAG, "fetchAndStore: No geocoding results for $cityName")
                return
            }
            
            val lat = geo.lat
            val lon = geo.lon
            Log.d(TAG, "fetchAndStore: Found coords for $cityName: $lat, $lon")

            // Step 2 — fetch current weather and forecast in parallel
            coroutineScope {
                val weatherDeferred  = async {
                    apiService.getCurrentWeatherData(lat = lat, lon =  lon, apiKey =  BuildConfig.API_KEY)
                }
                val forecastDeferred = async {
                    apiService.getHourlyForecast(lat, lon, BuildConfig.API_KEY)
                }
                val weatherDto  = weatherDeferred.await()
                val forecastDto = forecastDeferred.await()
                Log.d(TAG, "fetchAndStore: Successfully fetched data for $cityName")

                // Step 3 — map and persist (isFavorite never touched)
                val domain = mapper.dtoToDomain(weatherDto, forecastDto)
                weatherDao.updateWeatherData(
                    cityName           = cityName,
                    temperature        = domain.temperature,
                    feelsLike          = domain.feelsLike,
                    humidity           = domain.humidity,
                    pressure           = domain.pressure,
                    windSpeed          = domain.windSpeed,
                    condition          = domain.condition,
                    description        = domain.description,
                    rainChance         = domain.rainChance,
                    hourlyForecastJson = Json.encodeToString(domain.hourlyForecast),
                    lastUpdated        = domain.lastUpdated,
                    aiInsightsJson     = Json.encodeToString(domain.aiInsights)
                )
                Log.d(TAG, "fetchAndStore: Database updated for $cityName")
            }
        } catch (e: Exception) {
            Log.e(TAG, "fetchAndStore: Error for $cityName: ${e.message}", e)
        }
    }

}