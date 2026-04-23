package com.techsultan.weatherapp.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.techsultan.weatherapp.core.notification.WeatherNotificationManager
import com.techsultan.weatherapp.domain.use_case.GetFavoriteCityWeatherUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class WeatherNotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val getFavoriteCityWeather: GetFavoriteCityWeatherUseCase,
    private val notificationManager: WeatherNotificationManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {

            val favoriteCity = getFavoriteCityWeather().firstOrNull()

            if (favoriteCity != null) {
                notificationManager.showWeatherNotification(favoriteCity)
            }

            Result.success()
        } catch (e: Exception) {

            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "WeatherNotificationWork"
    }
}