package com.tcynik.racertimer.location_access

interface LocationManagerInterface {
    fun askPermissionGPS()

    fun finishApp()

    fun accessGranted()
}