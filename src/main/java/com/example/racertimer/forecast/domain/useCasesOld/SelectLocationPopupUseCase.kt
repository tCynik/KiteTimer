package com.example.racertimer.forecast.domain.useCasesOld

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import com.example.racertimer.R
import com.example.racertimer.forecast.domain.ForecastShownManager
import com.example.racertimer.forecast.domain.models.LocationsList
import com.example.racertimer.forecast.domain.use_cases.SelectLocationFromListByName

class SelectLocationPopupUseCase(private val context: Context,
                                 private val forecastShownManager: ForecastShownManager) {
    private val locationSelector = SelectLocationFromListByName()

    fun execute(view: View, locationsList: LocationsList) {
        val popup = PopupMenu(context, view)
        popup.inflate(R.menu.choose_location_layout)
        fillMenuByLocations(popup, locationsList)
        popup.setOnMenuItemClickListener {

            when (it.toString()) {
                "current location by GPS" -> {
                    forecastShownManager.updateLocationToShow(null)//todo: переделать - нужно передавать во VM
                    Log.i("bugfix", "ListOpen: chosen: current")
                    true
                }
                else -> {
                    val forecastLocation = locationsList[it.toString()]
                    //locationSelector.execute(locationsList, it.toString())
                    forecastShownManager.updateLocationToShow(forecastLocation)
                    Log.i("bugfix", "ListOpen: chosen: ${forecastLocation!!.name}")
                    if (forecastLocation == null)
                        Log.i("racer_tomer", "popup item name exception occurred")
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