package com.example.weather.api

import com.example.weather.BuildConfig
import com.example.weather.modul.SearchResultLocation
import com.example.weather.modul.WeatherResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class WeatherApi {
    private val apiKey: String = BuildConfig.WEATHER_API_KEY

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun getForecastWeather(city: String, days: Int = 5): WeatherResponse {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=$apiKey&q=$city&days=$days&aqi=no&alerts=no"

        return client.get(url).body()
    }

    suspend fun searchCity(query: String): List<SearchResultLocation> {
        val url = "https://api.weatherapi.com/v1/search.json?key=$apiKey&q=$query"
        return client.get(url).body()
    }
}