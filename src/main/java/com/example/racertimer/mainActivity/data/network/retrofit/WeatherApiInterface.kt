package com.example.racertimer.mainActivity.data.network.retrofit

import com.example.racertimer.forecast.data.network.retrofit.response.ResponseForecastModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiInterface {
    @GET("./data/2.5/weather")
    fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") key: String,
        @Query("units") units: String = "metric" ): Call<ResponseForecastModel>
}

//https://api.openweathermap.org/data/2.5/weather
//                                                  ?lat={lat}&lon={lon}&appid={API key}

// TODO: вопрос: создавать интерфесы под кажды отдельный запрос, или один ионетрфейс с разными методами