package com.example.racertimer.forecast.domain.models

class LocationsList: HashSet<ForecastLocation>() {
    fun findByName (name: String): ForecastLocation {
        this.iterator() // todo: make searching location by name
    }
}