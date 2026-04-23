package com.techsultan.weatherapp.worker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.techsultan.weatherapp.core.notification.WeatherNotificationManager
import com.techsultan.weatherapp.domain.WeatherInfo
import com.techsultan.weatherapp.domain.use_case.GetFavoriteCityWeatherUseCase
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WeatherNotificationWorkerTest {

    @Mock
    private lateinit var getFavoriteCityWeather: GetFavoriteCityWeatherUseCase
    @Mock
    private lateinit var notificationManager: WeatherNotificationManager
    
    private lateinit var context: Context

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun `doWork should show notification when favorite city exists`() = runBlocking {
        // Given
        val favoriteCity = createWeatherInfo("Lagos")
        `when`(getFavoriteCityWeather()).thenReturn(flowOf(favoriteCity))

        val worker = TestListenableWorkerBuilder<WeatherNotificationWorker>(context)
            .setWorkerFactory(object : androidx.work.WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: WorkerParameters
                ): ListenableWorker? {
                    return WeatherNotificationWorker(
                        appContext,
                        workerParameters,
                        getFavoriteCityWeather,
                        notificationManager
                    )
                }
            })
            .build()

        // When
        val result = worker.doWork()

        // Then
        assertEquals(ListenableWorker.Result.success(), result)
        verify(notificationManager).showWeatherNotification(favoriteCity)
    }

    @Test
    fun `doWork should not show notification when no favorite city exists`() = runBlocking {
        // Given
        `when`(getFavoriteCityWeather()).thenReturn(flowOf(null))

        val worker = TestListenableWorkerBuilder<WeatherNotificationWorker>(context)
            .setWorkerFactory(object : androidx.work.WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: WorkerParameters
                ): ListenableWorker? {
                    return WeatherNotificationWorker(
                        appContext,
                        workerParameters,
                        getFavoriteCityWeather,
                        notificationManager
                    )
                }
            })
            .build()

        // When
        val result = worker.doWork()

        // Then
        assertEquals(ListenableWorker.Result.success(), result)
        verify(notificationManager, never()).showWeatherNotification(any())
    }

    private fun createWeatherInfo(cityName: String): WeatherInfo {
        return WeatherInfo(
            cityName = cityName,
            temperature = 25,
            condition = "Sunny",
            feelsLike = 27,
            humidity = 60,
            windSpeed = 10,
            pressure = 1012,
            description = "Clear sky",
            lastUpdated = "10:00 AM"
        )
    }
}
