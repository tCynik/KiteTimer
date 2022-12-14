package com.tcynik.racertimer.forecast.domain.use_cases

import com.tcynik.racertimer.forecast.domain.models.ForecastLocation

class ForceUpdateForecastUseCase (private val updateForecastUseCase: UpdateForecastUseCase) {
    fun execute (lastForecastLocation: ForecastLocation) {
        updateForecastUseCase.execute(lastForecastLocation)
    }
}