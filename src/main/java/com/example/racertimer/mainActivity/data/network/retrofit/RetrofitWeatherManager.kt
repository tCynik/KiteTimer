package com.example.racertimer.mainActivity.data.network.retrofit

import com.example.racertimer.forecast.data.network.retrofit.response.ResponseWeatherModel
import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.mainActivity.data.network.WeatherResultInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RetrofitWeatherManager(resultCatcher: WeatherResultInterface, private val key: String) {
    private val weatherCallback = object: Callback<ResponseWeatherModel> {
        override fun onResponse(
            call: Call<ResponseWeatherModel>,
            response: Response<ResponseWeatherModel>
        ) {
            val responseBody: ResponseWeatherModel? = response.body()
            if (responseBody!=null) {
                val windDir = responseBody.windApi.windDir
                resultCatcher.gotResult(windDir)
            }
        }

        override fun onFailure(call: Call<ResponseWeatherModel>, t: Throwable) {
            resultCatcher.gorError(t.toString())
            TODO("в активити оставляем оповещение Set Wind Dir")
        }
    }

    fun makeWeatherRequest(weatherApi: WeatherApiInterface, forecastLocation: ForecastLocation){

    }

}