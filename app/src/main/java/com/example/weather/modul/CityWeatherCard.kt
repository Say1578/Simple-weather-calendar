package com.example.weather.modul

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlin.math.roundToInt

@Composable
fun CityWeatherCard(
    cityWeather: SavedCityWeather,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(cityWeather.name, fontSize = 20.sp, color = Color.White)
                cityWeather.weather?.current?.condition?.text?.let {
                    Text(it, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                }
            }
            Spacer(Modifier.width(16.dp))
            cityWeather.weather?.current?.let { current ->
                Text(
                    "${current.tempC.roundToInt()}°",
                    fontSize = 32.sp,
                    color = Color.White
                )
                AsyncImage(
                    model = "https:${current.condition.icon}",
                    contentDescription = current.condition.text,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete city", tint = Color.White)
            }
        }
    }
}
