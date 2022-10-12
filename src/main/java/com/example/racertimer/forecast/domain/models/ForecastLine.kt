package com.example.racertimer.forecast.domain.models

data class ForecastLine (val time: String,
                    val temperature: String,
                    val windSpeed: String,
                    val windDir: String,
                    val windGust: String) {
}