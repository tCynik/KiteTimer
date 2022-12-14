package com.tcynik.racertimer.forecast.di

import com.tcynik.racertimer.forecast.domain.use_cases.UpdateForecastUseCase
import org.koin.dsl.module

val domainModule = module {
    factory<UpdateForecastUseCase> {
        UpdateForecastUseCase(
            toaster = get(),
            lastLocationRepository = get())
    }

}