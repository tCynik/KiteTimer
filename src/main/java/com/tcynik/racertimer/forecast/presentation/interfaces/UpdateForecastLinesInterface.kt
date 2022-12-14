package com.tcynik.racertimer.forecast.presentation.interfaces

import com.tcynik.racertimer.forecast.domain.models.ForecastLine
import java.util.*

interface UpdateForecastLinesInterface {
    fun updateForecastLines(queueForecastLines: Queue<ForecastLine>?)
}