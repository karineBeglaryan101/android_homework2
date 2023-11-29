package com.example.hw02

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hw02.ui.theme.HW02Theme
import com.example.hw02.ui.theme.SearchCityWeather
import com.example.hw02.ui.theme.SecondScreen
import com.example.hw02.ui.theme.SettingsScreen
import com.example.hw02.ui.theme.WelcomeScreen
import com.example.hw02.viewmodel.WeatherViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val TAG = "WEATHER"

class MainActivity : ComponentActivity() {
    private val locationPermissionRequestCode = 123
    @SuppressLint("SuspiciousIndentation")
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
                        composable("welcome_screen") { WelcomeScreen(navController, viewModel)}
                        composable("second_screen") { SecondScreen(navController, viewModel)}
                        composable("settings_screen") { SettingsScreen(navController)}
                        composable("search_weather") { SearchCityWeather(navController, viewModel)}
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

            } else {

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

data class City(val name: String, val description: String, val imageResource: Int)


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
