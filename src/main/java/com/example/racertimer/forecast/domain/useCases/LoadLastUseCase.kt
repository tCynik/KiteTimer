package com.example.racertimer.forecast.domain.useCases

import com.example.racertimer.forecast.domain.interfaces.LastLocationInterface
import com.example.racertimer.forecast.domain.models.ForecastLocation

class LoadLastUseCase(val lastLocationInterface: LastLocationInterface) {

    fun execute() : String {
        return lastLocationInterface.load()
    }
}