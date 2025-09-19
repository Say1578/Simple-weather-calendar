package com.example.weather.modul.home

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weather.modul.AppSettings
import com.example.weather.modul.WeatherLocationSelector
import com.example.weather.modul.WeatherResponse
import com.example.weather.modul.home.components.CurrentWeatherSection
import com.example.weather.modul.home.components.DailyForecastCard
import com.example.weather.modul.home.components.DetailsGrid
import com.example.weather.modul.home.components.ErrorScreen
import com.example.weather.modul.home.components.HourlyForecastCard
import com.example.weather.modul.home.components.TopBar
import com.example.weather.modul.home.utils.WeatherType
import com.example.weather.modul.home.utils.createBackgroundBrush

@Composable
fun WeatherHome(viewModel: WeatherHomeViewModel = viewModel()) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshSelectedCityAndLoadWeather()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val backgroundBrush = when (val state = uiState) {
        is WeatherUiState.Success -> createBackgroundBrush(WeatherType.fromWeatherCode(state.weather.current.condition.code))
        else -> Brush.verticalGradient(colors = listOf(Color(0xFF3a6073), Color(0xFF16222A)))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        when (val state = uiState) {
            is WeatherUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
            }
            is WeatherUiState.Error -> {
                ErrorScreen(
                    errorMessage = state.message,
                    isCitySelectionError = state.isCitySelectionError,
                    onRetry = {
                        if (state.isCitySelectionError) {
                            context.startActivity(Intent(context, WeatherLocationSelector::class.java))
                        } else {
                            viewModel.retry()
                        }
                    }
                )
            }
            is WeatherUiState.Success -> {
                WeatherContent(state.weather)
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
