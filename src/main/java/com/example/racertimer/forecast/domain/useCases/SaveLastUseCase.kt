package com.example.racertimer.forecast.domain.useCases

import com.example.racertimer.forecast.domain.interfaces.LastLocationInterface
import com.example.racertimer.forecast.domain.models.ForecastLocation

class SaveLastUseCase(val lastLocationInterface: LastLocationInterface) {
    fun execute (forecastLocation: ForecastLocation) {
        lastLocationInterface.save(forecastLocation)
    }
}