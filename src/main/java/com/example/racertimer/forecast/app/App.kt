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
    lateinit var forecastApi: ForecastApiInterface

    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@App)
            modules(listOf(appModule, dataModule, domainModule))
        }

    }
}