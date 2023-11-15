package com.example.hw02

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hw02.ui.theme.HW02Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val TAG = "WEATHER"

class MainActivity : ComponentActivity() {
    private val locationPermissionRequestCode = 123
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HW02Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    requestLocationPermission()
                    LocationProvider.initialize(this)
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "welcome_screen"
                    ) {
                        val viewModel = WeatherViewModel()
                           viewModel.fetchWeatherForLocation(
                            this@MainActivity,
                            weatherApiService,
                            "8e7826a07c1644d280d82050231311"
                        )
                        composable("welcome_screen") { WelcomeScreen(navController, viewModel) }
                        composable("second_screen") { SecondScreen(navController, viewModel) }
                    }
                }
            }

        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionRequestCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
            }
        }
    }
}

val loggingInterceptor = HttpLoggingInterceptor().also {
    it.level = HttpLoggingInterceptor.Level.BODY
}
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build();

val retrofit = Retrofit.Builder()
    .baseUrl("https://api.weatherapi.com/")
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface WeatherApiService {
    @GET("v1/current.json")
    suspend fun getWeather(
        @Query("q") name: String,
        @Query("key") apiKey: String
    ): WeatherData
}


val weatherApiService: WeatherApiService = retrofit.create(WeatherApiService::class.java)

class WeatherViewModel() : ViewModel() {
    private val _weatherData = MutableLiveData<WeatherData>()
    private val _currentWeatherData = MutableLiveData<WeatherData>()
    val weatherData: LiveData<WeatherData> get() = _weatherData
    val currentWeatherData: LiveData<WeatherData> get() = _currentWeatherData

    fun fetchWeatherForLocation(
        context: Context,
        weatherApiService: WeatherApiService,
        apiKey: String
    ) {
        try {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                viewModelScope.launch {
                    var location = LocationProvider.getCurrentLocation()
                    if (location == null) {
                        delay(6000L)
                        location = LocationProvider.getCurrentLocation()
                    }
                    Log.e(TAG, "LOCATION BEFORE REQUEST" + location.toString())
                    if (location != null) {
                        val cityName = "${location.first},${location.second}"
                        val weather = weatherApiService.getWeather(cityName, apiKey)
                        _currentWeatherData.postValue(weather)
                    } else {
                        Log.e(TAG, "No last known location available")
                    }
                }
            } else {
                Log.e(TAG, "Location permission not granted")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching weather data", e)
        }
    }


    fun fetchWeatherData(city: City) {
        Log.e(TAG, "fetchWeatherData")
        viewModelScope.launch {
            try {
                val apiKey = getApiKey()
                val weather = weatherApiService.getWeather(city.name, apiKey)
                _weatherData.postValue(weather)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    private fun getApiKey(): String {
        return "8e7826a07c1644d280d82050231311"
    }
}

data class City(val name: String, val description: String, val imageResource: Int)

@Composable
fun WelcomeScreen(navController: NavController, viewModel: WeatherViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val weatherData by viewModel.currentWeatherData.observeAsState()

        weatherData?.let {
            Text(text = "In your current location temperature is: ${it.current.temp_c}°C")
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
            Text("Go to Second Screen")
        }
    }
}

@Composable
fun SecondScreen(navController: NavController, viewModel: WeatherViewModel) {
    val cities = listOf(
        City(
            "Yerevan",
            "The capital of Armenia.The picture is taken by me from my window.",
            R.drawable.yerevan
        ),
        City(
            "New York",
            "One of my favorite cities. Empire State Building through my eyes.",
            R.drawable.new_york
        ),
        City(
            "Vienna",
            "Vienna waits for you. One of the most beautiful cities I have ever visited. The architecture is uncanny.",
            R.drawable.vienna
        ),
        City(
            "Prague",
            "Small yet so colorful, Prague has the most beautiful roofs I have ever seen.",
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

@Composable
fun CityItem(city: City, expanded: Boolean, viewModel: WeatherViewModel, onToggle: () -> Unit) {
    val weatherData = viewModel.weatherData.observeAsState()

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
                    Text(
                        text = "Temperature: ${it.current.temp_c}°C",
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

typealias LocationCoordinate = Pair<Double, Double>

object LocationProvider {
    private var currentLocation: LocationCoordinate? = null
    fun getCurrentLocation(): LocationCoordinate? {
        return currentLocation
    }

    @SuppressLint("MissingPermission")
    fun initialize(context: Context) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 5f) {
            it.longitude = 180 + it.longitude
            currentLocation = it.longitude to it.latitude

            Log.e(TAG, "initialize: ${currentLocation}")
        }
    }
}
