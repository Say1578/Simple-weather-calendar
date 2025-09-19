package com.example.weather.modul

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.weather.modul.home.components.DailyForecastItem
import com.example.weather.modul.home.components.HourlyForecastItem
import com.example.weather.ui.theme.WeatherTheme
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class FiveDayForecastActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val forecastDaysJson = intent.getStringExtra("forecast") ?: "[]"
        val forecastDays: List<ForecastDay> = Json.decodeFromString(forecastDaysJson)

        setContent {
            WeatherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FiveDayForecastScreen(forecastDays)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiveDayForecastScreen(forecastDays: List<ForecastDay>) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Прогноз на 5 дней", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { (context as? Activity)?.finish() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1B263B))
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(forecastDays) { day ->
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        DailyForecastItem(day = day)
                        LazyRow(
                            modifier = Modifier.height(120.dp),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            items(day.hour) { hour ->
                                HourlyForecastItem(hour = hour, isNow = false)
                            }
                        }
                    }
                    Divider(color = Color.White.copy(alpha = 0.2f), thickness = 0.5.dp)
                }
            }
        }
    }
}
