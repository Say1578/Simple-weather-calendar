package com.example.weather.modul

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather.R
import com.example.weather.modul.home.WeatherHome
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AppEntry() {
    var showLoading by remember { mutableStateOf(true) }

    Crossfade(
        targetState = showLoading,
        animationSpec = tween(durationMillis = 1500) // плавность перехода
    ) { loading ->
        if (loading) {
            LoadingScreen {
                showLoading = false // переключаемся на основной экран
            }
        } else {
            WeatherHome() // тут твой основной экран
        }
    }
}

@Composable
fun LoadingScreen(onTimeout: () -> Unit) {
    // Таймер (когда закончится загрузка → переход на другой экран)
    LaunchedEffect(Unit) {
        delay(2500) // время загрузки
        onTimeout()
    }

    val infiniteTransition = rememberInfiniteTransition()

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val radians = angle * (PI / 180f).toFloat()
    val radiusX = 300f
    val radiusY = 600f
    val offsetX = radiusX * cos(radians)
    val offsetY = radiusY * sin(radians)

    val baseColors = listOf(
        Color(0xFF0D1B2A),
        Color(0xFF1B263B),
        Color(0xFF415A77),
        Color(0xFF778DA9)
    )

    val animatedColors = baseColors.map {
        it.copy(
            red = (it.red * shimmer).coerceIn(0f, 1f),
            green = (it.green * shimmer).coerceIn(0f, 1f),
            blue = (it.blue * shimmer).coerceIn(0f, 1f)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = animatedColors,
                    start = Offset(offsetX, offsetY),
                    end = Offset(offsetX + 1000f, offsetY + 1000f)
                )
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .align(Alignment.Center)
                .size(120.dp)
        )

        Text(
            text = "Wait a second...",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp)
        )
    }
}

@Composable
fun MainScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B263B)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Main Screen",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
