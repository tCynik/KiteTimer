package com.tcynik.racertimer.forecast.presentation.models_mappers

import android.location.Location
import com.tcynik.racertimer.forecast.domain.models.ForecastLocation

private const val CURRENT_POSITION = "Current"

class LocationMapper {
    companion object {
        fun androidLocationToForecastLocation(location: Location): ForecastLocation {
            return ForecastLocation(CURRENT_POSITION, latitude = location.latitude, longitude = location.longitude)
        }
    }
}