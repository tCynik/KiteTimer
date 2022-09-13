package com.example.racertimer.SailingTools

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.racertimer.ContentUpdater

class SailingToolsViewModel: ViewModel() {
    private val LOG_TAG = "racer_timer_sailing_tools_vm"
    val model: Model

    var maxSpeedLive = MutableLiveData<Int>()
    var maxUpwindLive = MutableLiveData<Int>()
    var maxDownwindLive = MutableLiveData<Int>()
    var speedLive = MutableLiveData<Int>()
    var bearingLive = MutableLiveData<Int>()
    var VMGLive = MutableLiveData<Int>()
    var courseToWindLive = MutableLiveData<Int>()
    var windDirLive = MutableLiveData<Int>()

    init {
        Log.i(LOG_TAG, "view model was created")
        model = Model()
    }

    fun onLocationChanged(velocity: Int, bearing: Int) {
        model.onLocationChanged(velocity, bearing)
        speedLive.value = velocity
        bearingLive.value = bearing
    }

    fun onWindChanged(windDirection: Int) {
        windDirLive.value = windDirection
        model.onWindChanged(windDirection)
    }

    override fun onCleared() {
        super.onCleared()
    }
}