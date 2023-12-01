package com.example.hw02.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.hw02.R
import com.example.hw02.Settings
import com.example.hw02.TemperatureType
import com.example.hw02.viewmodel.WeatherViewModel

@Composable
fun WelcomeScreen(navController: NavController, viewModel: WeatherViewModel) {
    val imageResource: Int = R.drawable.settings_button
    val painter: Painter = painterResource(id = imageResource)
    val temperatureType = Settings.temperatureType.observeAsState()
    Box(
        modifier = Modifier.fillMaxSize(),
    ){
        IconButton(
            onClick = { navController.navigate("settings_screen") },
            modifier = Modifier
                .size(100.dp)
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                painter = painter,
                contentDescription = "Settings",
                modifier = Modifier.size(48.dp)
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val weatherData by viewModel.currentWeatherData.observeAsState()

        weatherData?.let {
            val temp = if (temperatureType.value == TemperatureType.CELSIUS) "${it.current.temp_c}°C" else "${it.current.temp_f}°F"
            Text(text = "In your current location temperature is: $temp")
        }
        Text(
            text = "Welcome to the Main Screen!",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp)
        )
        Button(
            onClick = { navController.navigate("second_screen") },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("List of Cities")
        }
        Button(
            onClick = { navController.navigate("search_weather") },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Search City Weather")
        }

    }
}