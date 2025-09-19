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
                _searchResults.value = weatherApi.searchCity(query).filter {
                    !it.name.contains("airport", ignoreCase = true) && it.region.isNotEmpty()
                }
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun addCity(city: SearchResultLocation) {
        CityStorage.addCity(context, city.name)
        val newCityName = city.name
        viewModelScope.launch {
            val currentCities = _savedCitiesWeather.value.toMutableList()
            currentCities.add(0, SavedCityWeather(newCityName, null))
            _savedCitiesWeather.value = currentCities

            try {
                val weather = WeatherRepository.getForecastWeather(newCityName, 1)
                val index = _savedCitiesWeather.value.indexOfFirst { it.name == newCityName }
                if (index != -1) {
                    _savedCitiesWeather.value = _savedCitiesWeather.value.toMutableList().also {
                        it[index] = SavedCityWeather(newCityName, weather)
                    }
                }
            } catch (e: Exception) {
                // error
            }
        }
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }

    fun removeCity(cityName: String) {
        CityStorage.removeCity(context, cityName)
        _savedCitiesWeather.value = _savedCitiesWeather.value.filter { it.name != cityName }
    }

    fun selectCity(cityName: String) {
        CityStorage.setSelectedCity(context, cityName)
    }

    fun loadSavedCitiesWeather() {
        viewModelScope.launch {
            val savedCities = CityStorage.getSavedCities(context)
            val citiesWeather = mutableListOf<SavedCityWeather>()
            savedCities.forEach { cityName ->
                try {
                    val weather = WeatherRepository.getForecastWeather(cityName, 1)
                    citiesWeather.add(SavedCityWeather(cityName, weather))
                } catch (e: Exception) {
                    // error
                }
            }
            _savedCitiesWeather.value = citiesWeather
        }
    }
}