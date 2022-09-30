package com.example.racertimer.forecast.domain.useCases

import com.example.racertimer.forecast.domain.interfaces.LocationsListInterface
import com.example.racertimer.forecast.domain.models.LocationsList

class OpenLocationsListUseCase(val locationsListInterface: LocationsListInterface) {
    fun execute(): LocationsList? {
        return locationsListInterface.loadList()
    }
}