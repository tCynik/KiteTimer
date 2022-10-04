package com.example.racertimer.forecast.data.urlequest

import com.example.racertimer.forecast.domain.models.ForecastData
import com.example.racertimer.forecast.domain.models.ForecastLocation
import java.util.*

class ForecastURLReceiver {

    fun update(forecastLocation: ForecastLocation): ForecastData {
        val latitude = forecastLocation.latitude
        val longitude = forecastLocation.longitude
        val urlRequestManager = URLRequestManager()
        val forecastLines: Queue<String> = urlRequestManager.makeRequest(latitude = latitude, longitude = longitude)
    }
}