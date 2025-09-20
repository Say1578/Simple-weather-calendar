package com.example.weather.modul.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun PagerIndicator(
    currentPage: Int,
    pageCount: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = Color.White,
    inactiveColor: Color = Color.White.copy(alpha = 0.5f),
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until pageCount) {
            val distance = abs(currentPage - i)
            val alpha = 1f - (distance * 0.2f).coerceIn(0f, 0.9f)

            val isActive = i == currentPage
            val color by animateColorAsState(
                targetValue = if (isActive) activeColor else inactiveColor.copy(alpha = if (distance > 3) 0f else alpha),
                animationSpec = spring()
            )
            val width by animateDpAsState(
                targetValue = if (isActive) 12.dp else 6.dp,
                animationSpec = spring()
            )

            if (distance <= 3) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(width = width, height = 6.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}
