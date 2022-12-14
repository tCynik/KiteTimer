package com.tcynik.racertimer.forecast.di

import com.tcynik.racertimer.forecast.data.repository.LastForecastLocationNameRepository
import com.tcynik.racertimer.forecast.data.repository.LocationsListRepository
import com.tcynik.racertimer.forecast.domain.instruments.Toaster
import com.tcynik.racertimer.forecast.domain.interfaces.LastLocationNameRepositoryInterface
import com.tcynik.racertimer.forecast.domain.interfaces.LocationsListRepositoryInterface
import com.tcynik.racertimer.forecast.presentation.interfaces.LocationSelectorByNameInterface
import com.tcynik.racertimer.forecast.presentation.interfaces.ToasterInterface
import org.koin.dsl.module

val dataModule = module {

    single<ToasterInterface>{
        Toaster(context = get())
    }

    single<LastLocationNameRepositoryInterface> {
        LastForecastLocationNameRepository(context = get())
    }

    single<LocationsListRepositoryInterface> {
        LocationsListRepository(context = get(), toaster = get())
    }

    single<LocationSelectorByNameInterface> {
        com.tcynik.racertimer.forecast.data.LocationSelectorByNameImpl(locationsListRepository = get())
    }
}
