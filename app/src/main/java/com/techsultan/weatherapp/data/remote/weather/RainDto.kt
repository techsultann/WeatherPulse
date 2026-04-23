package com.techsultan.weatherapp.data.remote.weather

import com.google.gson.annotations.SerializedName

data class RainDto(
    @SerializedName("1h")
    val h: Double?
)