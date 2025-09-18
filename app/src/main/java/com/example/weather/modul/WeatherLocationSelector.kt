package com.example.weather.modul

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.weather.R
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer


class WeatherLocationSelector : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherLocationSelectorContent()
        }
    }
}

@Composable
fun WeatherLocationSelectorContent() {
    val backIcon: Painter = painterResource(id = R.drawable.arrow_back_icon)
    var query by remember { mutableStateOf("") }  // Состояние для текста поиска

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 2.dp)
    ) {
        // Кнопка назад
        IconButton(
            onClick = {

            },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Image(
                painter = backIcon,
                contentDescription = "Back Icon",
                modifier = Modifier
                    .size(50.dp)
                    .graphicsLayer(
                     colorFilter = ColorFilter.tint(Color.Black)  // черный цвет
                    )
            )
        }

        // Поле для поиска
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp, start = 16.dp, end = 16.dp)
        ) {
            // TextField для поиска с Material Design
            TextField(
                value = query,
                onValueChange = { query = it },
                // label = { Text("Search") },
                placeholder = { Text("Enter location...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                singleLine = true,
                leadingIcon = {
                    val searchIcon: Painter = painterResource(id = R.drawable.search_icon)
                    Image(
                        painter = searchIcon,
                        contentDescription = "Search Icon",
                        modifier = Modifier
                            .size(24.dp)
                            .graphicsLayer(
                                colorFilter = ColorFilter.tint(Color.Black)  // черный цвет
                            )
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                    unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                    disabledContainerColor = Color.Gray.copy(alpha = 0.1f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
        }
    }
}
