package com.example.racertimer.forecast.domain.instruments

import android.content.Context
import android.widget.Toast
import com.example.racertimer.forecast.presentation.interfaces.ToasterInterface

class Toaster (private val context: Context): ToasterInterface {
    override fun makeToast(text: String){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }
}