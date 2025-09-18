package com.example.weather.modul

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay

@Composable
fun ScreenTransition(
    loadingScreen: @Composable () -> Unit,
    nextScreen: @Composable () -> Unit,
    onFinished: (() -> Unit)? = null
) {
    var loadingFinished by remember { mutableStateOf(false) }
    var transitionProgress by remember { mutableStateOf(0f) }

    // запуск загрузки
    if (!loadingFinished) {
        loadingScreen()
        LaunchedEffect(Unit) {
            delay(2000) // имитация загрузки
            loadingFinished = true
        }
    }

    // анимация перехода
    LaunchedEffect(loadingFinished) {
        if (loadingFinished) {
            val duration = 600
            val steps = 60
            val stepTime = duration / steps
            repeat(steps) { i ->
                transitionProgress = (i + 1) / steps.toFloat()
                delay(stepTime.toLong())
            }
            transitionProgress = 1f
            onFinished?.invoke()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (!loadingFinished || transitionProgress < 1f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = 1f - transitionProgress }
            ) {
                loadingScreen()
            }
        }

        if (transitionProgress > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = transitionProgress },
                contentAlignment = Alignment.Center
            ) {
                nextScreen()
            }
        }
    }
}