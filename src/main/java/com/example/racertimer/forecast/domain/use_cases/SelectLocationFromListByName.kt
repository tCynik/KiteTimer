package com.example.racertimer.forecast.domain.use_cases

import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.models.LocationsList

class SelectLocationFromListByName {
    fun execute (list: LocationsList, locationName: String): ForecastLocation? {
        return list[locationName]
    }
}