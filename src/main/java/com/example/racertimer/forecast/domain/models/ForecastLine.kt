package com.example.racertimer.forecast.domain.models

data class ForecastLine (val time: Long,
                    val temperature: String,
                    val windSpeed: String,
                    val windDir: String,
                    val windGust: String) {
}