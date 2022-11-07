package com.example.racertimer.forecast.di

import com.example.racertimer.forecast.data.LastForecastLocationNameRepository
import com.example.racertimer.forecast.data.LocationSelectorByNameImpl
import com.example.racertimer.forecast.data.LocationsListRepository
import com.example.racertimer.forecast.domain.interfaces.LastLocationNameRepositoryInterface
import com.example.racertimer.forecast.domain.interfaces.LocationsListRepositoryInterface
import com.example.racertimer.forecast.domain.instruments.Toaster
import com.example.racertimer.forecast.presentation.interfaces.LocationSelectorByNameInterface
import org.koin.dsl.module

val dataModule = module {
    single<Toaster>{
        Toaster(context = get())
    }

    single<LastLocationNameRepositoryInterface> {
        LastForecastLocationNameRepository(context = get())
    }

    single<LocationsListRepositoryInterface> {
        LocationsListRepository(context = get(), toaster = get())
    }

    single<LocationSelectorByNameInterface> {
        LocationSelectorByNameImpl(locationsListRepository = get())
    }

}
