package com.tcynik.racertimer.forecast.presentation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tcynik.racertimer.forecast.domain.instruments.LocationsListOpener
import com.tcynik.racertimer.forecast.domain.interfaces.LastLocationNameRepositoryInterface
import com.tcynik.racertimer.forecast.domain.interfaces.LocationsListRepositoryInterface
import com.tcynik.racertimer.forecast.domain.models.ForecastLocation
import com.tcynik.racertimer.forecast.domain.models.LocationsList
import com.tcynik.racertimer.forecast.domain.use_cases.ForceUpdateForecastUseCase
import com.tcynik.racertimer.forecast.domain.use_cases.RestoreLastSessionLocationUseCase
import com.tcynik.racertimer.forecast.domain.use_cases.SaveLocationListUseCase
import com.tcynik.racertimer.forecast.domain.use_cases.UpdateForecastUseCase
import com.tcynik.racertimer.forecast.presentation.interfaces.LinesUpdater
import com.tcynik.racertimer.forecast.presentation.interfaces.UpdatingUserLocationInterface
import com.tcynik.racertimer.forecast.presentation.models_mappers.ForecastLinesData

class ForecastViewModel(lastLocationNameRepository: LastLocationNameRepositoryInterface,
                        locationsListRepository: LocationsListRepositoryInterface,
                        private val updateForecastUseCase: UpdateForecastUseCase
)
    : ViewModel() {

    private val forceUpdateForecastUseCase = ForceUpdateForecastUseCase(updateForecastUseCase)
    private val locationsListOpener = LocationsListOpener(locationsListRepository)

    val forecastLinesLive: MutableLiveData<ForecastLinesData> = MutableLiveData()
    private val linesUpdater = LinesUpdater(forecastLinesLive)

    private val userLocationUpdater = object: UpdatingUserLocationInterface {
        override fun getUserLocation(): ForecastLocation? {
            return currentUserLocation
        }
    }

    private val restoreLastSessionLocationUseCase = RestoreLastSessionLocationUseCase(
        lastLocationNameRepository = lastLocationNameRepository,
        locationsListRepository = locationsListRepository,
        updaterUserLocation = userLocationUpdater)

    private val saveLocationListUseCase = SaveLocationListUseCase(locationsListRepository)

    private var currentUserLocation: ForecastLocation? = null
    private var currentForecastLocation: ForecastLocation? = null
    private var awaitingUserLocation = false
    private var forecastShownStatus = false
    var locationNameLive: MutableLiveData<String> = MutableLiveData()

    init {
        updateForecastUseCase.initLinesUpdater(linesUpdater)
        updateForecastWhenActivityOpened()
        if (currentForecastLocation == null) locationNameLive.value = "Select location"
    }

    private fun updateForecastWhenActivityOpened() {
        currentUserLocation = restoreLastSessionLocationUseCase.execute()
        if (!checkIsAwaitingCurrentLocation()) updateForecastByLocation(currentUserLocation!!)
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

    fun updateForecastByLocation(forecastLocation: ForecastLocation) {
        setCurrentForecastLocation(forecastLocation)
        updateForecastUseCase.execute(currentForecastLocation!!)
    }

    private fun setCurrentForecastLocation(forecastLocation: ForecastLocation) {
        currentForecastLocation = forecastLocation
        locationNameLive.value = forecastLocation.name
    }

    private fun checkIsAwaitingCurrentLocation(): Boolean {
        if (currentUserLocation == null) {
            awaitingUserLocation = true
        }
        return awaitingUserLocation
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