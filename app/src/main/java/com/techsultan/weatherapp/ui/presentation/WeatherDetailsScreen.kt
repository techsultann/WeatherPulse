package com.techsultan.weatherapp.ui.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techsultan.weatherapp.domain.HourlyForecast
import com.techsultan.weatherapp.domain.WeatherCondition
import com.techsultan.weatherapp.domain.WeatherInfo
import com.techsultan.weatherapp.ui.theme.AiBadgeBg
import com.techsultan.weatherapp.ui.theme.DividerColor
import com.techsultan.weatherapp.ui.theme.GlassLight
import com.techsultan.weatherapp.ui.theme.GreenBottom
import com.techsultan.weatherapp.ui.theme.GreenTop
import com.techsultan.weatherapp.ui.theme.NowHighlight
import com.techsultan.weatherapp.ui.theme.TextMuted
import com.techsultan.weatherapp.ui.theme.TextPrimary
import com.techsultan.weatherapp.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun WeatherDetailScreen(
    weather: WeatherInfo,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    // Shared entrance animation state — drives sequential section reveals
    val heroAlpha   = remember { Animatable(0f) }
    val heroOffsetY = remember { Animatable(30f) }
    val card1Alpha  = remember { Animatable(0f) }
    val card1Offset = remember { Animatable(24f) }
    val card2Alpha  = remember { Animatable(0f) }
    val card2Offset = remember { Animatable(24f) }
    val card3Alpha  = remember { Animatable(0f) }
    val card3Offset = remember { Animatable(24f) }

    LaunchedEffect(Unit) {
        // Hero slides up from 30dp + fades
        heroAlpha.animateTo(1f,   tween(420, easing = FastOutSlowInEasing))
        heroOffsetY.animateTo(0f, tween(420, easing = FastOutSlowInEasing))
        // Cards stagger in 100ms apart
        delay(80)
        card1Alpha.animateTo(1f,   tween(360, easing = FastOutSlowInEasing))
        card1Offset.animateTo(0f,  tween(360, easing = FastOutSlowInEasing))
        delay(80)
        card2Alpha.animateTo(1f,   tween(360, easing = FastOutSlowInEasing))
        card2Offset.animateTo(0f,  tween(360, easing = FastOutSlowInEasing))
        delay(80)
        card3Alpha.animateTo(1f,   tween(360, easing = FastOutSlowInEasing))
        card3Offset.animateTo(0f,  tween(360, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(GreenTop, GreenBottom)))
    ) {
        DecorativeOrbs()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(scrollState)
        ) {
            BackButton(onBack = onBack)


            Box(
                modifier = Modifier
                    .graphicsLayer {
                        alpha = heroAlpha.value
                        translationY = heroOffsetY.value.dp.toPx()
                    }
            ) {
                HeroSection(weather = weather)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .graphicsLayer {
                        alpha = card1Alpha.value
                        translationY = card1Offset.value.dp.toPx()
                    }
            ) {
                StatsGridCard(weather = weather)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        alpha = card2Alpha.value
                        translationY = card2Offset.value.dp.toPx()
                    }
            ) {
                HourlyForecastCard(hourlyForecast = weather.hourlyForecast)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .graphicsLayer {
                        alpha = card3Alpha.value
                        translationY = card3Offset.value.dp.toPx()
                    }
            ) {
                AiInsightsCard(insights = weather.aiInsights)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}


@Composable
private fun BackButton(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(start = 16.dp, top = 12.dp)
            .size(40.dp)
            .clip(CircleShape)
            .background(GlassLight)
            .clickable(onClick = onBack),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = TextPrimary,
            modifier = Modifier.size(20.dp)
        )
    }
}


@Composable
private fun DecorativeOrbs() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.TopEnd)
                .graphicsLayer { translationX = 100f; translationY = -80f }
                .background(
                    Brush.radialGradient(
                        listOf(Color.White.copy(alpha = 0.08f), Color.Transparent)
                    ),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomStart)
                .graphicsLayer { translationX = -60f; translationY = 80f }
                .background(
                    Brush.radialGradient(
                        listOf(Color.White.copy(alpha = 0.05f), Color.Transparent)
                    ),
                    CircleShape
                )
        )
    }
}


@Composable
private fun HeroSection(weather: WeatherInfo) {
    // Infinite slow scale pulse on the icon — feels alive
    val infiniteTransition = rememberInfiniteTransition(label = "iconPulse")
    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = 1.08f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconScale"
    )
    // Slow rotation for sun icon
    val iconRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing)
        ),
        label = "iconRotation"
    )

    val isSunny = weather.condition?.lowercase().let {
        it == "clear" || it == "sunny"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Weather icon — glowing circle behind it
        Box(contentAlignment = Alignment.Center) {
            // Glow ring
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color.White.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    )
            )
            Icon(
                imageVector = getConditionIcon(weather.condition ?: ""),
                contentDescription = weather.condition,
                tint = TextPrimary,
                modifier = Modifier
                    .size(72.dp)
                    .scale(iconScale)
                    .then(
                        if (isSunny) Modifier.rotate(iconRotation) else Modifier
                    )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Condition label
        weather.description.ifBlank { weather.condition }
            ?.replaceFirstChar { it.uppercase() }?.let {
                Text(
                    text = it,
                    color = TextSecondary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }

        Spacer(modifier = Modifier.height(4.dp))

        // Dominant temperature
        Text(
            text = "${weather.temperature}°",
            color = TextPrimary,
            fontSize = 88.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 88.sp,
            textAlign = TextAlign.Center
        )


        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Feels like ${weather.feelsLike}°",
                color = TextSecondary,
                fontSize = 15.sp
            )
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(TextMuted)
            )
            Text(
                text = "${weather.rainChance}% chance of rain",
                color = TextSecondary,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // City + updated — left-aligned editorial block
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = weather.cityName,
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = weather.lastUpdated,
                color = TextMuted,
                fontSize = 12.sp,
                letterSpacing = 0.3.sp
            )
        }
    }
}

@Composable
private fun StatsGridCard(weather: WeatherInfo) {
    val stats = listOf(
        Triple(Icons.Filled.WaterDrop, "${weather.humidity}%",       "Humidity"),
        Triple(Icons.Filled.Air,       "${weather.windSpeed} km/h",  "Wind"),
        Triple(Icons.Filled.Umbrella,  "${weather.rainChance}%",     "Rain chance"),
        Triple(Icons.Filled.Thermostat,"${weather.feelsLike}°",      "Feels like"),
        Triple(Icons.Filled.Compress,  "${weather.pressure} hPa",    "Pressure"),
        Triple(Icons.Filled.WbSunny,   weather.condition,            "Condition")
    )

    GlassCard {
        // Section label
        SectionLabel(text = "Conditions")

        Spacer(modifier = Modifier.height(14.dp))

        // 2-column grid
        stats.chunked(2).forEachIndexed { rowIndex, rowStats ->
            if (rowIndex > 0) {
                HorizontalDivider(
                    color = DividerColor,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                rowStats.forEachIndexed { colIndex, (icon, value, label) ->
                    if (colIndex == 1) {
                        Box(
                            modifier = Modifier
                                .width(0.5.dp)
                                .height(52.dp)
                                .background(DividerColor)
                        )
                    }
                    StatTile(
                        icon     = icon,
                        value    = value ?: "",
                        label    = label,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (rowStats.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun StatTile(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(GlassLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = TextPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
        Column {
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = label,
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun HourlyForecastCard(hourlyForecast: List<HourlyForecast>) {
    Column {
        SectionLabel(
            text = "Hourly Forecast",
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(GlassLight)
        ) {
            LazyRow(
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                itemsIndexed(hourlyForecast) { index, forecast ->
                    AnimatedForecastItem(forecast = forecast, index = index)
                }
            }
        }
    }
}

@Composable
private fun AnimatedForecastItem(forecast: HourlyForecast, index: Int) {
    val offsetY = remember { Animatable(20f) }
    val alpha   = remember { Animatable(0f) }

    LaunchedEffect(forecast.time) {
        delay(index * 40L)
        offsetY.animateTo(0f, tween(300, easing = FastOutSlowInEasing))
        alpha.animateTo(1f,   tween(300, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
            translationY = offsetY.value.dp.toPx()
            this.alpha = alpha.value
        }
    ) {
        HourlyForecastItem(forecast = forecast)
    }
}

@Composable
fun HourlyForecastItem(forecast: HourlyForecast) {
    val isNow = forecast.time == "Now"

    Column(
        modifier = Modifier
            .width(62.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (isNow) NowHighlight else Color.Transparent)
            .padding(horizontal = 6.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = forecast.time,
            color = if (isNow) TextPrimary else TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isNow) FontWeight.SemiBold else FontWeight.Normal
        )

        Icon(
            imageVector = when (forecast.condition) {
                WeatherCondition.SUNNY        -> Icons.Filled.WbSunny
                WeatherCondition.CLOUDY       -> Icons.Filled.Cloud
                WeatherCondition.RAINY        -> Icons.Filled.Umbrella
                WeatherCondition.THUNDERSTORM -> Icons.Filled.Thunderstorm
            },
            contentDescription = null,
            tint = if (isNow) TextPrimary else TextSecondary,
            modifier = Modifier.size(22.dp)
        )

        Text(
            text = "${forecast.temp}°",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
private fun AiInsightsCard(insights: List<String>) {
    GlassCard {
        // Badge header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AiBadgeBg)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    val sparkle by rememberInfiniteTransition(label = "sparkle")
                        .animateFloat(
                            initialValue = 0.7f,
                            targetValue  = 1f,
                            animationSpec = infiniteRepeatable(
                                tween(1200, easing = LinearEasing),
                                RepeatMode.Reverse
                            ),
                            label = "sparkleAlpha"
                        )
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = TextPrimary.copy(alpha = sparkle),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Smart summary",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Insight rows — staggered fade-in
        insights.forEachIndexed { index, insight ->
            AnimatedInsightRow(
                insight = insight,
                index   = index
            )
            if (index < insights.lastIndex) {
                HorizontalDivider(
                    color = DividerColor,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun AnimatedInsightRow(insight: String, index: Int) {
    val alpha  = remember { Animatable(0f) }
    val offset = remember { Animatable(10f) }

    LaunchedEffect(insight) {
        delay(200L + index * 80L)
        alpha.animateTo(1f,  spring(stiffness = Spring.StiffnessMediumLow))
        offset.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.alpha   = alpha.value
                translationY = offset.value.dp.toPx()
            },
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(GlassLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getInsightIcon(insight),
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier.size(14.dp)
            )
        }
        Text(
            text = insight,
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun GlassCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(GlassLight)
            .padding(horizontal = 18.dp, vertical = 18.dp),
        content = content
    )
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        color = TextSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.5.sp,
        modifier = modifier
    )
}


private fun getConditionIcon(condition: String): ImageVector = when (condition.lowercase()) {
    "thunderstorm"                            -> Icons.Filled.Thunderstorm
    "rain", "drizzle"                         -> Icons.Filled.Umbrella
    "clouds", "mist", "fog", "haze", "smoke" -> Icons.Filled.Cloud
    else                                      -> Icons.Filled.WbSunny
}

fun getInsightIcon(insight: String): ImageVector = when {
    insight.contains("outdoor",   ignoreCase = true) -> Icons.Filled.WbSunny
    insight.contains("rain",      ignoreCase = true) -> Icons.Filled.Umbrella
    insight.contains("umbrella",  ignoreCase = true) -> Icons.Filled.Umbrella
    insight.contains("wind",      ignoreCase = true) -> Icons.Filled.Air
    insight.contains("hot",       ignoreCase = true) -> Icons.Filled.Thermostat
    insight.contains("cold",      ignoreCase = true) -> Icons.Filled.Thermostat
    insight.contains("humid",     ignoreCase = true) -> Icons.Filled.WaterDrop
    insight.contains("thunder",   ignoreCase = true) -> Icons.Filled.Thunderstorm
    insight.contains("indoor",    ignoreCase = true) -> Icons.Filled.Home
    else                                             -> Icons.Filled.Info
}