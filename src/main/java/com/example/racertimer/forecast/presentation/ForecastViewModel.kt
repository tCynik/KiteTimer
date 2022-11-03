package com.example.racertimer.forecast.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.racertimer.forecast.domain.Toaster
import com.example.racertimer.forecast.domain.models.ForecastLine
import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.use_cases.*
import com.example.racertimer.forecast.presentation.interfaces.LinesUpdater
import com.example.racertimer.forecast.presentation.interfaces.UpdatingUserLocationInterface
import java.util.*

class ForecastViewModel: ViewModel() {

    val forecastLinesLive: MutableLiveData<Queue<ForecastLine>> = MutableLiveData()
    val buttonLocationNameLive: MutableLiveData<String> = MutableLiveData()

    private val userLocationUpdater = object: UpdatingUserLocationInterface{
        override fun getUserLocation(): ForecastLocation? {
            return currentUserLocation
        }
    }
    private val linesUpdater = LinesUpdater(forecastLinesLive) //todo: как это передать в DI?

    private var runActivityUseCase = RunActivityUseCase(updaterUserLocation = userLocationUpdater) // todo: DI!!!

    private var currentUserLocation: ForecastLocation? = null
    private var currentForecastLocation: ForecastLocation? = null
    private var awaitingUserLocation = false
    private var forecastShownStatus = false

    fun updateForecastWhenActivityOpened() {
        currentForecastLocation = runActivityUseCase.execute()
        checkIsAwaitingCurrentLocation()
        // todo: нужно сделать обновление информации прогноза только если таблица не обновлена либо если с момента обновления прошло много времени
    }

    fun updateUserLocation(currentLocation: ForecastLocation) {
        currentUserLocation = currentLocation
        if (awaitingUserLocation) {
            updateForecastByLocation(currentLocation)
            awaitingUserLocation = false
        }
    }

    fun forceUpdateForecast() {
        forceUpdateForecastUseCase.execute(currentForecastLocation!!)
        checkIsAwaitingCurrentLocation()
    }

    fun updateForecastShownStatus(updateStatus: Boolean) {
        forecastShownStatus = updateStatus
    }

    fun updateForecastByLocation(forecastLocation: ForecastLocation) {
        currentForecastLocation = forecastLocation
        updateForecastUseCase.execute(forecastLocation)
        buttonLocationNameLive.value = forecastLocation.name
    }

    fun updateForecastByUserLocation() {
        if (currentUserLocation == null) awaitingUserLocation = true
        else updateForecastByLocation(currentUserLocation!!)
    }

    private fun checkIsAwaitingCurrentLocation() {
        if (currentForecastLocation == null) awaitingUserLocation = true
    }

    private fun runAutoUpdateTimeout() {
        // todo: запускаем в коурутине таймаут, после которого на локации обновлем прогноз.
        // если работает предыдущий таймаут, его отменяем и запускаем новый
    }

//    fun updateUrlResponseStatus(isForecastShown: Boolean) {
//        forecastShown = isForecastShown
//        if (isForecastShown) {
//            runAutoUpdateTimeout()
//        } else {
        // todo: значит, прогноз не отобразился (нет доступа к сети?) - тогда нужно через какое-то время пытаться снова
}