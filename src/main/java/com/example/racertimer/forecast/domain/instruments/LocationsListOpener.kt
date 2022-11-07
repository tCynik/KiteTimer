package com.example.racertimer.forecast.domain.instruments

import android.util.Log
import com.example.racertimer.forecast.domain.interfaces.LocationsListRepositoryInterface
import com.example.racertimer.forecast.domain.models.LocationsList

class LocationsListOpener (private val locationsListRepositoryInterface: LocationsListRepositoryInterface) {
    fun execute(): LocationsList? {
        Log.i("bugfix", "OpenLocationsUseCase: List size = " +
                "${(locationsListRepositoryInterface.loadList()?.size)}")
        return locationsListRepositoryInterface.loadList()
    }
}