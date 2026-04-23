package com.techsultan.weatherapp.ui.presentation

import app.cash.turbine.test
import com.techsultan.weatherapp.domain.WeatherInfo
import com.techsultan.weatherapp.domain.use_case.GetWeatherListUseCase
import com.techsultan.weatherapp.domain.use_case.SearchCitiesUseCase
import com.techsultan.weatherapp.domain.use_case.ToggleFavoriteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    @Mock
    private lateinit var getWeatherListUseCase: GetWeatherListUseCase
    @Mock
    private lateinit var searchCitiesUseCase: SearchCitiesUseCase
    @Mock
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should emit weather list from GetWeatherListUseCase`() = runTest {
        // Given
        val weatherList = listOf(createWeatherInfo("London"))
        `when`(getWeatherListUseCase()).thenReturn(flowOf(weatherList))

        // When
        viewModel = WeatherViewModel(getWeatherListUseCase, searchCitiesUseCase, toggleFavoriteUseCase)

        // Then
        viewModel.weatherList.test {
            // Initial empty list from stateIn
            assertEquals(emptyList<WeatherInfo>(), awaitItem())
            
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("London", result[0].cityName)
        }
    }

    @Test
    fun `search query change should trigger searchCitiesUseCase`() = runTest {
        // Given
        val query = "Lagos"
        val searchResult = listOf(createWeatherInfo("Lagos"))
        `when`(getWeatherListUseCase()).thenReturn(flowOf(emptyList()))
        `when`(searchCitiesUseCase(query)).thenReturn(flowOf(searchResult))

        viewModel = WeatherViewModel(getWeatherListUseCase, searchCitiesUseCase, toggleFavoriteUseCase)

        // Then
        viewModel.weatherList.test {
            // Initial state (might be conflated if getWeatherListUseCase also returns empty)
            assertEquals(emptyList<WeatherInfo>(), awaitItem())

            // When
            viewModel.onSearchQueryChange(query)

            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Lagos", result[0].cityName)
        }
    }

    @Test
    fun `toggleFavorite should call toggleFavoriteUseCase`() = runTest {
        // Given
        `when`(getWeatherListUseCase()).thenReturn(flowOf(emptyList()))
        viewModel = WeatherViewModel(getWeatherListUseCase, searchCitiesUseCase, toggleFavoriteUseCase)
        val cityName = "London"

        // When
        viewModel.toggleFavorite(cityName)
        advanceUntilIdle()

        // Then
        verify(toggleFavoriteUseCase).invoke(cityName)
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
