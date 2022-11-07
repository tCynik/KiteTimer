package com.example.racertimer.forecast.di

import com.example.racertimer.forecast.domain.use_cases.ForceUpdateForecastUseCase
import com.example.racertimer.forecast.domain.use_cases.UpdateForecastUseCase
import org.koin.dsl.module

val domainModule = module {
    factory<UpdateForecastUseCase> {
        UpdateForecastUseCase(linesUpdater = get(), toaster = get(), lastLocationRepository = get())
    }

    factory<ForceUpdateForecastUseCase> {
        ForceUpdateForecastUseCase(updateForecastUseCase = get())
    }

}