package com.example.racertimer.forecast.data.network.retrofit

import android.util.Log
import com.example.racertimer.forecast.data.network.ResponseResultInterface
import com.example.racertimer.forecast.domain.models.ForecastLocation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RetrofitManager(val responseResultInterface: ResponseResultInterface) {
    private val callback = object: Callback<ResponseForecastModel> {
        override fun onResponse(
            call: Call<ResponseForecastModel>,
            response: Response<ResponseForecastModel>
        ) {
            val responseBody: ResponseForecastModel? = response.body()
            val linesList: List<TimeHourForecast>? = responseBody?.items
            if (linesList == null) responseResultInterface.gotError("response is empty")
            else responseResultInterface.gotResult(ResponseMapper.transferToLines(linesList))

//            if (linesList != null) {
//                for (line in linesList) {
//                    Log.i("retrofit", "the next forecastLine: " +
//                            "temp = ${line.forecastMain.temp}, " +
//                            "wind = ${line.forecastWind.windSpeed}")
//                }
//            }
        }

        override fun onFailure(call: Call<ResponseForecastModel>, t: Throwable) {
            Log.i("retrofit", "onfailure, error = $t")
            responseResultInterface.gotError(t.toString())
        }
    }

    fun makeRequest(forecastLocation: ForecastLocation) {
        val lat = forecastLocation.latitude
        val long = forecastLocation.longitude

        forecastApi.getDailyForecast(lat, long, key).enqueue(callback)
    }
}