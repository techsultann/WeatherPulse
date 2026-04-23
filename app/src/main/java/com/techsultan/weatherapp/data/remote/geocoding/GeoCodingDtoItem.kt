package com.techsultan.weatherapp.data.remote.geocoding


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeoCodingDtoItem(
    @SerialName("country")
    val country: String?,
    @SerialName("lat")
    val lat: Double?,
    @SerialName("local_names")
    val localNames: LocalNames?,
    @SerialName("lon")
    val lon: Double?,
    @SerialName("name")
    val name: String?,
    @SerialName("state")
    val state: String?
)