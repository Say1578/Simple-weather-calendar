package com.example.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.example.weather.modul.LoadingScreen
import com.example.weather.modul.ScreenTransition
import com.example.weather.modul.WeatherHome
import com.example.weather.ui.theme.WeatherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            WeatherTheme {
                ScreenTransition(
                    loadingScreen = { LoadingScreen {} },
                    nextScreen = { WeatherHome() }
                )
            }
        }
    }
}
