package com.tcynik.racertimer.forecast.data.repository

import android.content.Context
import com.tcynik.racertimer.forecast.domain.interfaces.LastLocationNameRepositoryInterface
import com.tcynik.racertimer.forecast.domain.models.ForecastLocation

private const val SHARED_PREFERENCES_NAME = "last_location_preferences"
private const val LOCATION_NAME_KEY = "location_name"

class LastForecastLocationNameRepository(context: Context): LastLocationNameRepositoryInterface {
    private val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun save(forecastLocation: ForecastLocation): Boolean {
        return sharedPreferences.edit().putString(LOCATION_NAME_KEY, forecastLocation.name).commit()
    }

    override fun load(): String {
        return sharedPreferences.getString(LOCATION_NAME_KEY, "")?: ""
    }
}