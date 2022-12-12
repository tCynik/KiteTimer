package com.example.racertimer.forecast.app

import android.app.Application
import com.example.racertimer.forecast.data.network.retrofit.request.ForecastApiInterface
import com.example.racertimer.forecast.di.appModule
import com.example.racertimer.forecast.di.dataModule
import com.example.racertimer.forecast.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

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