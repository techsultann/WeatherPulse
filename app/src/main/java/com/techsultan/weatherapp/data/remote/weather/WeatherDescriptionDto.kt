package com.techsultan.weatherapp.data.remote.weather

import com.google.gson.annotations.SerializedName

data class WeatherDescriptionDto(
    @SerializedName("description")
    val description: String?,
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("main")
    val main: String?
)