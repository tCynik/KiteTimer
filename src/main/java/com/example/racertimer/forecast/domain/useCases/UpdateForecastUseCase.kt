package com.example.racertimer.forecast.domain.useCases

import com.example.racertimer.forecast.data.urlequest.ForecastURLReceiver
import com.example.racertimer.forecast.domain.models.ForecastLine
import com.example.racertimer.forecast.domain.models.ForecastLocation
import java.util.*

class UpdateForecastUseCase(updateForecastLinesInterface: UpdateForecastLinesInterface) {
// TODO: make the updating interface ^ . Pass the interface into URLReciever.
// TODO: get jSon from interface and pass one to the parser to get forecastLines. Then pass the lines into UI
    fun execute(forecastLocation: ForecastLocation): Queue<ForecastLine> {
        val forecastStrings = ForecastURLReceiver().update(forecastLocation)
        return parceStringsToLines(forecastStrings)
    }


}