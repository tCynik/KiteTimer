package com.example.racertimer.forecast.domain.useCasesOld

import com.example.racertimer.forecast.domain.interfaces.UpdateDataErrorInterface

class UpdateDataErrorUseCase (private val updateDataErrorInterface: UpdateDataErrorInterface){
    fun execute (errorDescription: String) {
        updateDataErrorInterface.errorOccurs(errorDescription)
    }
}