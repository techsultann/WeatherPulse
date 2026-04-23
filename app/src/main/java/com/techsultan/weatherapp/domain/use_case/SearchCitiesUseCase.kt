package com.techsultan.weatherapp.domain.use_case

import com.techsultan.weatherapp.domain.WeatherInfo
import com.techsultan.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchCitiesUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(query: String): Flow<List<WeatherInfo>> {
        return repository.searchCities(query.trim()).map { list ->
            list.sortedWith(
                compareByDescending<WeatherInfo> { it.isFavorite }
                    .thenBy { it.cityName }
            )
        }
    }
}