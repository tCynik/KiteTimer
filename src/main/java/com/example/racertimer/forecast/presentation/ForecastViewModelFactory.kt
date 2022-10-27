package com.example.racertimer.forecast.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.racertimer.forecast.data.LastForecastLocationRepository
import com.example.racertimer.forecast.data.LocationsListRepository
import com.example.racertimer.forecast.domain.interfaces.ChooseNameFromListInterface
import com.example.racertimer.forecast.domain.interfaces.UpdateDataErrorInterface
import com.example.racertimer.forecast.domain.interfaces.UpdateForecastLinesInterface
import com.example.racertimer.forecast.domain.useCases.*

class ForecastViewModelFactory(
    private val context: Context,
    private val updateDataErrorInterface: UpdateDataErrorInterface,
    private val updateForecastLinesInterface: UpdateForecastLinesInterface,
    private val chooseNameFromListInterface: ChooseNameFromListInterface) : ViewModelProvider.Factory {

    private val lastLocationRepository by lazy { LastForecastLocationRepository(context = context) }
    private val loadLastUseCase by lazy { LoadLastUseCase(lastLocationRepository) }
    private val saveLastLocationUseCase by lazy { SaveLastUseCase(lastLocationRepository) }

    private val locationsListRepository by lazy { LocationsListRepository(context = context) }
    private val openLocationsListUseCase by lazy { OpenLocationsListUseCase(locationsListRepository) }
    private val saveLocationsListUseCase by lazy { SaveLocationListUseCase(context = context, locationsListRepository) }
    private val chooseLocationByNameUseCase by lazy { ChooseLocationFromListUseCase() }
    private val updateDataErrorUseCase = UpdateDataErrorUseCase(updateDataErrorInterface)
    private val updateForecastUseCase by lazy { UpdateForecastUseCase(updateForecastLinesInterface, updateDataErrorUseCase) }
    private val selectLocationPopupUseCase = SelectLocationPopupUseCase(context, chooseNameFromListInterface)


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ForecastViewModel(
        chooseLocationByNameUseCase = chooseLocationByNameUseCase,
        loadLastUseCase = loadLastUseCase,
        openLocationsListUseCase = openLocationsListUseCase,
        saveLastUseCase = saveLastLocationUseCase,
        saveLocationListUseCase = saveLocationsListUseCase,
        updateDataErrorUseCase = updateDataErrorUseCase,
        updateForecastUseCase = updateForecastUseCase,
        ) as T
    }
}