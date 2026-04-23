package com.techsultan.weatherapp.domain.use_case

import com.techsultan.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(cityName: String) {
        repository.toggleFavorite(cityName)
    }
}