package com.techsultan.weatherapp.ui.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techsultan.weatherapp.domain.HourlyForecast
import com.techsultan.weatherapp.domain.WeatherCondition
import com.techsultan.weatherapp.domain.WeatherInfo

@Composable
fun WeatherHomeScreen(
    viewModel: WeatherViewModel,
    onCityClick: (WeatherInfo) -> Unit
) {
    val weatherList by viewModel.weatherList.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF90A17D), Color(0xFF4A5D3F))))
            .padding(16.dp)
    ) {
        Text(
            text = "Weather App",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp, top = 32.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(weatherList) { weather ->
                CityWeatherItem(
                    weather = weather,
                    onToggleFavorite = { viewModel.toggleFavorite(weather.cityName) },
                    onClick = { onCityClick(weather) }
                )
            }
        }
    }
}

@Composable
fun CityWeatherItem(
    weather: WeatherInfo,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = weather.cityName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = weather.condition, fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "${weather.temperature}°", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (weather.isFavorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (weather.isFavorite) Color.Yellow else Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherDetailScreen(weather: WeatherInfo, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF90A17D), Color(0xFF4A5D3F))))
            .padding(16.dp)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.padding(top = 32.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        // Main Weather Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.WbSunny,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )
                Text(text = "${weather.temperature}°", fontSize = 64.sp, fontWeight = FontWeight.Bold, color = Color.White)
                
                Text(text = weather.description, color = Color.White, modifier = Modifier.padding(bottom = 16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherDetailSmallItem(Icons.Filled.WaterDrop, "${weather.rainChance}%")
                    WeatherDetailSmallItem(Icons.Filled.Opacity, "${weather.humidity}%")
                    WeatherDetailSmallItem(Icons.Filled.Air, "${weather.windSpeed} kmh")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherDetailSmallItem(Icons.Filled.Thermostat, "Feels: ${weather.feelsLike}°")
                    WeatherDetailSmallItem(Icons.Filled.Compress, "${weather.pressure} hPa")
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = weather.cityName, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = weather.lastUpdated, fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f))
                }
            }
        }

        Text(text = "Weather Forecast", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
        ) {
            LazyRow(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(weather.hourlyForecast) { forecast ->
                    HourlyForecastItem(forecast)
                }
            }
        }

        Text(text = "Smart Summary", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "AI Insights", color = Color.White, fontSize = 12.sp)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                weather.aiInsights.forEach { insight ->
                    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(getInsightIcon(insight), contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = insight, color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherDetailSmallItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, color = Color.White, fontSize = 14.sp)
    }
}

@Composable
fun HourlyForecastItem(forecast: HourlyForecast) {
    val isNow = forecast.time == "Now"
    Column(
        modifier = Modifier
            .width(60.dp)
            .background(
                if (isNow) Color.White.copy(alpha = 0.2f) else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = forecast.time, color = if (isNow) Color.White else Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Icon(
            imageVector = when(forecast.condition) {
                WeatherCondition.SUNNY -> Icons.Filled.WbSunny
                WeatherCondition.CLOUDY -> Icons.Filled.Cloud
                WeatherCondition.RAINY -> Icons.Filled.Umbrella
                WeatherCondition.THUNDERSTORM -> Icons.Filled.Thunderstorm
            },
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "${forecast.temp}°", color = Color.White, fontWeight = FontWeight.Bold)
    }
}

fun getInsightIcon(insight: String): ImageVector {
    return when {
        insight.contains("outdoor", ignoreCase = true) -> Icons.Filled.WbSunny
        insight.contains("rain", ignoreCase = true) -> Icons.Filled.Thunderstorm
        insight.contains("umbrella", ignoreCase = true) -> Icons.Filled.Umbrella
        insight.contains("indoor", ignoreCase = true) -> Icons.Filled.Home
        else -> Icons.Filled.Info
    }
}
