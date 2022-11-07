package com.example.racertimer.forecast.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.racertimer.forecast.data.LocationSelectorByNameImpl
import com.example.racertimer.forecast.domain.instruments.LocationsListOpener
import com.example.racertimer.forecast.domain.interfaces.LastLocationNameRepositoryInterface
import com.example.racertimer.forecast.domain.interfaces.LocationsListRepositoryInterface
import com.example.racertimer.forecast.domain.models.ForecastLine
import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.models.LocationsList
import com.example.racertimer.forecast.domain.use_cases.*
import com.example.racertimer.forecast.presentation.interfaces.LocationSelectorByNameInterface
import com.example.racertimer.forecast.presentation.interfaces.UpdatingUserLocationInterface
import java.util.*

class ForecastViewModel(private val lastLocationNameRepository: LastLocationNameRepositoryInterface,
                        private val locationsListRepository: LocationsListRepositoryInterface,
                        private val forceUpdateForecastUseCase: ForceUpdateForecastUseCase): ViewModel() {

    private val locationsListOpener = LocationsListOpener(locationsListRepository)
    private val locationsSelectorByNameFromList = LocationSelectorByNameImpl(locationsListRepository)

    val forecastLinesLive: MutableLiveData<Queue<ForecastLine>> = MutableLiveData()
    val buttonLocationNameLive: MutableLiveData<String> = MutableLiveData()

    private val userLocationUpdater = object: UpdatingUserLocationInterface{
        override fun getUserLocation(): ForecastLocation? {
            return currentUserLocation
        }
    }

    private val restoreLastSessionLocationUseCase = RestoreLastSessionLocationUseCase(
        lastLocationNameRepository = lastLocationNameRepository,
        locationsSelectorByNameFromList = locationsSelectorByNameFromList,
        updaterUserLocation = userLocationUpdater,
        forceUpdateForecastUseCase = forceUpdateForecastUseCase)

    private var currentUserLocation: ForecastLocation? = null
    private var currentForecastLocation: ForecastLocation? = null
    private var awaitingUserLocation = false
    private var forecastShownStatus = false

    init {
        updateForecastWhenActivityOpened()
    }

    private fun updateForecastWhenActivityOpened() {
        currentForecastLocation = restoreLastSessionLocationUseCase.execute()
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

    fun getLocationsList(): LocationsList? {
        return locationsListOpener.execute()
    }

    fun updateForecastByUserLocation() {
        if (currentUserLocation == null) awaitingUserLocation = true
        else updateForecastByLocation(currentUserLocation!!)
    }

    private fun updateForecastByLocation(forecastLocation: ForecastLocation) {
        currentForecastLocation = forecastLocation
        forceUpdateForecastUseCase.execute(forecastLocation)
        buttonLocationNameLive.value = forecastLocation.name
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