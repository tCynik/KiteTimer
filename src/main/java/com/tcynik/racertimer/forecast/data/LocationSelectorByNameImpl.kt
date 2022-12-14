package com.tcynik.racertimer.forecast.data

import com.tcynik.racertimer.forecast.domain.interfaces.LocationsListRepositoryInterface
import com.tcynik.racertimer.forecast.domain.models.ForecastLocation
import com.tcynik.racertimer.forecast.presentation.interfaces.LocationSelectorByNameInterface

class LocationSelectorByNameImpl(private val locationsListRepository: LocationsListRepositoryInterface):
    LocationSelectorByNameInterface {
    override fun select(lastLocationName: String): ForecastLocation? {
        val locationsList = locationsListRepository.loadList()
        return locationsList?.get(lastLocationName)
    }
}