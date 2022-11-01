package com.example.racertimer.forecast.domain.useCasesOld

import com.example.racertimer.forecast.domain.interfaces.LastLocationNameRepositoryInterface
import com.example.racertimer.forecast.domain.models.ForecastLocation

class SaveLastUseCase(val lastLocationNameRepositoryInterface: LastLocationNameRepositoryInterface) {
    fun execute (forecastLocation: ForecastLocation) {
        lastLocationNameRepositoryInterface.save(forecastLocation)
    }
}