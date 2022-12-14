package com.tcynik.racertimer.forecast.presentation.interfaces

import com.tcynik.racertimer.forecast.domain.models.ForecastLocation

interface SelectLocationInterface {
    fun onLocationSelected(forecastLocation: ForecastLocation?)
}