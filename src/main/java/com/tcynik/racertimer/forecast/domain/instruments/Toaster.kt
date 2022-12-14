package com.tcynik.racertimer.forecast.domain.instruments

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.tcynik.racertimer.forecast.presentation.interfaces.ToasterInterface

class Toaster (private val context: Context): ToasterInterface {
    override fun makeToast(text: String){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
        Log.i("bugfix", "error: $text")
    }
}