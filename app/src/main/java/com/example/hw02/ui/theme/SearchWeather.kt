package com.example.hw02.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hw02.Settings
import com.example.hw02.TemperatureType
import com.example.hw02.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchCityWeather(navController: NavController, viewModel: WeatherViewModel) {
    var cityName by remember { mutableStateOf("") }
    var showTextField by remember { mutableStateOf(true) }
    val weatherData = viewModel.searchWeatherData.observeAsState()
    val temperatureType = Settings.temperatureType.observeAsState()
    val isLoading by viewModel.isloading.observeAsState()
    val showTemperature = weatherData.value != null

    if (isLoading!!) {
        Box(
            modifier = Modifier.size(60.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                strokeWidth = 4.dp
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showTextField) {
            TextField(
                value = cityName,
                onValueChange = { cityName = it },
                label = { Text("Enter City Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (cityName.isNotEmpty()) {
                        viewModel.searchWeatherData(cityName)
                        showTextField = false
                    }
                },
                enabled = cityName.isNotEmpty()
            ) {
                Text("Get Temperature")
            }
        } else if (showTemperature) {
                val temp = if (temperatureType.value == TemperatureType.CELSIUS) "${weatherData.value!!.current.temp_c}°C" else "${weatherData.value!!.current.temp_f}°F"
                Text(
                    text = "Temperature in $cityName is: $temp",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(8.dp)
                )
            Button(
                onClick = {
                    showTextField = true
                    cityName = ""
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Enter Another City")
            }
            Button(
                onClick = { navController.navigate("welcome_screen") },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Go to Main Screen")
            }
        } else {
            Text("Can't get the data for the city.")
            Button(
                onClick = { navController.navigate("welcome_screen") },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Go to Main Screen")
            }
        }
    }
}
