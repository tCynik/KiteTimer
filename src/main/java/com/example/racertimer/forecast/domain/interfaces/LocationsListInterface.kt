package com.example.racertimer.forecast.domain.interfaces

import com.example.racertimer.forecast.domain.models.LocationsList

interface LocationsListInterface {
    fun loadList(): LocationsList?

    fun saveList(locationsList: LocationsList): Boolean
}