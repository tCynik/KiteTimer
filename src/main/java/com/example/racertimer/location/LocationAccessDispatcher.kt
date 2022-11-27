package com.example.racertimer.location

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.racertimer.Instruments.LocationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationAccessDispatcher(val context: Context, private val managerInterface: LocationManagerInterface) {
    // проверка разрешения -> если разрешение есть, запускаем сервис
    //                        если разрешения нет, вызываем диалог -> если нажато да, вызываем запрос
    // запускаем короутину, которая проверяет наличие разрешения, если разрешение есть - запуск сервиса

    fun execute() {
        if (checkLocationPermission()) startService()
        else permissionGPSDialog()
    }

    private fun checkLocationPermission(): Boolean {
        return !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && // если версия СДК выше версии M (API 23)
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
    }

    private fun permissionGPSDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder
            .setMessage("App necessary GPS to work! Do you want to continue?")
            .setCancelable(false)
            .setPositiveButton("yes") { dialogInterface, i ->
                managerInterface.askPermissionGPS()
                runServiceStartingTimeout()
            }
            .setNegativeButton("no") { dialogInterface, i -> managerInterface.finishApp() }
        val alertDialog = dialogBuilder.create() // создание диалога
        alertDialog.setTitle("No GPS permission") // заголовок
        alertDialog.show() // отображение диалога
    }

    private fun runServiceStartingTimeout() {
        // make coroutine: check permission -> if true - run service
                                            // if false - run self
//        withContext(Dispatchers.IO) {
//            SystemClock.sleep(3000)
//            if (checkLocationPermission()) {
//                Log.i("bugfix: locationDispatcher", "Making service connection... ")
//                startService()
//                managerInterface.accessGranted() // mainActivity now can subscribe to broadcast
//            }
//            else runServiceStartingTimeout()
//        }
        if (checkLocationPermission()) {
            startService()
            managerInterface.accessGranted() // mainActivity now can subscribe to broadcast
        }
    }

    private fun startService() {
        val intentLocationService = Intent(context, LocationService::class.java)
        intentLocationService.setPackage("com.example.racertimer.Instruments")
        context.startService(intentLocationService)
    }
}