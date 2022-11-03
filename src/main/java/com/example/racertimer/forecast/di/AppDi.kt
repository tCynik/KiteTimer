package com.example.racertimer.forecast.di

import com.example.racertimer.forecast.presentation.interfaces.LinesUpdater
import com.example.racertimer.forecast.presentation.interfaces.UpdateForecastLinesInterface
import org.koin.dsl.module

val appModule = module {
    factory<UpdateForecastLinesInterface> {
        LinesUpdater(forecastLinesLive = )
        //todo: где взять ливдата? Либо как сюда запихнуть обьект созданный динамически в рантайме?
    }
}