package com.tcynik.racertimer.forecast.data.repository

import android.content.Context
import android.util.Log
import com.tcynik.racertimer.forecast.domain.models.ForecastLocation
import com.tcynik.racertimer.forecast.domain.models.LocationsList
import org.json.JSONObject
import java.io.IOException

class LocationsAssetLoader(val context: Context) {

    fun execute(): LocationsList {
        var inputString: String? = null
        try{
            inputString = context.applicationContext.assets
                .open("locations")
                .bufferedReader().use{it.readText()}
        } catch (e: IOException) {
            Log.i("racer_timer: listAssertLoader", "IOException in asset loading = $e")
        }
        return if (inputString == null) LocationsList()
        else parseStringToLocationsList(inputString)
    }

    private fun parseStringToLocationsList (inputString: String ): LocationsList {
        val locationsList = LocationsList()
        val resultJson = JSONObject(inputString)

        val jsonArray = resultJson.getJSONArray("list")

        var index = 0
        while (index < jsonArray.length() ) {
            val jSonObject = jsonArray.getJSONObject(index)
            index++

            val forecastLocation = parseJsonToLocation(jSonObject)
            locationsList[forecastLocation.name] = forecastLocation
        }
        return locationsList
    }

    private fun parseJsonToLocation(jsonObject: JSONObject): ForecastLocation {
        val name = jsonObject.getString("name")
        val latitude = jsonObject.getDouble("latitude")
        val longitude = jsonObject.getDouble("longitude")
        return ForecastLocation(
            name = name,
            latitude = latitude,
            longitude = longitude)
    }
}