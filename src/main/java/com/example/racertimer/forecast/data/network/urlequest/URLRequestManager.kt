package com.example.racertimer.forecast.data.network.urlequest

import com.example.racertimer.forecast.data.network.urlequest.JavaRequest.JavaForecastManager

class URLRequestManager(private val resultJsonInterface: ResultJsonInterface) {

    fun makeRequest(requestString: String) {
        //TODO: make Retrofit request here
        JavaForecastManager(resultJsonInterface).updateForecast(requestString)
    }
}