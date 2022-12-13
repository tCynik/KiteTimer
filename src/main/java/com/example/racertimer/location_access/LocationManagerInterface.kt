package com.example.racertimer.location_access

interface LocationManagerInterface {
    fun askPermissionGPS()

    fun finishApp()

    fun accessGranted()
}