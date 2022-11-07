package com.example.racertimer.forecast.domain.instruments

import android.content.Context
import android.widget.Toast

class Toaster (private val context: Context) {
    fun makeToast(text: String){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }
}