package com.example.weather.modul

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.weather.R
import com.example.weather.modul.shared.ForecastPreview
import kotlin.math.roundToInt

class WeatherLocationSelector : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            WeatherLocationSelectorScreen(
                onCitySelected = { (context as? Activity)?.finish() },
                onBack = { (context as? Activity)?.finish() }
            )
        }
    }
}

@Composable
fun WeatherLocationSelectorScreen(
    onCitySelected: () -> Unit,
    onBack: () -> Unit,
    viewModel: CitySearchViewModel = viewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val savedCitiesWeather by viewModel.savedCitiesWeather.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    val backIcon: Painter = painterResource(id = R.drawable.arrow_back_icon)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B263B))
            .padding(top = 24.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Image(
                    painter = backIcon,
                    contentDescription = "Back Icon",
                    modifier = Modifier.size(32.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Управление городами",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        TextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            placeholder = { Text("Введите название города", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true,
            leadingIcon = {
                Icon(
                    painterResource(id = R.drawable.search_icon),
                    contentDescription = null,
                    tint = Color.White
                )
            },
            trailingIcon = {
                if (isSearching) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.1f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                disabledContainerColor = Color.White.copy(alpha = 0.1f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White
            )
        )

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                if (savedCitiesWeather.isNotEmpty() && searchQuery.isEmpty()) {
                    item {
                        Text(
                            text = "Сохраненные города",
                            style = MaterialTheme.typography.titleMedium.copy(color = Color.White.copy(alpha = 0.8f)),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(savedCitiesWeather) { cityWeather ->
                        CityWeatherCard(
                            cityWeather = cityWeather,
                            onClick = {
                                viewModel.selectCity(cityWeather.name)
                                onCitySelected()
                            },
                            onDelete = { viewModel.removeCity(cityWeather.name) }
                        )
                    }
                }
            }

            if (searchQuery.isNotEmpty()) {
                val savedCityNames = savedCitiesWeather.map { it.name }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1B263B)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    if (searchResults.isNotEmpty()) {
                        items(searchResults.size) { index ->
                            val location = searchResults[index]
                            val isAdded = savedCityNames.contains(location.name)
                            val isPrimary = index == 0
                            val forecast = if (isPrimary) viewModel.primaryResultForecast.collectAsState().value else null

                            SearchResultItem(
                                location = location,
                                isAdded = isAdded,
                                forecast = forecast,
                                onClick = { 
                                    if (!isAdded) {
                                        viewModel.addCity(location) 
                                    }
                                }
                            )
                        }
                    } else if (!isSearching) {
                        item {
                            Text(
                                "Ничего не найдено",
                                modifier = Modifier.padding(16.dp),
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(location: SearchResultLocation, isAdded: Boolean, forecast: WeatherResponse?, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(location.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    Text("${location.region}, ${location.country}", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                }
                if (isAdded) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Добавлено", color = Color.White)
                        Icon(
                            Icons.Filled.KeyboardArrowRight,
                            contentDescription = "View weather",
                            tint = Color.White
                        )
                    }
                } else {
                    IconButton(onClick = onClick) {
                        Icon(
                            painterResource(id = R.drawable.add_icon),
                            contentDescription = "Add city",
                            tint = Color.White
                        )
                    }
                }
            }
            if (forecast != null) {
                ForecastPreview(forecast = forecast, textColor = Color.White)
            }
        }
    }
}
