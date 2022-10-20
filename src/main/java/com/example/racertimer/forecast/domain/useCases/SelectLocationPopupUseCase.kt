package com.example.racertimer.forecast.domain.useCases

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import com.example.racertimer.R
import com.example.racertimer.forecast.domain.interfaces.ChooseNameFromListInterface
import com.example.racertimer.forecast.domain.models.LocationsList
import com.example.racertimer.forecast.presentation.ActivityForecast

class SelectLocationPopupUseCase(private val context: Context, private val chooseInterface: ChooseNameFromListInterface) {
    fun execute(view: View, locationsList: LocationsList) {
        val popup = PopupMenu(context, view)
        popup.inflate(R.menu.choose_location_layout)
        fillMenuByLocations(popup, locationsList)
        popup.setOnMenuItemClickListener {

            when (it.toString()) {
                "current location by GPS" -> {
                    chooseInterface.choose("current")// если выбрана текущая локация
                    true
                }
                else -> {
                    val forecastLocation = locationsList[it.toString()]
                    chooseInterface.choose(forecastLocation!!.name)
                    true
                }
            }
        }
        popup.show()
    }

    private fun fillMenuByLocations(popupMenu: PopupMenu, locationsList: LocationsList) {
        for (forecastLocation in locationsList) {
            popupMenu.menu.add(forecastLocation.value.name)
        }
    }
}