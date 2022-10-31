package com.example.racertimer.forecast.domain.useCases

import com.example.racertimer.forecast.data.parsers.ParserJsonToQueueLines
import com.example.racertimer.forecast.data.urlequest.ResultJsonInterface
import com.example.racertimer.forecast.data.urlequest.URLRequestManager
import com.example.racertimer.forecast.data.urlequest.UrlRequestBuilder
import com.example.racertimer.forecast.presentation.interfaces.UpdateForecastLinesInterface
import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.presentation.interfaces.LinesUpdater
import org.json.JSONObject

class UpdateForecastUseCase(private val linesUpdater: LinesUpdater,
                            private val toaster: Toaster ) {
// TODO: put the . Then pass the lines into UI
    fun execute(forecastLocation: ForecastLocation) {
        val resultInterface = object : ResultJsonInterface{
            override fun gotResult(jsonOnObject: JSONObject?) {
                if (jsonOnObject != null) {
                    val queueLines = ParserJsonToQueueLines().execute(jsonOnObject)
                    linesUpdater.updateForecastLines(queueLines)
                } else linesUpdater.updateForecastLines(null)
            }

            override fun errorOccurs(error: String) {
                toaster.makeToast(error)
            }
        }
    val requestString = UrlRequestBuilder().makeRequest(
        latitude = forecastLocation.latitude,
        longitude = forecastLocation.longitude)
    URLRequestManager(resultInterface).makeRequest(requestString)
    }


}