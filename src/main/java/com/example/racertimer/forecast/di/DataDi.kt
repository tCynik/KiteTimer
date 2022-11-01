package com.example.racertimer.forecast.di

import com.example.racertimer.forecast.data.LastForecastLocationRepositoryNameRepository
import com.example.racertimer.forecast.data.LocationsListRepository
import com.example.racertimer.forecast.domain.interfaces.LastLocationNameRepositoryInterface
import com.example.racertimer.forecast.domain.interfaces.LocationsListInterface
import com.example.racertimer.forecast.domain.useCasesOld.LoadLastUseCase
import com.example.racertimer.forecast.domain.useCasesOld.OpenLocationsListUseCase
import org.koin.dsl.module

val dataModule = module {
    single<LastLocationNameRepositoryInterface> {
        LastForecastLocationRepositoryNameRepository(context = get())
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
