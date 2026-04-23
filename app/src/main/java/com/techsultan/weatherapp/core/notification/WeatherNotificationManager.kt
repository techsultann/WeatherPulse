package com.techsultan.weatherapp.core.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.techsultan.weatherapp.MainActivity
import com.techsultan.weatherapp.R
import com.techsultan.weatherapp.core.util.Constants
import com.techsultan.weatherapp.domain.WeatherInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherNotificationManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Hourly weather updates for your favourite city"
            enableVibration(false)
            setShowBadge(true)
        }
        val systemManager = context.getSystemService(NotificationManager::class.java)
        systemManager?.createNotificationChannel(channel)
    }

    fun showWeatherNotification(weather: WeatherInfo) {

        if (!hasNotificationPermission()) return

        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(getWeatherIcon(weather.condition))
            .setContentTitle(buildTitle(weather))
            .setContentText(buildBody(weather))
            .setStyle(

                NotificationCompat.BigTextStyle()
                    .bigText(buildExpandedBody(weather))
            )
            .setContentIntent(buildOpenAppIntent())
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(
            Constants.NOTIFICATION_ID,
            notification
        )
    }

    private fun buildTitle(weather: WeatherInfo): String =
        "${weather.cityName}  ${weather.temperature}°C"

    private fun buildBody(weather: WeatherInfo): String =
        "${weather.description}  ·  Feels like ${weather.feelsLike}°C"

    private fun buildExpandedBody(weather: WeatherInfo): String = buildString {
        appendLine("${weather.description}  ·  Feels like ${weather.feelsLike}°C")
        appendLine("💧 ${weather.humidity}%   💨 ${weather.windSpeed} km/h   🌧 ${weather.rainChance}% rain")
        if (weather.aiInsights.isNotEmpty()) {
            appendLine()
            append(weather.aiInsights.first())
        }
    }

    private fun buildOpenAppIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getWeatherIcon(condition: String?): Int = when (condition?.lowercase()) {
        "thunderstorm" -> R.drawable.thunderstorm
        "rain", "drizzle" -> R.drawable.rain
        "snow" -> R.drawable.snow
        "clouds" -> R.drawable.clouds
        else -> R.drawable.sunny
    }


    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}