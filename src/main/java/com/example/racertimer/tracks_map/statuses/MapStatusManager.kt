package com.example.racertimer.tracks_map.statuses

import android.util.Log

class MapStatusManager(private val statusChanger: MapStatusInterface) {
    private var hasSizes = false
    private var hasLandmark = false

    fun gotSizes() {
        hasSizes = true
        checkStatus()
    }

    fun gotLandmark() {
        hasLandmark = true
        checkStatus()
    }

    private fun checkStatus() {
        var currentStatus = MapStatus.NO_SIZES_NO_LANDMARK
        if (hasSizes) {
            currentStatus = if (hasLandmark) MapStatus.READY
            else MapStatus.HAS_SIZES_NO_LANDMARK
        }
        Log.i("bugfix: mapStatusManager", "current status is: $currentStatus")
        statusChanger.onStatusChanged(currentStatus)
    }
}