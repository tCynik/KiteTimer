package com.example.racertimer.forecast.domain.use_cases

import android.content.Context
import com.example.racertimer.forecast.domain.interfaces.LocationsListInterface
import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.models.LocationsList

class SaveLocationListUseCase(val context: Context, val locationsListInterface: LocationsListInterface) {
    private var locationsList = LocationsList()

    fun setLocationsList (locationsList: LocationsList) {
        this.locationsList = locationsList
    }

    fun addLocation(forecastLocation: ForecastLocation): Boolean {
        val name = forecastLocation.name
        if (locationsList.containsKey(name)) {
            return false
        } else {
            locationsList.put(name, forecastLocation)
            return true
        }
    }

    fun removeLocation(forecastLocation: ForecastLocation) {
        locationsList.remove(forecastLocation.name)
    }

    fun save(): Boolean {
        return locationsListInterface.saveList(locationsList = locationsList)
    }
}