package com.example.racertimer.forecast.app

import android.app.Application
import com.example.racertimer.forecast.data.network.retrofit.ForecastApiInterface
import com.example.racertimer.forecast.di.appModule
import com.example.racertimer.forecast.di.dataModule
import com.example.racertimer.forecast.di.domainModule
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App: Application() {
    private val baseUrl = "https://api.openweathermap.org"
    lateinit var forecastApi: ForecastApiInterface

    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@App)
            modules(listOf(appModule, dataModule, domainModule))
        }

        forecastApi = configRetrofit()
    }

    private fun configRetrofit(): ForecastApiInterface {
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