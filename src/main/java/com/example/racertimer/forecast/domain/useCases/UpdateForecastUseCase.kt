package com.example.racertimer.forecast.domain.useCases

import com.example.racertimer.forecast.data.parsers.ParserJsonToQueueLines
import com.example.racertimer.forecast.data.urlequest.ResultJsonInterface
import com.example.racertimer.forecast.data.urlequest.URLRequestManager
import com.example.racertimer.forecast.data.urlequest.UrlRequestBuilder
import com.example.racertimer.forecast.domain.interfaces.UpdateForecastLinesInterface
import com.example.racertimer.forecast.domain.models.ForecastLocation
import org.json.JSONObject

class UpdateForecastUseCase(val updateForecastLinesInterface: UpdateForecastLinesInterface) {
// TODO: put the . Then pass the lines into UI
    fun execute(forecastLocation: ForecastLocation) {
        val resultInterface = object : ResultJsonInterface{
            override fun gotResult(jsonOnbject: JSONObject) {
                val queueLines = ParserJsonToQueueLines().execute(jsonOnbject)
                updateForecastLinesInterface.updateForecastLines(queueLines)
            }
        }
    val requestString = UrlRequestBuilder().makeRequest(
        latitude = forecastLocation.latitude,
        longitude = forecastLocation.longitude)
    URLRequestManager(resultInterface).makeRequest(requestString)
    }


}