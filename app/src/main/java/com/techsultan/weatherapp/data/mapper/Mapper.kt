package com.techsultan.weatherapp.data.mapper

import com.techsultan.weatherapp.data.local.WeatherEntity
import com.techsultan.weatherapp.data.remote.GeoLocation
import com.techsultan.weatherapp.data.remote.forecast.ForecastDto
import com.techsultan.weatherapp.data.remote.forecast.ForecastItemDto
import com.techsultan.weatherapp.data.remote.geocoding.GeocodingDto
import com.techsultan.weatherapp.data.remote.weather.WeatherDto
import com.techsultan.weatherapp.domain.HourlyForecast
import com.techsultan.weatherapp.domain.WeatherCondition
import com.techsultan.weatherapp.domain.WeatherInfo
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class WeatherMapper @Inject constructor() {

    private val json = Json { ignoreUnknownKeys = true }
    private val dtTxtParser   = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val hourDisplay   = SimpleDateFormat("ha", Locale.getDefault())
    private val timeDisplay   = SimpleDateFormat("h:mm a", Locale.getDefault())

    fun dtoToDomain(
        dto: WeatherDto,
        forecast: ForecastDto
    ): WeatherInfo {
        // Take the first 8 forecast slots (covers next 24 hours at 3-hour intervals)
        val hourlySlots = forecast.list.take(8)
        val hourly = hourlySlots.mapIndexed { index, item ->
            HourlyForecast(
                time = if (index == 0) "Now" else formatForecastHour(item),
                temp = item.main.temp?.roundToInt() ?: 0,
                condition = mapConditionFromId(
                    item.weather.firstOrNull()?.id ?: 800,
                    item.sys.pod
                )
            )
        }

        // Probability of precipitation — use max across first 8 slots for accuracy
        val maxPop = hourlySlots.maxOfOrNull { it.pop } ?: 0.0
        val rainChance = (maxPop * 100).roundToInt()

        return WeatherInfo(
            cityName = dto.name ?: "Unknown",
            temperature = dto.main?.temp?.roundToInt(),
            feelsLike = dto.main?.feelsLike?.roundToInt(),
            humidity = dto.main?.humidity,
            pressure = dto.main?.pressure,
            windSpeed = (dto.wind?.speed?.times(3.6))?.roundToInt() ?: 0, // m/s → km/h
            condition = dto.weather?.firstOrNull()?.main ?: "Clear",
            description = dto.weather?.firstOrNull()?.description
                ?.replaceFirstChar { it.uppercase() } ?: "",
            rainChance = rainChance,
            hourlyForecast = hourly,
            isFavorite = false,
            lastUpdated = "Updated ${timeDisplay.format(Date())}",
            aiInsights = generateInsights(dto, rainChance)
        )
    }

    // ── Geocoding DTO → Domain ────────────────────────────────────────────────

    fun geocodingDtoToDomain(dto: GeocodingDto): GeoLocation {
        // Try to get a localised name for the device's language, fall back to English, then default name
        val deviceLang  = Locale.getDefault().language   // e.g. "fr", "de", "yo"
        val localName   = dto.localNames?.get(deviceLang)
            ?: dto.localNames?.get("en")

        return GeoLocation(
            name      = dto.name,
            localName = localName,
            lat       = dto.lat,
            lon       = dto.lon,
            country   = dto.country,
            state     = dto.state
        )
    }



    fun entityToDomain(entity: WeatherEntity): WeatherInfo = WeatherInfo(
        cityName = entity.cityName,
        temperature = entity.temperature,
        feelsLike = entity.feelsLike,
        humidity = entity.humidity,
        pressure = entity.pressure,
        windSpeed = entity.windSpeed,
        condition = entity.condition,
        description = entity.description,
        rainChance = entity.rainChance,
        hourlyForecast = runCatching {
            json.decodeFromString<List<HourlyForecast>>(entity.hourlyForecastJson)
        }.getOrDefault(emptyList()),
        isFavorite = entity.isFavorite,
        lastUpdated = entity.lastUpdated,
        aiInsights = entity.aiInsightsJson?.let {
            runCatching { json.decodeFromString<List<String>>(it) }.getOrDefault(emptyList())
        } ?: emptyList()
    )

    // ── Domain → Entity ───────────────────────────────────────────────────────

    fun domainToEntity(info: WeatherInfo): WeatherEntity = WeatherEntity(
        cityName = info.cityName,
        temperature = info.temperature,
        feelsLike = info.feelsLike,
        humidity = info.humidity,
        pressure = info.pressure,
        windSpeed = info.windSpeed,
        condition = info.condition,
        description = info.description,
        rainChance = info.rainChance,
        hourlyForecastJson = json.encodeToString(info.hourlyForecast),
        isFavorite = info.isFavorite,   // preserved — never overwritten by network
        lastUpdated = info.lastUpdated,
        aiInsightsJson = json.encodeToString(info.aiInsights)
    )

    private fun mapConditionFromId(id: Int, pod: String = "d"): WeatherCondition = when {
        id in 200..299 -> WeatherCondition.THUNDERSTORM
        id in 300..399 -> WeatherCondition.RAINY
        id in 500..599 -> WeatherCondition.RAINY
        id in 600..699 -> WeatherCondition.CLOUDY
        id in 700..799 -> WeatherCondition.CLOUDY
        id == 800      -> WeatherCondition.SUNNY
        id in 801..804 -> WeatherCondition.CLOUDY
        else           -> WeatherCondition.SUNNY
    }

    fun mapConditionFromMain(main: String): WeatherCondition = when (main.lowercase()) {
        "thunderstorm"         -> WeatherCondition.THUNDERSTORM
        "drizzle", "rain"      -> WeatherCondition.RAINY
        "snow"                 -> WeatherCondition.CLOUDY
        "mist", "smoke", "haze",
        "dust", "fog", "sand",
        "ash", "squall", "tornado" -> WeatherCondition.CLOUDY
        "clouds"               -> WeatherCondition.CLOUDY
        else                   -> WeatherCondition.SUNNY  // "Clear"
    }

    private fun formatForecastHour(item: ForecastItemDto): String {
        return try {
            val date = dtTxtParser.parse(item.dtTxt) ?: Date(item.dt * 1000)
            hourDisplay.format(date)
        } catch (e: Exception) {
            hourDisplay.format(Date(item.dt * 1000))
        }
    }

    private fun generateInsights(dto: WeatherDto, rainChance: Int): List<String> {
        val insights    = mutableListOf<String>()
        val temp        = dto.main?.temp
        val windKmh     = dto.wind?.speed?.times(3.6)
        val humidity    = dto.main?.humidity
        val weatherId   = dto.weather?.firstOrNull()?.id ?: 800
        val visibility  = dto.visibility       // metres

        when {
            weatherId in 200..299 ->
                insights.add("Thunderstorm warning — avoid outdoor activities")
            rainChance >= 70 ->
                insights.add("High chance of rain — carry an umbrella")
            rainChance >= 40 ->
                insights.add("Possible rain later — a light jacket may help")
        }

        // Temperature
        if (temp != null) {
            when {
                temp >= 38 -> insights.add("Extreme heat — stay indoors during peak hours")
                temp >= 32 -> insights.add("Very hot — stay hydrated and use sunscreen")
                temp >= 26 -> insights.add("Warm and pleasant — great for outdoor activities")
                temp <= 0  -> insights.add("Freezing temperatures — dress in heavy layers")
                temp <= 10 -> insights.add("Cold conditions — a warm coat is essential")
                temp <= 18 -> insights.add("Cool weather — a light jacket is recommended")
            }
        }

        // Wind
        if (windKmh != null) {
            when {
                windKmh >= 60 -> insights.add("Strong winds — secure outdoor items and drive carefully")
                windKmh >= 40 -> insights.add("Windy conditions — hold on to your hat!")
            }
        }

        // Humidity
        if (humidity != null) {
            if (humidity >= 85) {
                insights.add("Very high humidity — it will feel hotter than the temperature shows")
            }
        }

        // Visibility
        if (visibility != null) {
            if (visibility < 1000) {
                insights.add("Poor visibility — drive with caution")
            }
        }

        // Fallback if nothing triggered
        if (insights.isEmpty()) {
            insights.add("Comfortable conditions — a great day to head outside")
        }

        return insights
    }
}