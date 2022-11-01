package com.example.racertimer.forecast.domain.useCasesOld

import android.util.Log
import com.example.racertimer.forecast.domain.interfaces.LastLocationNameRepositoryInterface

class LoadLastUseCase(private val lastLocationNameRepositoryInterface: LastLocationNameRepositoryInterface) {

    fun execute() : String {
        Log.i("bugfix", "LoadLastLocation: LastLocation = ${lastLocationNameRepositoryInterface.load()} ")

        return lastLocationNameRepositoryInterface.load()
    }
}