package com.example.weather.modul

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement
import com.example.weather.R

enum class WeatherType {
    SUNNY, CLOUDY, RAINY
}

@Composable
fun WeatherHome() {
    val context = LocalContext.current
    val weather = WeatherType.RAINY // временно вручную менять

    val backgroundBrush = when (weather) {
        WeatherType.SUNNY -> Brush.verticalGradient(
            colors = listOf(Color(0xFF87CEEB), Color(0xFFfefcea))
        )
        WeatherType.CLOUDY -> Brush.verticalGradient(
            colors = listOf(Color(0xFF90A4AE), Color(0xFFCFD8DC))
        )
        WeatherType.RAINY -> Brush.verticalGradient(
            colors = listOf(Color(0xFF3a6073), Color(0xFF16222A))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(16.dp)
    ) {
        // Верхний ряд с кнопками
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Кнопка "плюс"
            Image(
                painter = painterResource(id = R.drawable.add_icon),
                contentDescription = "Добавить город",
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        context.startActivity(
                            Intent(context, WeatherLocationSelector::class.java)
                        )
                    }
            )

            // Кнопка "настройки"
            Image(
                painter = painterResource(id = R.drawable.settings_icon),
                contentDescription = "Настройки",
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        context.startActivity(Intent(context, AppSettings::class.java))
                    }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp), // отступ чтобы не налезало на кнопки
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Название города
            Text("Запорожье", fontSize = 28.sp, color = Color.White)

            Spacer(modifier = Modifier.height(4.dp))

            // Маленький тег
            Box(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.3f), shape = RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("Службы местоположения", fontSize = 14.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Температура
            Text("16°", fontSize = 80.sp, color = Color.White)

            // Облачно + min/max
            Text("Облачно  16°/12°", fontSize = 20.sp, color = Color.White)

            Spacer(modifier = Modifier.height(16.dp))

            // Индекс
            Box(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("ИКВ 60", fontSize = 16.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Карточка прогноза
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Text("Прогноз на 5 дней", fontSize = 18.sp, color = Color.White)

                Spacer(modifier = Modifier.height(12.dp))

                DayForecastRow("Чт", "Небольшой дождь", "16° / 12°")
                DayForecastRow("Пт", "Ясно", "22° / 13°")
                DayForecastRow("Сб", "Ясно", "22° / 13°")

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(50))
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Прогноз на 5 дней", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun DayForecastRow(day: String, desc: String, temp: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("$day  $desc", color = Color.White, fontSize = 16.sp)
        Text(temp, color = Color.White, fontSize = 16.sp)
    }
}
