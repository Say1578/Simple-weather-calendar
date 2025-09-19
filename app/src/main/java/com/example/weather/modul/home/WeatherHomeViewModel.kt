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

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private var selectedCity: String? = null

    init {
        refreshSelectedCityAndLoadWeather()
    }

    fun refreshSelectedCityAndLoadWeather() {
        selectedCity = CityStorage.getSelectedCity(getApplication())
        loadWeatherData()
    }

    fun retry() {
        loadWeatherData()
    }

    private fun loadWeatherData() {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            val city = selectedCity
            if (city != null) {
                try {
                    val weather = WeatherRepository.getForecastWeather(city)
                    _uiState.value = WeatherUiState.Success(weather)
                } catch (e: Exception) {
                    _uiState.value = WeatherUiState.Error("Ошибка загрузки: ${e.message}", isCitySelectionError = false)
                }
            } else {
                _uiState.value = WeatherUiState.Error("Пожалуйста, добавьте город, чтобы увидеть погоду.", isCitySelectionError = true)
            }
        }
    }
}
