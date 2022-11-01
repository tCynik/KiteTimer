package com.example.racertimer.forecast.domain.use_cases

import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.useCasesOld.UpdateForecastUseCase

class ForceUpdateForecastUseCase (private val updateForecastUseCase: UpdateForecastUseCase) {
    fun execute (lastForecastLocation: ForecastLocation) {
        updateForecastUseCase.execute(lastForecastLocation)
    }
}