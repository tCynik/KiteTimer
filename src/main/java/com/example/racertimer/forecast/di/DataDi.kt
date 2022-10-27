package com.example.racertimer.forecast.di

import com.example.racertimer.forecast.data.LastForecastLocationRepository
import com.example.racertimer.forecast.data.LocationsListRepository
import com.example.racertimer.forecast.domain.interfaces.LastLocationInterface
import com.example.racertimer.forecast.domain.interfaces.LocationsListInterface
import com.example.racertimer.forecast.domain.useCases.LoadLastUseCase
import com.example.racertimer.forecast.domain.useCases.OpenLocationsListUseCase
import org.koin.core.scope.get
import org.koin.dsl.module

val dataModule = module {
    single<LastLocationInterface> {
        LastForecastLocationRepository(context = get())
    }

    single<LoadLastUseCase> {
        LoadLastUseCase(lastLocationInterface = get())
    }

    single<OpenLocationsListUseCase> {
        OpenLocationsListUseCase(locationsListInterface =  get())
    }
    
    single<LocationsListInterface> {
        LocationsListRepository(context = get())
    }

}

private val lastLocationRepository by lazy { LastForecastLocationRepository(context = applicationContext) }
private val locationsListRepository by lazy { LocationsListRepository(context = applicationContext) }
