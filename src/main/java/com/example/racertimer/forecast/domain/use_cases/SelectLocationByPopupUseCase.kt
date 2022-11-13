package com.example.racertimer.forecast.domain.use_cases

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import com.example.racertimer.R
import com.example.racertimer.forecast.domain.models.LocationsList
import com.example.racertimer.forecast.presentation.interfaces.SelectLocationInterface

class SelectLocationByPopupUseCase(private val context: Context,
                                   private val locationSelector: SelectLocationInterface) {

    fun execute(view: View, locationsList: LocationsList) {
        val popup = PopupMenu(context, view)
        popup.inflate(R.menu.choose_location_layout)
        fillMenuByLocations(popup, locationsList)
        popup.setOnMenuItemClickListener {

            when (it.toString()) {
                "current location by GPS" -> {
                    locationSelector.onLocationSelected(null)
                    true
                }
                else -> {
                    val forecastLocation = locationsList[it.toString()]
                    locationSelector.onLocationSelected(forecastLocation)
                    if (forecastLocation == null)
                        Log.i("racer_timer", "popup item name exception occurred")
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