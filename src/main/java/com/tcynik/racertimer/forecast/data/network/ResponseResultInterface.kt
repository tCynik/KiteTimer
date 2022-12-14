package com.tcynik.racertimer.forecast.data.network

import com.tcynik.racertimer.forecast.domain.models.ForecastLine
import java.util.*

interface ResponseResultInterface {
    fun gotResult(queueForecastLines: Queue<ForecastLine>)
    fun gotError(error: String)
}