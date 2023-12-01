package com.example.hw02.viewmodel

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    private val _isLoading = MutableLiveData<Boolean>(false)
    val weatherData: LiveData<WeatherData> = _weatherData
    val currentWeatherData: LiveData<WeatherData> = _currentWeatherData
    val searchWeatherData: LiveData<WeatherData> = _searchWeatherData
    val isloading: LiveData<Boolean> = _isLoading
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
                        _currentWeatherData.postValue(null)
                    }
                }
            } else {
                _currentWeatherData.postValue(null)
            }
        } catch (e: Exception) {
            _currentWeatherData.postValue(null)
        }
    }


    fun fetchWeatherData(city: City) {
        viewModelScope.launch {
            try {
                val apiKey = getApiKey()
                val weather = weatherApiService.getWeather(city.name, apiKey)
                _weatherData.postValue(weather)
            } catch (e: Exception) {
                _weatherData.postValue(null)
            }
        }
    }

    fun searchWeatherData(cityName: String) {
        _isLoading.postValue(true)
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
                _searchWeatherData.postValue(null)
            }
            finally {
                _isLoading.postValue(false)
            }
        }
    }

    private fun getApiKey(): String {
        return "8e7826a07c1644d280d82050231311"
    }
}
