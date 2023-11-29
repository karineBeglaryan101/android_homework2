package com.example.hw02.viewmodel

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw02.City
import com.example.hw02.LocationProvider
import com.example.hw02.TAG
import com.example.hw02.WeatherApiService
import com.example.hw02.WeatherData
import com.example.hw02.weatherApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WeatherViewModel() : ViewModel() {
    private val _weatherData = MutableLiveData<WeatherData>()
    private val _currentWeatherData = MutableLiveData<WeatherData>()
    private val _searchWeatherData = MutableLiveData<WeatherData>()
    val weatherData: LiveData<WeatherData> get() = _weatherData
    val currentWeatherData: LiveData<WeatherData> get() = _currentWeatherData
    val searchWeatherData: LiveData<WeatherData> get() = _searchWeatherData
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

    fun searchWeatherData(cityName: String) {
        Log.e(TAG, "fetchWeatherData")
        viewModelScope.launch {
            try {
                val apiKey = getApiKey()
                val weather = weatherApiService.getWeather(cityName, apiKey)
                if (weather != null) {
                    _searchWeatherData.postValue(weather)
                } else {
                    _searchWeatherData.postValue(null)
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    private fun getApiKey(): String {
        return "8e7826a07c1644d280d82050231311"
    }
}
