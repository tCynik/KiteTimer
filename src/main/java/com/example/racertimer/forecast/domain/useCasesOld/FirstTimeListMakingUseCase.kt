package com.example.racertimer.forecast.domain.useCasesOld

import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.models.LocationsList

class FirstTimeListMakingUseCase {

    fun execute () : LocationsList {
        var locationsList = LocationsList()
        var nextLocation = ForecastLocation("Krasnoyarsk", 56.02698, 92.94564833333334)
        locationsList.put(nextLocation.name, nextLocation)
        nextLocation = ForecastLocation("Shumiha", 55.91477, 92.27641999999999)
        locationsList.put(nextLocation.name, nextLocation)
        nextLocation = ForecastLocation("Obskoe Sea", 54.82607500000001, 83.02941833333334)
        locationsList.put(nextLocation.name, nextLocation)
        return locationsList
    }
}