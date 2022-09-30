package com.example.racertimer.forecast.data

import com.example.racertimer.forecast.data.models.DataForecastLocation
import com.example.racertimer.forecast.domain.models.ForecastLocation

class Mappers {
    companion object{
        fun locationFromDataToDomain(dataLocation: DataForecastLocation): com.example.racertimer.forecast.domain.models.ForecastLocation {
            return ForecastLocation(
                name = dataLocation.name,
                latitude = dataLocation.latitude,
                longitude = dataLocation.longitude)
        }

    }
}