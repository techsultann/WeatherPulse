package com.techsultan.weatherapp.domain.use_case

import com.techsultan.weatherapp.domain.WeatherInfo
import com.techsultan.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import kotlinx.coroutines.flow.first

class SearchCitiesUseCaseTest {

    @Mock
    private lateinit var repository: WeatherRepository

    private lateinit var searchCitiesUseCase: SearchCitiesUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        searchCitiesUseCase = SearchCitiesUseCase(repository)
    }

    @Test
    fun `invoke should return sorted list from repository`() = runBlocking {
        // Given
        val query = "Lagos"
        val weatherList = listOf(
            createWeatherInfo("Abuja", isFavorite = false),
            createWeatherInfo("Lagos", isFavorite = true),
            createWeatherInfo("Ibadan", isFavorite = false)
        )
        `when`(repository.searchCities(query)).thenReturn(flowOf(weatherList))

        // When
        val result = searchCitiesUseCase(query).first()

        // Then
        assertEquals(3, result.size)
        assertEquals("Lagos", result[0].cityName) // Favorite first
        assertEquals("Abuja", result[1].cityName) // Alphabetical second
        assertEquals("Ibadan", result[2].cityName) // Alphabetical third
    }

    private fun createWeatherInfo(cityName: String, isFavorite: Boolean): WeatherInfo {
        return WeatherInfo(
            cityName = cityName,
            temperature = 25,
            condition = "Sunny",
            feelsLike = 27,
            humidity = 60,
            windSpeed = 10,
            pressure = 1012,
            description = "Clear sky",
            lastUpdated = "10:00 AM",
            isFavorite = isFavorite
        )
    }
}
