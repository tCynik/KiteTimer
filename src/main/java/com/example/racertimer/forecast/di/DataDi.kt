package com.example.racertimer.forecast.di

import com.example.racertimer.forecast.data.LastForecastLocationNameRepository
import com.example.racertimer.forecast.data.LocationSelectorImpl
import com.example.racertimer.forecast.data.LocationsListRepository
import com.example.racertimer.forecast.domain.interfaces.LastLocationNameRepositoryInterface
import com.example.racertimer.forecast.domain.interfaces.LocationsListInterface
import com.example.racertimer.forecast.domain.Toaster
import com.example.racertimer.forecast.presentation.interfaces.LocationSelectorFromListInterface
import org.koin.dsl.module

val dataModule = module {
    single<Toaster>{
        Toaster(context = get())
    }

    single<LastLocationNameRepositoryInterface> {
        LastForecastLocationNameRepository(context = get())
    }

    single<LocationsListInterface> {
        LocationsListRepository(context = get(), toaster = get())
    }

    single<LocationSelectorFromListInterface> {
        LocationSelectorImpl(locationsListRepository = get())
    }

}
