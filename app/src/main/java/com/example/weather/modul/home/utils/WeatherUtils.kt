package com.example.weather.modul.home.utils

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

internal fun formatDayOfWeek(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = inputFormat.parse(dateString) ?: return ""
        val calendar = Calendar.getInstance().apply { time = date }
        val today = Calendar.getInstance()

        if (calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
            calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
            "Сегодня"
        } else {
            SimpleDateFormat("E", Locale("ru")).format(date).replaceFirstChar { it.uppercase() }
        }
    } catch (e: Exception) {
        ""
    }
}

internal fun createBackgroundBrush(weatherType: WeatherType): Brush {
    return when (weatherType) {
        WeatherType.CLEAR_OR_SUNNY -> Brush.verticalGradient(colors = listOf(Color(0xFF2E86C1), Color(0xFF85C1E9)))
        WeatherType.PARTLY_CLOUDY -> Brush.verticalGradient(colors = listOf(Color(0xFF5D6D7E), Color(0xFFAEB6BF)))
        WeatherType.CLOUDY_OR_OVERCAST -> Brush.verticalGradient(colors = listOf(Color(0xFF424242), Color(0xFF9E9E9E)))
        WeatherType.MIST_OR_FOG -> Brush.verticalGradient(colors = listOf(Color(0xFF78909C), Color(0xFFB0BEC5)))
        WeatherType.RAINY -> Brush.verticalGradient(colors = listOf(Color(0xFF2C3E50), Color(0xFF566573)))
        WeatherType.SNOWY -> Brush.verticalGradient(colors = listOf(Color(0xFF81D4FA), Color(0xFFE1F5FE)))
        WeatherType.SLEET -> Brush.verticalGradient(colors = listOf(Color(0xFF607D8B), Color(0xFFB0BEC5)))
        WeatherType.THUNDERSTORM -> Brush.verticalGradient(colors = listOf(Color(0xFF212121), Color(0xFF484848)))
        WeatherType.UNKNOWN -> Brush.verticalGradient(colors = listOf(Color(0xFF3a6073), Color(0xFF16222A)))
    }
}

enum class WeatherType {
    CLEAR_OR_SUNNY, PARTLY_CLOUDY, CLOUDY_OR_OVERCAST, MIST_OR_FOG,
    RAINY, SNOWY, SLEET, THUNDERSTORM, UNKNOWN;

    companion object {
        fun fromWeatherCode(code: Int): WeatherType {
            return when (code) {
                1000 -> CLEAR_OR_SUNNY
                1003 -> PARTLY_CLOUDY
                1006, 1009 -> CLOUDY_OR_OVERCAST
                1030, 1135, 1147 -> MIST_OR_FOG
                1063, 1072, 1150, 1153, 1168, 1171, 1180, 1183, 1186, 1189,
                1192, 1195, 1198, 1201, 1240, 1243, 1246 -> RAINY
                1066, 1210, 1213, 1216, 1219, 1222, 1225, 1255, 1258, 1279, 1282 -> SNOWY
                1069, 1204, 1207, 1237, 1249, 1252, 1261, 1264 -> SLEET
                1087, 1273, 1276 -> THUNDERSTORM
                else -> UNKNOWN
            }
        }
    }
}
