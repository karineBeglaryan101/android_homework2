package com.example.hw02

import android.os.Bundle
import android.widget.ScrollView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hw02.ui.theme.HW02Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HW02Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "welcome_screen"
                    ) {
                        composable("welcome_screen") { WelcomeScreen(navController) }
                        composable("second_screen") { SecondScreen(navController) }
                    }
                }

            }
        }
    }
}

data class City(val name: String, val description: String, val imageResource: Int)

@Composable
fun WelcomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to the Main Screen!",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp)
        )
        Button(
            onClick = { navController.navigate("second_screen") },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Go to Second Screen")
        }
    }
}

@Composable
fun SecondScreen(navController: NavController) {
    val cities = listOf(
        City("Yerevan", "The capital of Armenia.The picture is taken by me from my window.", R.drawable.yerevan),
        City("New York", "One of my favorite cities. Empire State Building through my eyes.", R.drawable.new_york),
        City("Vienna", "Vienna waits for you. One of the most beautiful cities I have ever visited. The architecture is uncanny.", R.drawable.vienna),
        City("Prague", "Small yet so colorful, Prague has the most beautiful roofs I have ever seen.", R.drawable.prague),
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
                CityItem(city = city, expanded = cities.indexOf(city) == expandedCityIndex) {
                    expandedCityIndex = if (cities.indexOf(city) != expandedCityIndex) cities.indexOf(city) else -1
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

@Composable
fun CityItem(city: City, expanded: Boolean, onToggle: () -> Unit) {
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

                Text(
                    text = city.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}