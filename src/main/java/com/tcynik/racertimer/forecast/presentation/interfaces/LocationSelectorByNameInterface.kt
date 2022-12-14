package com.tcynik.racertimer.forecast.presentation.interfaces

import com.tcynik.racertimer.forecast.domain.models.ForecastLocation

interface LocationSelectorByNameInterface {
    fun select(lastLocationName: String): ForecastLocation?
}