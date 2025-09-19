package com.example.weather.modul

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val location: LocationData,
    val current: CurrentData,
    val forecast: ForecastData
)

@Serializable
data class LocationData(
    val name: String,
    val region: String,
    val country: String,
    val localtime: String
)

@Serializable
data class CurrentData(
    @SerialName("temp_c") val tempC: Double,
    @SerialName("feelslike_c") val feelslikeC: Double,
    val condition: ConditionData,
    @SerialName("wind_kph") val windKph: Double,
    @SerialName("wind_dir") val windDir: String,
    @SerialName("wind_degree") val wind_degree: Int,
    @SerialName("pressure_mb") val pressureMb: Double,
    val humidity: Int,
    val uv: Double
)

@Serializable
data class ConditionData(
    val text: String,
    val icon: String,
    val code: Int
)

@Serializable
data class ForecastData(
    @SerialName("forecastday") val forecastDay: List<ForecastDay>
)

@Serializable
data class ForecastDay(
    val date: String,
    val day: DayData,
    val astro: AstroData,
    val hour: List<HourData>
)

@Serializable
data class DayData(
    @SerialName("maxtemp_c") val maxTempC: Double,
    @SerialName("mintemp_c") val minTempC: Double,
    @SerialName("daily_chance_of_rain") val dailyChanceOfRain: Int,
    val condition: ConditionData
)

@Serializable
data class AstroData(
    val sunrise: String,
    val sunset: String
)

@Serializable
data class HourData(
    val time: String,
    @SerialName("temp_c") val tempC: Double,
    val condition: ConditionData
)

@Serializable
data class SearchResultLocation(
    val id: Int,
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val url: String
)