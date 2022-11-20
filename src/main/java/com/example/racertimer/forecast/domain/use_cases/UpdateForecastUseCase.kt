package com.example.racertimer.forecast.domain.use_cases

import com.example.racertimer.forecast.data.parsers.ParserJsonToQueueLines
import com.example.racertimer.forecast.data.urlequest.ResultJsonInterface
import com.example.racertimer.forecast.data.urlequest.URLRequestManager
import com.example.racertimer.forecast.data.urlequest.UrlRequestBuilder
import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.interfaces.LastLocationNameRepositoryInterface
import com.example.racertimer.forecast.presentation.interfaces.LinesUpdater
import com.example.racertimer.forecast.presentation.interfaces.ToasterInterface
import org.json.JSONObject

class UpdateForecastUseCase(private val toaster: ToasterInterface,
                            private val lastLocationRepository: LastLocationNameRepositoryInterface
) {
    var linesUpdater: LinesUpdater? = null

    fun initLinesUpdater(linesUpdater: LinesUpdater) {
        this.linesUpdater = linesUpdater
    }

    fun execute(forecastLocation: ForecastLocation) {
        toaster.makeToast("updating forecast for location ${forecastLocation.name}")
        val resultInterface = object : ResultJsonInterface{
            override fun gotResult(jsonOnObject: JSONObject?) {
                if (linesUpdater != null) {
                    if (jsonOnObject != null) {
                        val queueLines = ParserJsonToQueueLines().execute(jsonOnObject)
                        linesUpdater!!.updateForecastLines(queueLines)
                    } else {
                        linesUpdater!!.updateForecastLines(null)
                    }
                } else toaster.makeToast("lines updater in null = ${linesUpdater == null}")
            }

            override fun errorOccurs(error: String) {
                toaster.makeToast(error)
            }
        }
    val requestString = UrlRequestBuilder().makeRequest(
        latitude = forecastLocation.latitude,
        longitude = forecastLocation.longitude)
    URLRequestManager(resultInterface).makeRequest(requestString)
    lastLocationRepository.save(forecastLocation)
    }
}