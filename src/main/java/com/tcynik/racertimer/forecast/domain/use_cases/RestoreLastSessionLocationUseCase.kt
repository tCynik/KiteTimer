package com.tcynik.racertimer.forecast.domain.use_cases

import com.tcynik.racertimer.forecast.domain.interfaces.LastLocationNameRepositoryInterface
import com.tcynik.racertimer.forecast.domain.interfaces.LocationsListRepositoryInterface
import com.tcynik.racertimer.forecast.domain.models.ForecastLocation
import com.tcynik.racertimer.forecast.presentation.interfaces.UpdatingUserLocationInterface

private const val CURRENT_POSITION = "current position"//"DEFAULT"

class RestoreLastSessionLocationUseCase(
    private val lastLocationNameRepository: LastLocationNameRepositoryInterface,
    locationsListRepository: LocationsListRepositoryInterface,
    private val updaterUserLocation: UpdatingUserLocationInterface,
) {
    private val locationsSelectorByNameFromList =
        com.tcynik.racertimer.forecast.data.LocationSelectorByNameImpl(locationsListRepository)

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