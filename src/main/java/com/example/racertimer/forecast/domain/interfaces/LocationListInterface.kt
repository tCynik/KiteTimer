package com.example.racertimer.forecast.domain.interfaces

import com.example.racertimer.forecast.domain.models.LocationsList

interface LocationListInterface {
    fun loadList(): LocationsList?

    fun saveList(locationsList: LocationsList)
}