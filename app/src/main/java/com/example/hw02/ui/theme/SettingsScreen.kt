package com.example.hw02.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hw02.Settings.temperatureType
import com.example.hw02.TemperatureType
import com.example.hw02.viewmodel.WeatherViewModel

@Composable
fun SettingsScreen(navController: NavController) {
    var temperatureUnit = temperatureType.observeAsState()
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Text(
            text = "Welcome to the Settings Screen!",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Temperature Unit")
        Spacer(modifier = Modifier.height(4.dp) )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RadioButton(
                    selected = temperatureUnit.value == TemperatureType.CELSIUS,
                    onClick = {
                        temperatureType.postValue(TemperatureType.CELSIUS)
                    }
                )
                Text(text = "Celsius")
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RadioButton(
                    selected = temperatureUnit.value == TemperatureType.FAHRENHEIT,
                    onClick = {
                       temperatureType.postValue(TemperatureType.FAHRENHEIT)
                    }
                )
                Text(text = "Fahrenheit")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("welcome_screen") },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Go to Main Screen")
        }
    }


}
