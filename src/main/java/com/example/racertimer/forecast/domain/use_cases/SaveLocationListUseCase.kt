package com.example.racertimer.forecast.domain.use_cases

import com.example.racertimer.forecast.domain.interfaces.LocationsListRepositoryInterface
import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.models.LocationsList

class SaveLocationListUseCase(private val locationsListRepository: LocationsListRepositoryInterface) {
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
        return locationsListRepository.saveList(locationsList = locationsList)
    }
}