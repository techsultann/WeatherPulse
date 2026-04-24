package com.techsultan.weatherapp.ui.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techsultan.weatherapp.core.util.Constants
import com.techsultan.weatherapp.domain.WeatherInfo
import com.techsultan.weatherapp.domain.repository.WeatherRepository
import com.techsultan.weatherapp.domain.use_case.GetWeatherListUseCase
import com.techsultan.weatherapp.domain.use_case.SearchCitiesUseCase
import com.techsultan.weatherapp.domain.use_case.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherListUseCase: GetWeatherListUseCase,
    private val searchCitiesUseCase: SearchCitiesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val repository: WeatherRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val weatherList: StateFlow<List<WeatherInfo>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                getWeatherListUseCase()
            } else {
                searchCitiesUseCase(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(cityName: String?) {
        cityName?.let {
            viewModelScope.launch {
                toggleFavoriteUseCase(it)
            }
        }
    }

    fun refreshWeather() {
        viewModelScope.launch {
            _isRefreshing.value = true
            repository.refreshWeather(Constants.CITIES)
            _isRefreshing.value = false
        }
    }
}
