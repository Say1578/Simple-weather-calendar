package com.example.weather.modul

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.api.WeatherApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SavedCityWeather(
    val name: String,
    val weather: WeatherResponse?
)

class CitySearchViewModel(application: Application) : AndroidViewModel(application) {
    private val weatherApi = WeatherApi()
    private val context = application.applicationContext

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<SearchResultLocation>>(emptyList())
    val searchResults: StateFlow<List<SearchResultLocation>> = _searchResults

    private val _savedCitiesWeather = MutableStateFlow<List<SavedCityWeather>>(emptyList())
    val savedCitiesWeather: StateFlow<List<SavedCityWeather>> = _savedCitiesWeather

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private var searchJob: Job? = null

    init {
        loadSavedCitiesWeather()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        if (query.length > 2) {
            searchJob = viewModelScope.launch {
                delay(300)
                searchCities(query)
            }
        } else {
            _searchResults.value = emptyList()
        }
    }

    private fun searchCities(query: String) {
        viewModelScope.launch {
            _isSearching.value = true
            try {
                _searchResults.value = weatherApi.searchCity(query)
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun addCity(city: SearchResultLocation) {
        CityStorage.addCity(context, city.name)
        loadSavedCitiesWeather()
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }

    fun removeCity(cityName: String) {
        CityStorage.removeCity(context, cityName)
        loadSavedCitiesWeather()
    }

    fun selectCity(cityName: String) {
        CityStorage.setSelectedCity(context, cityName)
    }

    fun loadSavedCitiesWeather() {
        viewModelScope.launch {
            val savedCities = CityStorage.getSavedCities(context)
            _savedCitiesWeather.value = savedCities.map { cityName ->
                SavedCityWeather(cityName, null)
            }

            savedCities.forEachIndexed { index, cityName ->
                try {
                    val weather = weatherApi.getForecastWeather(cityName, 1)
                    _savedCitiesWeather.value = _savedCitiesWeather.value.toMutableList().also {
                        it[index] = SavedCityWeather(cityName, weather)
                    }
                } catch (e: Exception) {

                }
            }
        }
    }
}
