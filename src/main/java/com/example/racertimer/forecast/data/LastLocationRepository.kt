package com.example.racertimer.forecast.data

import android.content.Context
import android.util.Log
import com.example.racertimer.forecast.domain.interfaces.LastLocationNameRepositoryInterface
import com.example.racertimer.forecast.domain.models.ForecastLocation

private val SHARED_PREFERENCES_NAME = "last_location_preferences"
private val LOCATION_NAME_KEY = "location_name"

class LastForecastLocationNameRepository(context: Context): LastLocationNameRepositoryInterface  {
    private val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun save(forecastLocation: ForecastLocation): Boolean {
        val result = sharedPreferences.edit().putString(LOCATION_NAME_KEY, forecastLocation.name).commit()
        Log.i("bugfix", "LastLocationsRepo: save last = $result ")
        return result
    }

    override fun load(): String {
        return sharedPreferences.getString(LOCATION_NAME_KEY, "")?: ""
    }
}