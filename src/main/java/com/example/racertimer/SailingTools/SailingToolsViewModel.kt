package com.example.racertimer.SailingTools

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SailingToolsViewModel: ViewModel() {
    private val LOG_TAG = "racer_timer_sailing_tools_vm"
    val model: Model

    var maxSpeedLive = MutableLiveData<Int>()
    var maxVMGLive = MutableLiveData<Int>()
    var maxUpwindLive = MutableLiveData<Int>()
    var maxDownwindLive = MutableLiveData<Int>()
    var speedLive = MutableLiveData<Int>()
    var bearingLive = MutableLiveData<Int>()
    var VMGLive = MutableLiveData<Int>()
    var courseToWindLive = MutableLiveData<Int>()
    var windDirLive = MutableLiveData<Int>()

    init {
        Log.i(LOG_TAG, "view model was created")
        val fieldsUpdaters = initFieldsUpdaters()
        model = Model(fieldsUpdaters)
    }

    private fun initFieldsUpdaters(): Map<Fields, FieldUpdater> {
        val mapOfUpdaters = mutableMapOf<Fields, FieldUpdater>()

        var updater = FieldUpdater { value -> maxSpeedLive.value = value }
        mapOfUpdaters[Fields.MAX_VELOCITY] = updater

        updater = FieldUpdater { value -> maxVMGLive.value = value }
        mapOfUpdaters[Fields.MAX_VMG] = updater

        updater = FieldUpdater { value -> courseToWindLive.value = value }
        mapOfUpdaters[Fields.COURSE_TO_WIND] = updater

        updater = FieldUpdater { value -> VMGLive.value = value }
        mapOfUpdaters[Fields.VMG] = updater

        updater = FieldUpdater { value -> maxUpwindLive.value = value }
        mapOfUpdaters[Fields.MAX_UPWIND] = updater

        updater = FieldUpdater { value -> maxDownwindLive.value = value }
        mapOfUpdaters[Fields.MAX_DOWNWIND]=updater

        return mapOfUpdaters
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