package com.example.racertimer.forecast.domain.interfaces

import com.example.racertimer.forecast.domain.models.ForecastLocation

interface LastLocationInterface {
    fun save(forecastLocation: ForecastLocation): Boolean
    fun load(): String
}