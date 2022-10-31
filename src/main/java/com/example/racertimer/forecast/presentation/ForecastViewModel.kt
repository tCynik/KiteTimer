package com.example.racertimer.forecast.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.racertimer.forecast.presentation.interfaces.UpdateForecastLinesInterface
import com.example.racertimer.forecast.domain.models.ForecastLine
import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.models.LocationsList
import com.example.racertimer.forecast.domain.useCases.*
import java.util.*

class ForecastViewModel(
    private val chooseLocationByNameUseCase: SelectLocationFromListByName,
    private val loadLastUseCase: LoadLastUseCase,
    private val openLocationsListUseCase: OpenLocationsListUseCase,
    private val saveLastUseCase: SaveLastUseCase,
    private val saveLocationListUseCase: SaveLocationListUseCase,
    private val updateDataErrorUseCase: UpdateDataErrorUseCase,
    //private val updateForecastUseCase: UpdateForecastUseCase
    ): ViewModel() {

    private val updateForecastLinesInterface = object: UpdateForecastLinesInterface {
        override fun updateForecastLines(queueForecastLines: Queue<ForecastLine>?) {
            forecastLinesLive.value = queueForecastLines
        }
    }
//    private val updateForecastUseCase = UpdateForecastUseCase(updateForecastLinesInterface) // для тестирования инжектим
//
//    private val forecastStatusManager = ForecastStatusManager(updateForecastUseCase)

    var locationsListLive: MutableLiveData<LocationsList> = MutableLiveData()
    val forecastLinesLive: MutableLiveData<Queue<ForecastLine>> = MutableLiveData()
    val buttonLocationNameLive: MutableLiveData<String> = MutableLiveData()
    var currentUserLocation: ForecastLocation? = null

    fun updateForecastWhenActivityOpened() {
        // todo: нужно сделать обновление информации прогноза только если таблица не обновлена либо если с момента обновления прошло много времени
        // оригинальная сигнатура:
//        val forecastLocation = lastForecastLocation()
//        if (forecastLocation == null) {
//            locationUpdateAwaiting = true
//        }
//        else {
//            Log.i("bugfix", "ActivityForecast: current forecast location = ${forecastLocation.name}, lat = ${forecastLocation.latitude} ")
//            forecastStatusManager.updateLocation(forecastLocation)
//            btn_select_location.text = forecastLocation.name
//        }

    }

    fun setUserLocation(currentLocation: ForecastLocation) {
        currentUserLocation = currentLocation
    }

    fun selectLocationClicked() {
//        val locationsList = openLocationsListUseCase.execute()
//        if (locationsList != null)
//            selectLocationPopupUseCase.execute(buttonSelectLocation, locationsList)
    }
    // todo: liveFields to be created:
    //
//  next
}