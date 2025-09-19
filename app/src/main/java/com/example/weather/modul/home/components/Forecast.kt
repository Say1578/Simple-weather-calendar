package com.example.weather.modul.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weather.modul.ForecastDay
import com.example.weather.modul.HourData
import com.example.weather.modul.home.components.CardBase
import com.example.weather.modul.home.utils.formatDayOfWeek
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun DailyForecastCard(forecastDays: List<ForecastDay>) {
    CardBase(title = "Прогноз на ${forecastDays.size} дней") {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            forecastDays.forEachIndexed { index, forecastDay ->
                DailyForecastItem(forecastDay)
                if (index < forecastDays.size - 1) {
                    Divider(color = Color.White.copy(alpha = 0.2f), thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
fun DailyForecastItem(day: ForecastDay) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = formatDayOfWeek(day.date),
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f)
        )
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1.5f)) {
            AsyncImage(
                model = "https:${day.day.condition.icon}",
                contentDescription = day.day.condition.text,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = day.day.condition.text,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                maxLines = 1,
            )
        }
        Text(
            text = "${day.day.maxTempC.roundToInt()}° / ${day.day.minTempC.roundToInt()}°",
            color = Color.White,
            fontSize = 18.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun HourlyForecastCard(hours: List<HourData>, currentTimeString: String) {
    val currentHour = remember {
        try {
            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(currentTimeString)?.hours ?: 0
        } catch (e: Exception) { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    }

    val filteredHours = remember(hours, currentHour) {
        hours.filter { it.time.substring(11, 13).toInt() >= currentHour }
    }

    CardBase(title = "Прогноз на 24 ч") {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(items = filteredHours, key = { it.time }) { hour ->
                HourlyForecastItem(
                    hour = hour,
                    isNow = filteredHours.indexOf(hour) == 0
                )
            }
        }
    }
}

@Composable
fun HourlyForecastItem(hour: HourData, isNow: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = if (isNow) "Сейчас" else hour.time.substring(11, 16),
            color = Color.White,
            fontSize = 16.sp
        )
        AsyncImage(
            model = "https:${hour.condition.icon}",
            contentDescription = hour.condition.text,
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = "${hour.tempC.roundToInt()}°",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
