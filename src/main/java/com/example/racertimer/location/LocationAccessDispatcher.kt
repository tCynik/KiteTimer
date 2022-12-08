package com.example.racertimer.location

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.*

class LocationAccessDispatcher(val context: Context, private val managerInterface: LocationManagerInterface) {

    fun execute() {
        if (checkLocationPermission()) managerInterface.accessGranted()
        else permissionGPSDialog()
    }

    private fun checkLocationPermission(): Boolean {
        return !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
    }

    private fun permissionGPSDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder
            .setMessage("App needs GPS to work! Do you want to continue?")
            .setCancelable(false)
            .setPositiveButton("yes") { dialogInterface, i ->
                managerInterface.askPermissionGPS()
                runServiceStartingTimeout()
            }
            .setNegativeButton("no") { dialogInterface, i ->
                managerInterface.finishApp()
            }
        val alertDialog = dialogBuilder.create() // создание диалога
        alertDialog.setTitle("No GPS permission") // заголовок
        alertDialog.show() // отображение диалога
    }

    private fun runServiceStartingTimeout() {
        val timerScope = CoroutineScope(Job())
        timerScope.launch {
            if (checkLocationPermission()) {
                managerInterface.accessGranted()
            }
            else {
                delay(5000)
                managerInterface.askPermissionGPS()
                runServiceStartingTimeout()
            }
        }
    }
}