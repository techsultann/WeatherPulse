package com.techsultan.weatherapp.ui.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.techsultan.weatherapp.domain.WeatherInfo
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CityListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun weatherItemsShouldBeDisplayed() {
        val viewModel: WeatherViewModel = mock()
        val weatherList = listOf(
            createWeatherInfo("London"),
            createWeatherInfo("Paris")
        )
        whenever(viewModel.weatherList).thenReturn(MutableStateFlow(weatherList))
        whenever(viewModel.searchQuery).thenReturn(MutableStateFlow(""))

        composeTestRule.setContent {
            WeatherHomeScreen(
                viewModel = viewModel,
                onCityClick = {}
            )
        }

        composeTestRule.onNodeWithText("London").assertIsDisplayed()
        composeTestRule.onNodeWithText("Paris").assertIsDisplayed()
    }

    @Test
    fun clickingWeatherItemShouldTriggerCallback() {
        val viewModel: WeatherViewModel = mock()
        val weather = createWeatherInfo("London")
        val weatherList = listOf(weather)
        var clickedCity: WeatherInfo? = null

        whenever(viewModel.weatherList).thenReturn(MutableStateFlow(weatherList))
        whenever(viewModel.searchQuery).thenReturn(MutableStateFlow(""))

        composeTestRule.setContent {
            WeatherHomeScreen(
                viewModel = viewModel,
                onCityClick = { clickedCity = it }
            )
        }

        composeTestRule.onNodeWithText("London").performClick()
        assert(clickedCity?.cityName == "London")
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
