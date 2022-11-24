package com.example.racertimer.forecast.data.network

import com.example.racertimer.forecast.domain.models.ForecastLine
import java.util.*

interface ResponseResultInterface {
    fun gotResult(queueForecastLines: Queue<ForecastLine>)
    fun gotError(error: String)
}