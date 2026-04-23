package com.techsultan.weatherapp.data.remote.forecast

import com.google.gson.annotations.SerializedName
import com.techsultan.weatherapp.data.remote.weather.CloudsDto
import com.techsultan.weatherapp.data.remote.weather.CoordDto
import com.techsultan.weatherapp.data.remote.weather.MainDto
import com.techsultan.weatherapp.data.remote.weather.RainDto
import com.techsultan.weatherapp.data.remote.weather.WeatherDescriptionDto
import com.techsultan.weatherapp.data.remote.weather.WindDto

data class ForecastDto(
    @SerializedName("cod")     val cod: String,
    @SerializedName("message") val message: Int,
    @SerializedName("cnt")     val cnt: Int,
    @SerializedName("list")    val list: List<ForecastItemDto>,
    @SerializedName("city")    val city: ForecastCityDto
)

data class ForecastItemDto(
    @SerializedName("dt")         val dt: Long,
    @SerializedName("main")       val main: MainDto,
    @SerializedName("weather")    val weather: List<WeatherDescriptionDto>,
    @SerializedName("clouds")     val clouds: CloudsDto,
    @SerializedName("wind")       val wind: WindDto,
    @SerializedName("visibility") val visibility: Int = 10000,
    @SerializedName("pop")        val pop: Double = 0.0,
    @SerializedName("rain")       val rain: RainDto? = null,
    @SerializedName("sys")        val sys: ForecastSysDto,
    @SerializedName("dt_txt")     val dtTxt: String
)

data class ForecastSysDto(
    @SerializedName("pod") val pod: String
)

data class ForecastCityDto(
    @SerializedName("id")         val id: Long,
    @SerializedName("name")       val name: String,
    @SerializedName("coord")      val coord: CoordDto,
    @SerializedName("country")    val country: String,
    @SerializedName("population") val population: Int,
    @SerializedName("timezone")   val timezone: Int,      // Shift in seconds from UTC
    @SerializedName("sunrise")    val sunrise: Long,
    @SerializedName("sunset")     val sunset: Long
)