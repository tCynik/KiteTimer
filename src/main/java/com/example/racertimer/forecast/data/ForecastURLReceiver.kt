package com.example.racertimer.forecast.data

import com.example.racertimer.forecast.domain.models.ForecastData
import com.example.racertimer.forecast.domain.models.ForecastLocation

class ForecastURLReceiver {

    fun update(forecastLocation: ForecastLocation): ForecastData {
        val latitude = forecastLocation.latitude
        val longitude = forecastLocation.longitude
    }
}