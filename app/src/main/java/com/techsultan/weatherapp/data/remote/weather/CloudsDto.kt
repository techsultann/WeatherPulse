package com.techsultan.weatherapp.data.remote.weather

import com.google.gson.annotations.SerializedName

data class CloudsDto(
    @SerializedName("all")
    val all: Int?
)