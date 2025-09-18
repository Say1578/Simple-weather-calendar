package com.example.weather.modul

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weather.R
import com.example.weather.api.WeatherApi
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun WeatherHome() {
    val context = LocalContext.current
    var weatherResponse by remember { mutableStateOf<WeatherResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            weatherResponse = WeatherApi().getForecastWeather("Zaporizhzhya")
        } catch (e: Exception) {
            errorMessage = "Ошибка загрузки: ${e.message}"
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    val backgroundBrush = weatherResponse?.let {
        createBackgroundBrush(WeatherType.fromWeatherCode(it.current.condition.code))
    } ?: Brush.verticalGradient(colors = listOf(Color(0xFF3a6073), Color(0xFF16222A)))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage!!,
                    color = Color.White,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center).padding(32.dp)
                )
            }
            weatherResponse != null -> {
                WeatherContent(weatherResponse!!)
            }
        }

        TopBar(
            onAddClick = { context.startActivity(Intent(context, WeatherLocationSelector::class.java)) },
            onSettingsClick = { context.startActivity(Intent(context, AppSettings::class.java)) }
        )
    }
}

@Composable
fun WeatherContent(data: WeatherResponse) {
    val todayForecast = data.forecast.forecastDay.first()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 80.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        item {
            Box(
                modifier = Modifier.height(screenHeight * 0.45f),
                contentAlignment = Alignment.Center
            ) {
                CurrentWeatherSection(data.location, data.current, todayForecast.day)
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { DailyForecastCard(data.forecast.forecastDay) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { HourlyForecastCard(todayForecast.hour, data.location.localtime) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { DetailsGrid(data.current, todayForecast.astro, todayForecast.day) }
    }
}

@Composable
fun TopBar(onAddClick: () -> Unit, onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.add_icon),
            contentDescription = "Добавить город",
            modifier = Modifier.size(32.dp).clickable(onClick = onAddClick)
        )
        Image(
            painter = painterResource(id = R.drawable.settings_icon),
            contentDescription = "Настройки",
            modifier = Modifier.size(32.dp).clickable(onClick = onSettingsClick)
        )
    }
}

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

@Composable
fun CardBase(modifier: Modifier = Modifier, title: String? = null, content: @Composable () -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
    ) {
        Column {
            title?.let {
                Text(
                    text = it.uppercase(),
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 4.dp)
                )
            }
            content()
        }
    }
}

private fun formatDayOfWeek(dateString: String): String {
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

@Composable
private fun createBackgroundBrush(weatherType: WeatherType): Brush {
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
