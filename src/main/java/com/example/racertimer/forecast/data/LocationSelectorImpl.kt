package com.example.racertimer.forecast.data

import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.presentation.interfaces.LocationSelectorFromListInterface

class LocationSelectorImpl(private val locationsListRepository: LocationsListRepository): LocationSelectorFromListInterface {
    override fun select(lastLocationName: String): ForecastLocation? {
        val locationsList = locationsListRepository.loadList()
        return locationsList?.get(lastLocationName)
    }
}