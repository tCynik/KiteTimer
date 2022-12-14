package com.tcynik.racertimer.forecast.di

import com.tcynik.racertimer.forecast.presentation.ForecastViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel<ForecastViewModel>{
        ForecastViewModel(
            lastLocationNameRepository = get(),
            locationsListRepository = get(),
            updateForecastUseCase = get()
        )
    }
}