package com.example.racertimer.forecast.domain.interfaces

import com.example.racertimer.forecast.domain.models.ForecastLocation

interface LastLocationNameRepositoryInterface {
    fun save(forecastLocation: ForecastLocation): Boolean
    fun load(): String
}