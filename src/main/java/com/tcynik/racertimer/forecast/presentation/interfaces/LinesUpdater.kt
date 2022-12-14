package com.tcynik.racertimer.forecast.presentation.interfaces

import androidx.lifecycle.MutableLiveData
import com.tcynik.racertimer.forecast.domain.models.ForecastLine
import com.tcynik.racertimer.forecast.presentation.models_mappers.ForecastLinesData
import java.util.*

class LinesUpdater(private val forecastLines: MutableLiveData<ForecastLinesData>): UpdateForecastLinesInterface {
    override fun updateForecastLines(queueForecastLines: Queue<ForecastLine>?) {
        if (queueForecastLines != null) forecastLines.value = ForecastLinesData(queueForecastLines)
    }
}