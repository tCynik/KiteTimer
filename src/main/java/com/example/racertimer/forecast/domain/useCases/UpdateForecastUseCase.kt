package com.example.racertimer.forecast.domain.useCases

import com.example.racertimer.forecast.data.urlequest.ForecastURLReceiver
import com.example.racertimer.forecast.domain.models.ForecastLine
import com.example.racertimer.forecast.domain.models.ForecastLocation
import java.util.*

class UpdateForecastUseCase() {

    fun execute(forecastLocation: ForecastLocation): Queue<ForecastLine> {
        val forecastStrings = ForecastURLReceiver().update(forecastLocation)
        return parceStringsToLines(forecastStrings)
    }


}