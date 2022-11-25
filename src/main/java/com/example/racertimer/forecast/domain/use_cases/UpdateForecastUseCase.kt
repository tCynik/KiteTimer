package com.example.racertimer.forecast.domain.use_cases

import com.example.racertimer.forecast.data.network.ResponseResultInterface
import com.example.racertimer.forecast.data.network.retrofit.RetrofitManager
import com.example.racertimer.forecast.data.parsers.ParserJsonToQueueLines
import com.example.racertimer.forecast.data.network.urlequest.ResultJsonInterface
import com.example.racertimer.forecast.data.network.urlequest.URLRequestManager
import com.example.racertimer.forecast.data.network.urlequest.UrlRequestBuilderMaker
import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.interfaces.LastLocationNameRepositoryInterface
import com.example.racertimer.forecast.domain.models.ForecastLine
import com.example.racertimer.forecast.presentation.interfaces.LinesUpdater
import com.example.racertimer.forecast.presentation.interfaces.ToasterInterface
import org.json.JSONObject
import java.util.*

// этот класс тупо определяет способ запроса - ЮРЛ или ретрофит
// под каждый тип запроса - отдельный манагер, который уже работает с внутренними класами
class UpdateForecastUseCase(private val toaster: ToasterInterface,
                            private val lastLocationRepository: LastLocationNameRepositoryInterface
) {
    var linesUpdater: LinesUpdater? = null
    private val responseResult = object: ResponseResultInterface {
        override fun gotResult(queueForecastLines: Queue<ForecastLine>) {
            if (linesUpdater != null) linesUpdater!!.updateForecastLines(queueForecastLines)
        }

        override fun gotError(error: String) {
            linesUpdater!!.updateForecastLines(null)
            toaster.makeToast(error)
        }
    }

    fun initLinesUpdater(linesUpdater: LinesUpdater) { // queueForecastLines: Queue<ForecastLine>?
        this.linesUpdater = linesUpdater
    }

    fun executeByURL(forecastLocation: ForecastLocation) {
        toaster.makeToast("updating forecast for location ${forecastLocation.name}")

        val requestString = UrlRequestBuilderMaker().makeRequest(
            latitude = forecastLocation.latitude,
            longitude = forecastLocation.longitude)
        URLRequestManager(responseResult).makeRequest(requestString)
        lastLocationRepository.save(forecastLocation)
    }

    fun executeByRetrofit(forecastLocation: ForecastLocation) {
        RetrofitManager(responseResult).makeRequest(forecastLocation)
        lastLocationRepository.save(forecastLocation)
    }
}