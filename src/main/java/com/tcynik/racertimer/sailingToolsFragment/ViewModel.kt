package com.tcynik.racertimer.sailingToolsFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModel: ViewModel() {
    private val LOG_TAG = "racer_timer_sailing_tools_vm"
    private val model: Model

    var speedLive = MutableLiveData<Int>()
    var maxSpeedLive = MutableLiveData<Int>()
    var VMGLive = MutableLiveData<Int>()
    var maxUpwindLive = MutableLiveData<Int>()
    var maxDownwindLive = MutableLiveData<Int>()
    var bearingLive = MutableLiveData<Int>()
    var windDirectionLive = MutableLiveData<Int>()
    var courseToWindLive = MutableLiveData<Int>()
    var percentVelocityLive = MutableLiveData<Int>()


    init {
        //Log.i(LOG_TAG, "view model was created")
        val fieldsUpdaters = initFieldsUpdatersByModel()
        model = Model(fieldsUpdaters)
    }

    private fun initFieldsUpdatersByModel(): Map<Fields, FieldUpdater> {
        val mapOfUpdaters = mutableMapOf<Fields, FieldUpdater>()

        var updater = FieldUpdater { value -> speedLive.value = value }
        mapOfUpdaters[Fields.VELOCITY] = updater

        updater = FieldUpdater { value -> maxSpeedLive.value = value }
        mapOfUpdaters[Fields.MAX_VELOCITY] = updater

        updater = FieldUpdater { value -> bearingLive.value = value }
        mapOfUpdaters[Fields.BEARING] = updater

        updater = FieldUpdater { value -> courseToWindLive.value = value }
        mapOfUpdaters[Fields.COURSE_TO_WIND] = updater

        updater = FieldUpdater { value -> VMGLive.value = value }
        mapOfUpdaters[Fields.VMG] = updater

        updater = FieldUpdater { value -> maxUpwindLive.value = value }
        mapOfUpdaters[Fields.MAX_UPWIND] = updater

        updater = FieldUpdater { value -> maxDownwindLive.value = value }
        mapOfUpdaters[Fields.MAX_DOWNWIND]=updater

        updater = FieldUpdater { value -> percentVelocityLive.value = value }
        mapOfUpdaters[Fields.PERCENT_VELOCITY]=updater

        return mapOfUpdaters
    }

    fun onLocationChanged(velocityMpS: Int, bearing: Int) {
        model.onLocationChanged(velocityMpS, bearing)
        bearingLive.value = bearing
    }

    fun onWindChanged(windDirection: Int) {
        model.onWindChanged(windDirection)
        windDirectionLive.value = windDirection
    }

    fun resetMaximums() {
        model.setMaximums(0, 0, 0)
    }

    override fun onCleared() {
        super.onCleared()
    }
}