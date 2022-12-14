package com.tcynik.racertimer.forecast.domain.interfaces

import com.tcynik.racertimer.forecast.domain.models.LocationsList

interface LocationsListRepositoryInterface {
    fun loadList(): LocationsList?

    fun saveList(locationsList: LocationsList): Boolean
}