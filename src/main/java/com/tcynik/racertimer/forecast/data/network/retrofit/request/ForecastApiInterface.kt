package com.tcynik.racertimer.forecast.data.network.retrofit.request

import com.tcynik.racertimer.forecast.data.network.retrofit.response.ResponseForecastModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastApiInterface {
    @GET("./data/2.5/forecast")
    fun getDailyForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") key: String,
        @Query("units") units: String = "metric" ): Call<ResponseForecastModel>
}