package com.example.weather.modul

import com.example.weather.api.WeatherApi

private val inMemoryCache = mutableMapOf<String, CachedWeather>()
private const val CACHE_DURATION_MS = 15 * 60 * 1000

object WeatherRepository {

    suspend fun getForecastWeather(city: String, days: Int = 5): WeatherResponse {
        val cacheKey = "$city-$days"
        val cachedItem = inMemoryCache[cacheKey]
        if (cachedItem != null && (System.currentTimeMillis() - cachedItem.timestamp) < CACHE_DURATION_MS) {
            return cachedItem.weatherResponse
        }

        val response = WeatherApi().getForecastWeather(city, days)
        inMemoryCache[cacheKey] = CachedWeather(response, System.currentTimeMillis())
        return response
    }

    suspend fun searchCity(query: String): List<SearchResultLocation> {
        return WeatherApi().searchCity(query)
    }
}

data class CachedWeather(val weatherResponse: WeatherResponse, val timestamp: Long)
