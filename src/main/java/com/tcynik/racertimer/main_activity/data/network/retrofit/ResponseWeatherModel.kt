package com.example.racertimer.forecast.data.network.retrofit.response

import com.google.gson.annotations.SerializedName

data class ResponseWeatherModel(
    @SerializedName("wind")
    val windApi: WindApi
)

data class WindApi(
    @SerializedName("deg")
    val windDir: Int
)