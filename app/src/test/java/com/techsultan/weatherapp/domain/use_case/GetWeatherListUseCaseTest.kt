package com.techsultan.weatherapp.domain.use_case

import com.techsultan.weatherapp.domain.WeatherInfo
import com.techsultan.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class GetWeatherListUseCaseTest {

    @Mock
    private lateinit var repository: WeatherRepository

    private lateinit var getWeatherListUseCase: GetWeatherListUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        getWeatherListUseCase = GetWeatherListUseCase(repository)
    }

    @Test
    fun `invoke should return sorted list from repository`() = runBlocking {
        // Given
        val weatherList = listOf(
            createWeatherInfo("Berlin", isFavorite = false),
            createWeatherInfo("Amsterdam", isFavorite = false),
            createWeatherInfo("Paris", isFavorite = true)
        )
        `when`(repository.getWeatherForCities(anyList())).thenReturn(flowOf(weatherList))

        // When
        val result = getWeatherListUseCase().first()

        // Then
        assertEquals(3, result.size)
        assertEquals("Paris", result[0].cityName)      // Favorite first
        assertEquals("Amsterdam", result[1].cityName)  // Alphabetical second
        assertEquals("Berlin", result[2].cityName)     // Alphabetical third
    }

    private fun createWeatherInfo(cityName: String, isFavorite: Boolean): WeatherInfo {
        return WeatherInfo(
            cityName = cityName,
            temperature = 20,
            condition = "Cloudy",
            feelsLike = 22,
            humidity = 50,
            windSpeed = 5,
            pressure = 1013,
            description = "Partly cloudy",
            lastUpdated = "12:00 PM",
            isFavorite = isFavorite
        )
    }

    private fun <T> anyList(): List<T> = org.mockito.ArgumentMatchers.anyList()
}
