package com.example.racertimer.forecast.data.urlequest

import com.example.racertimer.forecast.data.urlequest.JavaRequest.JavaForecastManager
import org.json.JSONObject

class URLRequestManager {
    init {

    }

    fun makeRequest(latitude: Double, longitude: Double) {
        val resultJsonInterface = object : ResultJsonInterface {
            override fun gotResult(jsonOnbject: JSONObject): JSONObject {
                return jsonOnbject
            }
        }
        val javaForecastManager = JavaForecastManager(resultJsonInterface)
        javaForecastManager.updateForecast(latitude, longitude)
    }
}