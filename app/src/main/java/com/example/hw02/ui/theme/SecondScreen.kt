package com.example.hw02.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hw02.City
import com.example.hw02.R
import com.example.hw02.item.CityItem
import com.example.hw02.resources.Descriptions
import com.example.hw02.viewmodel.WeatherViewModel

@Composable
fun SecondScreen(navController: NavController, viewModel: WeatherViewModel) {
    val cities = listOf(
        City(
            "Yerevan",
            Descriptions.YEREVAN.description,
            R.drawable.yerevan
        ),
        City(
            "New York",
            Descriptions.NEW_YORK.description,
            R.drawable.new_york
        ),
        City(
            "Vienna",
            Descriptions.VIENNA.description,
            R.drawable.vienna
        ),
        City(
            "Prague",
            Descriptions.PRAGUE.description,
            R.drawable.prague
        ),
    )
    var expandedCityIndex by remember { mutableIntStateOf(-1) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "List of Cities:",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn {
            items(cities) { city ->
                CityItem(
                    city = city,
                    expanded = cities.indexOf(city) == expandedCityIndex,
                    viewModel = viewModel
                ) {
                    if (cities.indexOf(city) != expandedCityIndex) {
                        viewModel.fetchWeatherData(city)
                    }
                    expandedCityIndex =
                        if (cities.indexOf(city) != expandedCityIndex) cities.indexOf(city) else -1

                }
            }
        }
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Go To Main Screen")
        }
    }
}