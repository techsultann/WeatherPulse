package com.techsultan.weatherapp.data.remote.weather

import com.google.gson.annotations.SerializedName

data class CoordDto(
    @SerializedName("lat")
    val lat: Double?,
    @SerializedName("lon")
    val lon: Double?
)