package com.example.hw02.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.hw02.City
import com.example.hw02.Settings
import com.example.hw02.TemperatureType
import com.example.hw02.viewmodel.WeatherViewModel

@Composable
fun CityItem(city: City, expanded: Boolean, viewModel: WeatherViewModel, onToggle: () -> Unit) {
    val weatherData = viewModel.weatherData.observeAsState()
    val temperatureType = Settings.temperatureType.observeAsState()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp)
        ) {
            Text(
                text = city.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onToggle() }
            )
            if (expanded) {
                Image(
                    painter = painterResource(id = city.imageResource),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(shape = MaterialTheme.shapes.medium),
                )
                weatherData.value?.let {
                    val temp = if (temperatureType.value == TemperatureType.CELSIUS) "${it.current.temp_c}°C" else "${it.current.temp_f}°F"
                    Text(
                        text = "Temperature: $temp",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Text(
                    text = city.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
