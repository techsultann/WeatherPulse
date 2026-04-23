package com.techsultan.weatherapp.ui.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techsultan.weatherapp.domain.HourlyForecast
import com.techsultan.weatherapp.domain.WeatherCondition
import com.techsultan.weatherapp.domain.WeatherInfo
import com.techsultan.weatherapp.ui.theme.FavoriteTint
import com.techsultan.weatherapp.ui.theme.GlassLight
import com.techsultan.weatherapp.ui.theme.GreenBottom
import com.techsultan.weatherapp.ui.theme.GreenTop
import com.techsultan.weatherapp.ui.theme.StarActive
import com.techsultan.weatherapp.ui.theme.TextHint
import com.techsultan.weatherapp.ui.theme.TextPrimary
import com.techsultan.weatherapp.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WeatherHomeScreen(
    viewModel: WeatherViewModel,
    onCityClick: (WeatherInfo) -> Unit
) {
    val weatherList by viewModel.weatherList.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(GreenTop, GreenBottom))
            )
    ) {
        DecorativeOrbs()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            HomeHeader()

            WeatherSearchBar(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (weatherList.isNotEmpty()) {
                Text(
                    text = if (searchQuery.isBlank())
                        "${weatherList.size} cities tracked"
                    else
                        "${weatherList.size} result${if (weatherList.size != 1) "s" else ""}",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )
            }


            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 4.dp,
                    bottom = 32.dp
                )
            ) {
                itemsIndexed(
                    items = weatherList,
                    key = { _, weather -> weather.cityName }
                ) { index, weather ->
                    AnimatedCityWeatherItem(
                        weather = weather,
                        index = index,
                        onToggleFavorite = { viewModel.toggleFavorite(weather.cityName) },
                        onClick = { onCityClick(weather) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DecorativeOrbs() {
    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.TopEnd)
                .graphicsLayer { translationX = 80f; translationY = -60f }
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color.White.copy(alpha = 0.07f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.BottomStart)
                .graphicsLayer { translationX = -50f; translationY = 60f }
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color.White.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )
    }
}

@Composable
private fun HomeHeader() {
    val dateFormat = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()) }
    val today = remember { dateFormat.format(Date()) }

    Column(
        modifier = Modifier.padding(
            start = 24.dp,
            end = 24.dp,
            top = 24.dp,
            bottom = 20.dp
        )
    ) {

        Text(
            text = today.uppercase(),
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Weather",
            color = TextPrimary,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 44.sp
        )


        Text(
            text = "Your cities at a glance",
            color = TextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
    }
}


@Composable
fun WeatherSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(GlassLight)
            .then(
                Modifier.then(
                    Modifier.clip(RoundedCornerShape(16.dp))
                )
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "Search",
            tint = if (query.isEmpty()) TextHint else TextSecondary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(
                    text = "Search cities…",
                    color = TextHint,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                textStyle = TextStyle(
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal
                ),
                cursorBrush = SolidColor(TextPrimary),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                interactionSource = interactionSource
            )
        }

        if (query.isNotEmpty()) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.20f))
                    .clickable { onQueryChange("") },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "×",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AnimatedCityWeatherItem(
    weather: WeatherInfo,
    index: Int,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    val offsetX = remember { Animatable(60f) }
    val alpha   = remember { Animatable(0f) }

    LaunchedEffect(weather.cityName) {
        // Stagger: each item waits 60ms × its position before starting
        delay((index * 60L).coerceAtMost(400L))
        // Slide in + fade simultaneously
        offsetX.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 380,
                easing = FastOutSlowInEasing
            )
        )
    }

    LaunchedEffect(weather.cityName) {
        delay((index * 60L).coerceAtMost(400L))
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 380,
                easing = FastOutSlowInEasing
            )
        )
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                translationX = offsetX.value.dp.toPx()
                this.alpha = alpha.value
            }
    ) {
        CityWeatherItem(
            weather = weather,
            onToggleFavorite = onToggleFavorite,
            onClick = onClick
        )
    }
}

@Composable
fun CityWeatherItem(
    weather: WeatherInfo,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                // Favourite cities get a slightly warmer/brighter glass tint
                if (weather.isFavorite)
                    Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.18f),
                            Color.White.copy(alpha = 0.10f)
                        )
                    )
                else
                    Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.13f),
                            Color.White.copy(alpha = 0.07f)
                        )
                    )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WeatherIconBox(condition = weather.condition ?: "")

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = weather.cityName,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (weather.isFavorite) {
                        Spacer(modifier = Modifier.width(6.dp))
                        FavoriteBadge()
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                weather.description.ifBlank { weather.condition }?.let {
                    Text(
                        text = it,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Mini stat row
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    WeatherMiniStat(
                        icon = Icons.Filled.WaterDrop,
                        value = "${weather.humidity}%"
                    )
                    WeatherMiniStat(
                        icon = Icons.Filled.Air,
                        value = "${weather.windSpeed}km/h"
                    )
                    WeatherMiniStat(
                        icon = Icons.Filled.Umbrella,
                        value = "${weather.rainChance}%"
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${weather.temperature}°",
                    color = TextPrimary,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 36.sp
                )

                Spacer(modifier = Modifier.height(4.dp))


                Text(
                    text = "feels ${weather.feelsLike}°",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (weather.isFavorite)
                                StarActive.copy(alpha = 0.20f)
                            else
                                Color.White.copy(alpha = 0.10f)
                        )
                        .clickable(onClick = onToggleFavorite),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (weather.isFavorite)
                            Icons.Filled.Star
                        else
                            Icons.Filled.StarBorder,
                        contentDescription = if (weather.isFavorite)
                            "Remove from favourites"
                        else
                            "Add to favourites",
                        tint = if (weather.isFavorite) StarActive else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}


@Composable
private fun WeatherIconBox(condition: String) {
    val icon: ImageVector = when (condition.lowercase()) {
        "thunderstorm" -> Icons.Filled.Thunderstorm
        "rain", "drizzle" -> Icons.Filled.Umbrella
        "clouds", "mist",
        "fog", "haze", "smoke" -> Icons.Filled.Cloud
        else -> Icons.Filled.WbSunny   // Clear
    }

    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = condition,
            tint = TextPrimary,
            modifier = Modifier.size(28.dp)
        )
    }
}


@Composable
private fun FavoriteBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(FavoriteTint.copy(alpha = 0.25f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "Pinned",
            color = FavoriteTint,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
    }
}


@Composable
private fun WeatherMiniStat(
    icon: ImageVector,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = value,
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal
        )
    }
}



