package com.tcynik.racertimer.forecast.app

import android.app.Application
import com.tcynik.racertimer.forecast.data.network.retrofit.request.ForecastApiInterface
import com.tcynik.racertimer.forecast.di.appModule
import com.tcynik.racertimer.forecast.di.dataModule
import com.tcynik.racertimer.forecast.di.domainModule
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