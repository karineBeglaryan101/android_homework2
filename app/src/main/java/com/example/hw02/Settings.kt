package com.example.hw02

import androidx.lifecycle.MutableLiveData

enum class TemperatureType {
    CELSIUS,
    FAHRENHEIT
}

object Settings {
    val temperatureType = MutableLiveData(TemperatureType.CELSIUS)
}