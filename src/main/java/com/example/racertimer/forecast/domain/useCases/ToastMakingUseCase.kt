package com.example.racertimer.forecast.domain.useCases

import android.content.Context
import android.widget.Toast

class ToastMakingUseCase (private val context: Context) {
    fun make(text: String){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }
}