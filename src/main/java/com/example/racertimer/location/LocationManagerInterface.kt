package com.example.racertimer.location

interface LocationManagerInterface {
    fun askPermissionGPS()

    fun finishApp()

    fun accessGranted()
}