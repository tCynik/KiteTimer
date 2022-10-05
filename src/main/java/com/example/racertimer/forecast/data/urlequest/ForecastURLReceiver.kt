package com.example.racertimer.forecast.data.urlequest

import com.example.racertimer.forecast.domain.models.ForecastLocation
import java.util.*

class ForecastURLReceiver {

    fun update(forecastLocation: ForecastLocation): Queue<String> {
        val latitude = forecastLocation.latitude
        val longitude = forecastLocation.longitude
        val urlRequestManager = URLRequestManager()
        return urlRequestManager.makeRequest(latitude = latitude, longitude = longitude)
    }
}