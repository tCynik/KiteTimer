package com.example.racertimer.forecast.domain.useCases

import android.util.Log
import com.example.racertimer.forecast.domain.interfaces.LocationsListInterface
import com.example.racertimer.forecast.domain.models.LocationsList

class OpenLocationsListUseCase(private val locationsListInterface: LocationsListInterface) {
    fun execute(): LocationsList? {
        Log.i("bugfix", "OpenLocationsUseCase: List size = " +
                "${(locationsListInterface.loadList()?.size)}")
        return locationsListInterface.loadList()
    }
}