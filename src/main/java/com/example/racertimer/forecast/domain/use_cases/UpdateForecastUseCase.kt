package com.example.racertimer.forecast.domain.use_cases

import com.example.racertimer.forecast.data.parsers.ParserJsonToQueueLines
import com.example.racertimer.forecast.data.urlequest.ResultJsonInterface
import com.example.racertimer.forecast.data.urlequest.URLRequestManager
import com.example.racertimer.forecast.data.urlequest.UrlRequestBuilder
import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.instruments.Toaster
import com.example.racertimer.forecast.domain.interfaces.LastLocationNameRepositoryInterface
import com.example.racertimer.forecast.presentation.interfaces.LinesUpdater
import org.json.JSONObject

class UpdateForecastUseCase(//private val linesUpdater: LinesUpdater,
                            private val toaster: Toaster,
                            private val lastLocationRepository: LastLocationNameRepositoryInterface
) {
    var linesUpdater: LinesUpdater? = null

    fun initLinesUpdater(linesUpdater: LinesUpdater) {
        this.linesUpdater = linesUpdater
    }

// TODO: инстанс LinesUpdater создается после появления VM (после появления liveData). Его нужно импортировать после создания VM
    fun execute(forecastLocation: ForecastLocation) {
        val resultInterface = object : ResultJsonInterface{
            override fun gotResult(jsonOnObject: JSONObject?) {
                if (linesUpdater != null) {
                    if (jsonOnObject != null) {
                        val queueLines = ParserJsonToQueueLines().execute(jsonOnObject)
                        linesUpdater!!.updateForecastLines(queueLines)
                    } else linesUpdater!!.updateForecastLines(null)
                } else toaster.makeToast("Error table init")
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