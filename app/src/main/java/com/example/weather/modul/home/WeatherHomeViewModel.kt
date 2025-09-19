package com.example.weather.modul.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.modul.CityStorage
import com.example.weather.modul.WeatherRepository
import com.example.weather.modul.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface WeatherUiState {
    data class Success(val weather: WeatherResponse) : WeatherUiState
    data class Error(val message: String, val isCitySelectionError: Boolean) : WeatherUiState
    object Loading : WeatherUiState
}

class WeatherHomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiStates = MutableStateFlow<Map<String, WeatherUiState>>(emptyMap())
    val uiStates: StateFlow<Map<String, WeatherUiState>> = _uiStates.asStateFlow()

    private val _savedCities = MutableStateFlow<List<String>>(emptyList())
    val savedCities: StateFlow<List<String>> = _savedCities.asStateFlow()

    init {
        loadWeatherForSavedCities()
    }

    fun loadWeatherForSavedCities() {
        viewModelScope.launch {
            val cities = CityStorage.getSavedCities(getApplication())
            _savedCities.value = cities
            if (cities.isEmpty()) {
                _uiStates.value = mapOf("empty" to WeatherUiState.Error("Пожалуйста, добавьте город, чтобы увидеть погоду.", isCitySelectionError = true))
            } else {
                cities.forEach { city ->
                    _uiStates.value = _uiStates.value.toMutableMap().apply {
                        put(city, WeatherUiState.Loading)
                    }
                    launch {
                        try {
                            val weather = WeatherRepository.getForecastWeather(city)
                            _uiStates.value = _uiStates.value.toMutableMap().apply {
                                put(city, WeatherUiState.Success(weather))
                            }
                        } catch (e: Exception) {
                            _uiStates.value = _uiStates.value.toMutableMap().apply {
                                put(city, WeatherUiState.Error("Ошибка загрузки: ${e.message}", isCitySelectionError = false))
                            }
                        }
                    }
                }
            }
        }
    }

    fun retry(city: String) {
        viewModelScope.launch {
            _uiStates.value = _uiStates.value.toMutableMap().apply {
                put(city, WeatherUiState.Loading)
            }
            try {
                val weather = WeatherRepository.getForecastWeather(city)
                _uiStates.value = _uiStates.value.toMutableMap().apply {
                    put(city, WeatherUiState.Success(weather))
                }
            } catch (e: Exception) {
                _uiStates.value = _uiStates.value.toMutableMap().apply {
                    put(city, WeatherUiState.Error("Ошибка загрузки: ${e.message}", isCitySelectionError = false))
                }
            }
        }
    }
}
