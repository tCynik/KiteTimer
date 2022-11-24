package com.example.racertimer.forecast.data.network.urlequest

import com.example.racertimer.forecast.data.network.ResponseResultInterface
import com.example.racertimer.forecast.data.network.urlequest.JavaRequest.JavaForecastManager
import com.example.racertimer.forecast.data.parsers.ParserJsonToQueueLines
import com.example.racertimer.forecast.domain.models.ForecastLocation
import org.json.JSONObject

class URLRequestManager(private val responseResult: ResponseResultInterface) {

    private val resultInterface = object : ResultJsonInterface {
        override fun gotResult(jsonOnObject: JSONObject?) {
            if (jsonOnObject != null) {
                val queueLines = ParserJsonToQueueLines().execute(jsonOnObject)
                responseResult.gotResult(queueLines)
            } else {
                responseResult.gotError("Response is empty")
            }
        }

        override fun errorOccurs(error: String) {
            responseResult.gotError(error)
        }
    }

    fun makeRequest(requestString: String) {
        JavaForecastManager(resultInterface).updateForecast(requestString)
    }
}