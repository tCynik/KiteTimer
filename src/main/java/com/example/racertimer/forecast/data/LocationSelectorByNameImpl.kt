package com.example.racertimer.forecast.data

import com.example.racertimer.forecast.domain.interfaces.LocationsListRepositoryInterface
import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.presentation.interfaces.LocationSelectorByNameInterface

class LocationSelectorByNameImpl(private val locationsListRepository: LocationsListRepositoryInterface): LocationSelectorByNameInterface {
    override fun select(lastLocationName: String): ForecastLocation? {
        val locationsList = locationsListRepository.loadList()
        return locationsList?.get(lastLocationName)
    }
}