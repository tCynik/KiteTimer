package com.tcynik.racertimer.forecast.data.network.retrofit

import android.util.Log
import com.tcynik.racertimer.forecast.data.network.retrofit.request.ForecastApiInterface
import com.tcynik.racertimer.forecast.data.network.retrofit.response.ResponseForecastModel
import com.tcynik.racertimer.forecast.data.network.retrofit.response.TimeHourForecast
import com.tcynik.racertimer.forecast.domain.models.ForecastLocation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RetrofitForecastManager(val responseResultInterface: com.tcynik.racertimer.forecast.data.network.ResponseResultInterface, private val key: String) {
    private val forecastCallback = object: Callback<ResponseForecastModel> {
        override fun onResponse(
            call: Call<ResponseForecastModel>,
            response: Response<ResponseForecastModel>
        ) {
            val responseBody: ResponseForecastModel? = response.body()
            val linesList: List<TimeHourForecast>? = responseBody?.items
            if (linesList == null) responseResultInterface.gotError("response is empty")
            else responseResultInterface.gotResult(ResponseMapper().transferToLines(linesList))
        }

        override fun onFailure(call: Call<ResponseForecastModel>, t: Throwable) {
            Log.i("racer_timer_retrofit", "on Failure, error = $t")
            responseResultInterface.gotError(t.toString())
        }
    }

    fun makeForecastRequest(forecastApi: ForecastApiInterface, forecastLocation: ForecastLocation) {
        val lat = forecastLocation.latitude
        val long = forecastLocation.longitude
        forecastApi.getDailyForecast(lat, long, key).enqueue(forecastCallback)
    }
}