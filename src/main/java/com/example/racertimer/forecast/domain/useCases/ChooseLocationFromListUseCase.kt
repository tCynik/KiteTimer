package com.example.racertimer.forecast.domain.useCases

import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.models.LocationsList

class ChooseLocationFromListUseCase {
    fun execute (list: LocationsList, locationName: String): ForecastLocation? {
        return list[locationName]
    }
}