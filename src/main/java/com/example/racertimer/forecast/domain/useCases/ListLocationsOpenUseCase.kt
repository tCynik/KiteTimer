package com.example.racertimer.forecast.domain.useCases

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import com.example.racertimer.R
import com.example.racertimer.forecast.domain.interfaces.ChooseNameFromListInterface
import com.example.racertimer.forecast.domain.models.LocationsList
import com.example.racertimer.forecast.presentation.ActivityForecast

class ListLocationsOpenUseCase(private val context: Context, private val chooseInterface: ChooseNameFromListInterface) {
    fun execute(view: View, layoutInflater: LayoutInflater, locationsList: LocationsList) {
        val popup = PopupMenu(context, view)
        popup.inflate(R.menu.choose_location_layout)
        fillMenuByLocations(popup, locationsList)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.current -> {
                    chooseInterface.choose("current")// если выбрана текущая локация
                    true
                }
                else -> {
                    for (forecastLocation in locationsList) {
                        val name = forecastLocation.value.name
                        if (it.equals(name))  {
                            chooseInterface.choose(name)
                        }
                    }
                    true
                }
            }
        }
    }

    private fun fillMenuByLocations(popupMenu: PopupMenu, locationsList: LocationsList) {
        for (forecastLocation in locationsList) {
            popupMenu.menu.add(forecastLocation.value.name)
        }
    }
}