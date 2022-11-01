package com.example.racertimer.forecast.presentation.interfaces

import com.example.racertimer.forecast.domain.models.ForecastLocation

interface UpdatingUserLocationInterface {
    fun getUserLocation(): ForecastLocation?
}