package com.tcynik.racertimer.forecast.domain.instruments

import android.util.Log
import com.tcynik.racertimer.forecast.domain.interfaces.LocationsListRepositoryInterface
import com.tcynik.racertimer.forecast.domain.models.LocationsList

class LocationsListOpener (private val locationsListRepositoryInterface: LocationsListRepositoryInterface) {
    fun execute(): LocationsList? {
        Log.i("racer_timer_locationsListOpener", "OpenLocationsUseCase: List size = " +
                "${(locationsListRepositoryInterface.loadList()?.size)}")
        return locationsListRepositoryInterface.loadList()
    }
}