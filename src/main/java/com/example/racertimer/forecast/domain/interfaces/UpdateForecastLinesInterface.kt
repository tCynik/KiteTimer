package com.example.racertimer.forecast.domain.interfaces

import com.example.racertimer.forecast.domain.models.ForecastLine
import java.util.*

interface UpdateForecastLinesInterface {
    fun updateForecastLines(queueForecastLines: Queue<ForecastLine>?)
}