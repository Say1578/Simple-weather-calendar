package com.example.weather.modul.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather.modul.CurrentData
import com.example.weather.modul.DayData
import com.example.weather.modul.LocationData
import kotlin.math.roundToInt

@Composable
fun CurrentWeatherSection(location: LocationData, current: CurrentData, day: DayData) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(location.name, fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.Light)
        Spacer(modifier = Modifier.height(4.dp))
        Text("${current.tempC.roundToInt()}°", fontSize = 96.sp, color = Color.White, fontWeight = FontWeight.Thin)
        Text(current.condition.text, fontSize = 22.sp, color = Color.White.copy(alpha = 0.9f))
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Макс.: ${day.maxTempC.roundToInt()}° / Мин.: ${day.minTempC.roundToInt()}°",
            fontSize = 18.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}
