package com.example.racertimer.forecast.data

import android.content.Context
import com.example.racertimer.forecast.domain.interfaces.LastLocationInterface
import com.example.racertimer.forecast.domain.models.ForecastLocation

private val SHARED_PREFERENCES_NAME = "last_location_preferences"
private val LOCATION_NAME_KEY = "location_name"

class LastForecastLocationRepository(context: Context): LastLocationInterface  {
    val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun save(forecastLocation: ForecastLocation): Boolean {
        sharedPreferences.edit().putString(LOCATION_NAME_KEY, forecastLocation.name)
        return true
    }

    override fun load(): String {
        return sharedPreferences.getString(LOCATION_NAME_KEY, "")?: ""
    }
}