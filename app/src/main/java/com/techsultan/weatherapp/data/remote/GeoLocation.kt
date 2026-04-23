package com.techsultan.weatherapp.data.remote

data class GeoLocation(
    val name: String,
    val localName: String?,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String?
) {
    val displayName: String get() = buildString {
        append(name)
        state?.let { append(", $it") }
        append(", $country")
    }
}