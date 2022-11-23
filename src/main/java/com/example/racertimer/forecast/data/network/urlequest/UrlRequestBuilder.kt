package com.example.racertimer.forecast.data.network.urlequest

private const val WEBSITE_FORECAST = "http://api.openweathermap.org/data/2.5"
private const val FORECAST_ACTION = "/forecast?"
private const val WEBSITE_KEY = "fc35b8ee90f4ee45109149cc13ee7a4f"

class UrlRequestBuilder {
    fun makeRequest(latitude: Double, longitude: Double): String {
        return WEBSITE_FORECAST +
                FORECAST_ACTION +
                "lat=" + latitude +
                "&lon=" + longitude +
                "&appid=" + WEBSITE_KEY +
                "&units=metric"
    }

}