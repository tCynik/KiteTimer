package com.example.racertimer.forecast.domain.use_cases

import android.util.Log
import com.example.racertimer.forecast.data.LocationSelectorByNameImpl
import com.example.racertimer.forecast.domain.interfaces.LastLocationNameRepositoryInterface
import com.example.racertimer.forecast.domain.interfaces.LocationsListRepositoryInterface
import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.presentation.interfaces.UpdatingUserLocationInterface

private const val CURRENT_POSITION = "current position"//"DEFAULT"

class RestoreLastSessionLocationUseCase(
    private val lastLocationNameRepository: LastLocationNameRepositoryInterface,
    locationsListRepository: LocationsListRepositoryInterface,
    private val updaterUserLocation: UpdatingUserLocationInterface,
) {
    private val locationsSelectorByNameFromList = LocationSelectorByNameImpl(locationsListRepository)

    fun execute() : ForecastLocation? {
        var forecastLocation: ForecastLocation?
        val lastLocationName: String? = lastLocationNameRepository.load()

        forecastLocation = if (lastLocationName == null || lastLocationName == CURRENT_POSITION) {
            updaterUserLocation.getUserLocation()
        } else {
            locationsSelectorByNameFromList.select(lastLocationName)
        }
        // TODO: добавить обработку налла с выводом во флаг awaiting
        return forecastLocation
    }
}