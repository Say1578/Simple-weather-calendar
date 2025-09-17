package com.example.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.example.weather.modul.LoadingScreen
import com.example.weather.ui.theme.WeatherTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            WeatherTheme {
                // состояние загрузки
                var loadingFinished by remember { mutableStateOf(false) }
                var transitionProgress by remember { mutableStateOf(0f) } // 0f = Loading, 1f = NextScreen

                // Запускаем таймер загрузки
                if (!loadingFinished) {
                    LoadingScreen {
                        loadingFinished = true
                    }
                }

                // Когда загрузка закончена — плавно анимируем переход
                LaunchedEffect(loadingFinished) {
                    if (loadingFinished) {
                        val duration = 120 // миллисекунд
                        val steps = 60
                        val stepTime = duration / steps
                        repeat(steps) { i ->
                            transitionProgress = (i + 1) / steps.toFloat()
                            delay(stepTime.toLong())
                        }
                        transitionProgress = 1f
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    // LoadingScreen с затуханием
                    if (!loadingFinished || transitionProgress < 1f) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { alpha = 1f - transitionProgress }
                        ) {
                            LoadingScreen {}
                        }
                    }

                    // NextScreen с проявлением
                    if (transitionProgress > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { alpha = transitionProgress },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Следующий экран",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}
