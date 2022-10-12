package com.example.racertimer.forecast.domain.models

import java.io.Serializable

data class ForecastLocation (val name: String, val latitude: Double, val longitude: Double): Serializable {
}