package com.example.racertimer.forecast.presentation.interfaces

import androidx.lifecycle.MutableLiveData
import com.example.racertimer.forecast.domain.models.ForecastLine
import com.example.racertimer.forecast.presentation.models_mappers.ForecastLinesData
import java.util.*

class LinesUpdater(private val forecastLines: MutableLiveData<ForecastLinesData>): UpdateForecastLinesInterface {
    override fun updateForecastLines(queueForecastLines: Queue<ForecastLine>?) {
        if (queueForecastLines != null) forecastLines.value = ForecastLinesData(queueForecastLines)
    }
}