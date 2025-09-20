package com.example.weather.modul.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.weather.modul.WeatherResponse
import com.example.weather.modul.home.utils.formatDayOfWeek
import kotlin.math.roundToInt

@Composable
fun ForecastPreview(forecast: WeatherResponse, textColor: Color = Color.Unspecified) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        forecast.forecast.forecastDay.take(5).forEach { day ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(formatDayOfWeek(day.date), color = textColor)
                AsyncImage(
                    model = "https:${day.day.condition.icon}",
                    contentDescription = day.day.condition.text,
                    modifier = Modifier.size(40.dp)
                )
                Text("${day.day.maxTempC.roundToInt()}°/${day.day.minTempC.roundToInt()}°", color = textColor)
            }
        }
    }
}
