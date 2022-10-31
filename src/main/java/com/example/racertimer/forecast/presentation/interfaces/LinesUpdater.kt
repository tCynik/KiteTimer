package com.example.racertimer.forecast.presentation.interfaces

import androidx.lifecycle.MutableLiveData
import com.example.racertimer.forecast.domain.models.ForecastLine
import java.util.*

class LinesUpdater(private val forecastLinesLive: MutableLiveData<Queue<ForecastLine>>): UpdateForecastLinesInterface {
    override fun updateForecastLines(queueForecastLines: Queue<ForecastLine>?) {
        if (queueForecastLines != null) forecastLinesLive.value = queueForecastLines
    }
}