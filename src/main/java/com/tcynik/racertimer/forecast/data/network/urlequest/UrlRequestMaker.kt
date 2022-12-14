package com.tcynik.racertimer.forecast.data.network.urlequest

private const val WEBSITE_FORECAST = "http://api.openweathermap.org/data/2.5"
private const val FORECAST_ACTION = "/forecast?"

class UrlRequestMaker(private val key: String) {
    fun makeRequest(latitude: Double, longitude: Double): String {
        return WEBSITE_FORECAST +
                FORECAST_ACTION +
                "lat=" + latitude +
                "&lon=" + longitude +
                "&appid=" + key +
                "&units=metric"
    }

}