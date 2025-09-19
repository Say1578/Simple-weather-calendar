package com.example.weather.modul.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather.modul.AstroData
import com.example.weather.modul.CurrentData
import com.example.weather.modul.DayData
import com.example.weather.modul.home.components.CardBase
import kotlin.math.roundToInt

@Composable
fun DetailsGrid(current: CurrentData, astro: AstroData, day: DayData) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            WindCard(windKph = current.windKph, windDir = current.windDir, windDegree = current.wind_degree, modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DetailInfoCard(title = "Влажность", value = "${current.humidity}%", modifier = Modifier.weight(1f))
            DetailInfoCard(title = "Ощущается", value = "${current.feelslikeC.roundToInt()}°", modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DetailInfoCard(title = "УФ-индекс", value = "${current.uv.roundToInt()}", modifier = Modifier.weight(1f))
            DetailInfoCard(title = "Давление", value = "${current.pressureMb.roundToInt()} мбар", modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DetailInfoCard(title = "Вер-ть дождя", value = "${day.dailyChanceOfRain}%", modifier = Modifier.weight(1f))

        }
    }
}

@Composable
fun DetailInfoCard(title: String, value: String, modifier: Modifier = Modifier) {
    CardBase(modifier = modifier.height(IntrinsicSize.Min)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title.uppercase(), color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Light)
        }
    }
}

@Composable
fun WindCard(windKph: Double, windDir: String, windDegree: Int, modifier: Modifier = Modifier) {
    CardBase(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "ВЕТЕР", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                WindIndicator(windDegree = windDegree)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${windKph.roundToInt()}",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Light
                    )
                    Text(text = "км/ч", color = Color.White.copy(alpha = 0.8f))
                }
            }
        }
    }
}

@Composable
fun WindIndicator(windDegree: Int) {
    Canvas(modifier = Modifier.size(60.dp)) {
        val radius = size.minDimension / 2
        val center = Offset(radius, radius)

        drawCircle(
            color = Color.White.copy(alpha = 0.2f),
            radius = radius,
            style = Stroke(width = 2.dp.toPx())
        )

        rotate(degrees = windDegree.toFloat(), pivot = center) {
            val path = Path().apply {
                moveTo(center.x, center.y - radius)
                lineTo(center.x - 5.dp.toPx(), center.y - radius + 10.dp.toPx())
                lineTo(center.x + 5.dp.toPx(), center.y - radius + 10.dp.toPx())
                close()
            }
            drawPath(path, color = Color.White)
            drawLine(
                color = Color.White,
                start = Offset(center.x, center.y),
                end = Offset(center.x, center.y + radius * 0.8f),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}
