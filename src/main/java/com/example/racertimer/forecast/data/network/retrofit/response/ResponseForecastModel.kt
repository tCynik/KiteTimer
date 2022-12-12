package com.example.racertimer.forecast.data.network.retrofit.response

import com.google.gson.annotations.SerializedName

data class ResponseForecastModel(
    @SerializedName("list")
    val items: List<TimeHourForecast>)

data class TimeHourForecast (
    @SerializedName("dt")
    val daytime: Long, // field "dt"

    @SerializedName("main")
    val forecastMain: ForecastApiMain,

    @SerializedName("wind")
    val forecastWind: ForecastApiWind
)

data class ForecastApiMain(
    @SerializedName("temp")
    val temp: Double
)

data class ForecastApiWind(
    @SerializedName("speed")
    val windSpeed: Double,

    @SerializedName("gust")
    val windGust: Double,

    @SerializedName("deg")
    val windDir: Int
)