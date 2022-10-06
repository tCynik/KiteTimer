package com.example.racertimer.forecast.data.urlequest

import com.example.racertimer.forecast.data.urlequest.JavaRequest.JavaForecastManager

class URLRequestManager(private val resultJsonInterface: ResultJsonInterface) {

    fun makeRequest(requestString: String) {
        //TODO: make Retrofit request here
        JavaForecastManager(resultJsonInterface).updateForecast(requestString)
    }
}