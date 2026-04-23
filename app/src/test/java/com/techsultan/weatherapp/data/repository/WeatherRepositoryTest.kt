package com.techsultan.weatherapp.data.repository

import app.cash.turbine.test
import com.techsultan.weatherapp.core.network.NetworkMonitor
import com.techsultan.weatherapp.data.api.WeatherApiService
import com.techsultan.weatherapp.data.local.WeatherDao
import com.techsultan.weatherapp.data.local.WeatherEntity
import com.techsultan.weatherapp.data.mapper.WeatherMapper
import com.techsultan.weatherapp.domain.WeatherInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class WeatherRepositoryTest {

    @Mock
    private lateinit var weatherDao: WeatherDao
    @Mock
    private lateinit var apiService: WeatherApiService
    @Mock
    private lateinit var mapper: WeatherMapper
    @Mock
    private lateinit var networkMonitor: NetworkMonitor

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: WeatherRepositoryImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        repository = WeatherRepositoryImpl(
            weatherDao,
            apiService,
            mapper,
            networkMonitor,
            testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getWeatherForCities should emit from database`() = runTest {
        // Given
        val cities = listOf("London", "Paris")
        val entities = listOf(createWeatherEntity("London"), createWeatherEntity("Paris"))

        whenever(weatherDao.getAllWeatherOnce()).thenReturn(entities)
        whenever(weatherDao.getAllWeather()).thenReturn(flowOf(entities))
        whenever(networkMonitor.isOnline()).thenReturn(false)
        whenever(mapper.entityToDomain(any())).thenAnswer { invocation ->
            val entity = invocation.arguments[0] as WeatherEntity
            createWeatherInfo(entity.cityName)
        }

        // When & Then
        repository.getWeatherForCities(cities).test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("London", result[0].cityName)
            assertEquals("Paris", result[1].cityName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleFavorite should update dao`() = runTest {
        // Given
        val cityName = "London"
        val entity = createWeatherEntity(cityName, isFavorite = false)
        whenever(weatherDao.getCityByName(cityName)).thenReturn(entity)

        // When
        repository.toggleFavorite(cityName)

        // Then
        verify(weatherDao).updateFavorite(cityName, true)
    }

    private fun createWeatherEntity(cityName: String, isFavorite: Boolean = false): WeatherEntity {
        return WeatherEntity(
            cityName = cityName,
            temperature = 20,
            feelsLike = 22,
            humidity = 50,
            pressure = 1013,
            windSpeed = 5,
            condition = "Cloudy",
            description = "Partly cloudy",
            rainChance = 10,
            hourlyForecastJson = "[]",
            isFavorite = isFavorite,
            lastUpdated = "12:00 PM"
        )
    }

    private fun createWeatherInfo(cityName: String): WeatherInfo {
        return WeatherInfo(
            cityName = cityName,
            temperature = 20,
            condition = "Cloudy",
            feelsLike = 22,
            humidity = 50,
            windSpeed = 5,
            pressure = 1013,
            description = "Partly cloudy",
            lastUpdated = "12:00 PM"
        )
    }
}
