package com.example.racertimer.forecast.domain.use_cases

import com.example.racertimer.forecast.domain.interfaces.LastLocationNameRepositoryInterface
import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.use_cases.ForceUpdateForecastUseCase
import com.example.racertimer.forecast.presentation.interfaces.LocationSelectorFromListInterface
import com.example.racertimer.forecast.presentation.interfaces.UpdatingUserLocationInterface

private const val CURRENT_POSITION = "current position"//"DEFAULT"

class RunActivityUseCase(
    private val lastLocationNameRepository: LastLocationNameRepositoryInterface,
    private val locationsSelectorFromList: LocationSelectorFromListInterface,
    private val updaterUserLocation: UpdatingUserLocationInterface,
    private val updateForecastUseCase: ForceUpdateForecastUseCase
) {

    fun execute() : ForecastLocation? {
        var forecastLocation: ForecastLocation?
        val lastLocationName: String? = lastLocationNameRepository.load()
        forecastLocation = if (lastLocationName == null || lastLocationName == CURRENT_POSITION) {
            updaterUserLocation.getUserLocation()
        } else {
            locationsSelectorFromList.select(lastLocationName)
        }
        // TODO: добавить обработку налла с выводом во флаг awaiting
        updateForecastUseCase.execute(forecastLocation!!)
        return forecastLocation
    }
}