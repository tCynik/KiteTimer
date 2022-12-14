package com.tcynik.racertimer.main_activity.data.network

import android.location.Location
import com.tcynik.racertimer.forecast.domain.models.ForecastLocation
import com.tcynik.racertimer.main_activity.data.network.retrofit.RetrofitWeatherManager
import com.tcynik.racertimer.main_activity.data.network.retrofit.WeatherApiInterface

private const val WEBSITE_KEY = "fc35b8ee90f4ee45109149cc13ee7a4f"
private const val BASE_URL = "https://api.openweathermap.org"

class WindDirRequestUseCase (private val resultCatcher: WeatherResultInterface) {
    private val weatherRequest: WeatherApiInterface = com.tcynik.racertimer.forecast.data.network.retrofit.ForecastRetrofitCreator()
        .createWeatherRetrofit(BASE_URL)

    fun execute(location: Location) {// использовать локацию для запроса
        val forecastLocation = ForecastLocation(
            longitude = location.longitude,
            latitude = location.latitude,
            name = "")

        RetrofitWeatherManager(resultCatcher, WEBSITE_KEY).makeWeatherRequest(weatherRequest, forecastLocation)
    }
}