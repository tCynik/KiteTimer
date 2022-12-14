package com.tcynik.racertimer.forecast.data.network.retrofit

import com.tcynik.racertimer.forecast.data.network.retrofit.request.ForecastApiInterface
import com.tcynik.racertimer.main_activity.data.network.retrofit.WeatherApiInterface
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ForecastRetrofitCreator {
    fun createForecastRetrofit(baseUrl: String): ForecastApiInterface {
        val retrofit = createRetrofit(baseUrl)
        return retrofit.create(ForecastApiInterface::class.java) // экземплаяр интерфейса
    }

    fun createWeatherRetrofit(baseUrl: String): WeatherApiInterface {
        val retrofit = createRetrofit(baseUrl)
        return retrofit.create(WeatherApiInterface::class.java)
    }

    private fun createRetrofit(baseUrl: String): Retrofit {
        val httpLoggingInterceptor = HttpLoggingInterceptor() // логгер отправки-получения
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder() // клиент с интерцептором
            .addInterceptor(httpLoggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }
}