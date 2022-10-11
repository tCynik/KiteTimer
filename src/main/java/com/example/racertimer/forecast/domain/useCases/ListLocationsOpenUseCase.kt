package com.example.racertimer.forecast.domain.useCases

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import com.example.racertimer.R

class ListLocationsOpenUseCase(val context: Context) {
    fun execute(view: View, layoutInflater: LayoutInflater) {
        val popup = PopupMenu(context, view)
        //popup.setOnMenuItemClickListener { this }
        popup.inflate(R.menu.choose_location_layout)


    }
}