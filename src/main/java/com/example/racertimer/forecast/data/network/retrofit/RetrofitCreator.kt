package com.example.racertimer.forecast.data.network.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitCreator {
    fun createRetrofit(baseUrl: String): ForecastApiInterface {
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

        return retrofit.create(ForecastApiInterface::class.java) // экземплаяр интерфейса
    }
}