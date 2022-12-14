package com.tcynik.racertimer.forecast.domain.interfaces

import com.tcynik.racertimer.forecast.domain.models.ForecastLocation

interface LastLocationNameRepositoryInterface {
    fun save(forecastLocation: ForecastLocation): Boolean
    fun load(): String
}