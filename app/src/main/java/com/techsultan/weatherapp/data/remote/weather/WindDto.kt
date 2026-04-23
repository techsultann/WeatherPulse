package com.techsultan.weatherapp.data.remote.weather

import com.google.gson.annotations.SerializedName

data class WindDto(
    @SerializedName("deg")
    val deg: Int?,
    @SerializedName("gust")
    val gust: Double?,
    @SerializedName("speed")
    val speed: Double?
)