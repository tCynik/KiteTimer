package com.example.racertimer.forecast.domain.use_cases

import android.util.Log
import com.example.racertimer.forecast.data.network.ResponseResultInterface
import com.example.racertimer.forecast.data.network.retrofit.request.ForecastApiInterface
import com.example.racertimer.forecast.data.network.retrofit.RequestType
import com.example.racertimer.forecast.data.network.retrofit.ForecastRetrofitCreator
import com.example.racertimer.forecast.data.network.retrofit.RetrofitForecastManager
import com.example.racertimer.forecast.data.network.urlequest.URLRequestManager
import com.example.racertimer.forecast.data.network.urlequest.UrlRequestMaker
import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.interfaces.LastLocationNameRepositoryInterface
import com.example.racertimer.forecast.domain.models.ForecastLine
import com.example.racertimer.forecast.presentation.interfaces.LinesUpdater
import com.example.racertimer.forecast.presentation.interfaces.ToasterInterface
import java.util.*

// этот класс тупо определяет способ запроса - ЮРЛ или ретрофит
// под каждый тип запроса - отдельный манагер, который уже работает с внутренними класами
private const val BASE_URL = "https://api.openweathermap.org"
private const val WEBSITE_KEY = "fc35b8ee90f4ee45109149cc13ee7a4f"
private val requestType = RequestType.RETROFIT

class UpdateForecastUseCase(private val toaster: ToasterInterface,
                            private val lastLocationRepository: LastLocationNameRepositoryInterface
) {
    private var forecastApi: ForecastApiInterface = ForecastRetrofitCreator().createForecastRetrofit(BASE_URL)

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

    fun execute(forecastLocation: ForecastLocation) {
        if (requestType == RequestType.URL) { // запрос через URL с ручным парсингом ответа
            toaster.makeToast("Updating forecast for location ${forecastLocation.name}")

            val requestString = UrlRequestMaker(WEBSITE_KEY).makeRequest(
                latitude = forecastLocation.latitude,
                longitude = forecastLocation.longitude)
            URLRequestManager(responseResult).makeRequest(requestString)
            lastLocationRepository.save(forecastLocation)
        } else { // запрос через Retrofit
            RetrofitForecastManager(responseResult, WEBSITE_KEY).makeForecastRequest(forecastApi, forecastLocation)
            lastLocationRepository.save(forecastLocation)
            toaster.makeToast("Updating forecast. Location is: ${forecastLocation.name}")
        }
    }
}