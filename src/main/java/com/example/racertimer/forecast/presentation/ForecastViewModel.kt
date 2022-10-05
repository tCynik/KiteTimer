package com.example.racertimer.forecast.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.racertimer.forecast.domain.models.ForecastLine
import com.example.racertimer.forecast.domain.models.LocationsList
import java.util.*

class ForecastViewModel: ViewModel() {
    var locationsListLive: MutableLiveData<LocationsList> = MutableLiveData()
    val forecastLines: MutableLiveData<Queue<ForecastLine>> = MutableLiveData()

    // todo: liveFields to be created:
    //
//  next
}