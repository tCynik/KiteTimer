package com.example.racertimer.forecast.di

import com.example.racertimer.forecast.domain.use_cases.ForceUpdateForecastUseCase
import com.example.racertimer.forecast.domain.use_cases.RunActivityUseCase
import com.example.racertimer.forecast.domain.use_cases.UpdateForecastUseCase
import org.koin.dsl.module

val domainModule = module {
    factory<UpdateForecastUseCase> {
        UpdateForecastUseCase(linesUpdater = get(), toaster = get(), lastLocationRepository = get())
    }

    factory<ForceUpdateForecastUseCase> {
        ForceUpdateForecastUseCase(updateForecastUseCase = get())
    }

    factory<RunActivityUseCase> {
        RunActivityUseCase(lastLocationNameRepository = get(),
            locationsSelectorFromList = get(),
            updaterUserLocation= , //todo: создается во VM
            updateForecastUseCase = get())
    }

}