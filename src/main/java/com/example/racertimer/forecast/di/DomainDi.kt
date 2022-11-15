package com.example.racertimer.forecast.di

import com.example.racertimer.forecast.domain.use_cases.UpdateForecastUseCase
import org.koin.dsl.module

val domainModule = module {
    factory<UpdateForecastUseCase> {
        UpdateForecastUseCase(toaster = get(), lastLocationRepository = get())
    }

}