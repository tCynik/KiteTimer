package com.example.racertimer.forecast.presentation

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import com.example.racertimer.R
import com.example.racertimer.forecast.data.LastForecastLocationRepository
import com.example.racertimer.forecast.data.LocationsRepository
import com.example.racertimer.forecast.data.ulrequest.ForecastURLReceiver
import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.useCases.LoadLastUseCase
import com.example.racertimer.forecast.domain.useCases.OpenLocationsListUseCase
import com.example.racertimer.forecast.domain.useCases.SaveLastUseCase
import com.example.racertimer.forecast.domain.useCases.UpdateForecastUseCase

class ActivityForecast : AppCompatActivity() {

    private val lastLocationRepository by lazy {LastForecastLocationRepository(context = applicationContext)}
    private val loadLastLocationUseCase by lazy {LoadLastUseCase(lastLocationRepository)}
    private val saveLastLocationUseCase by lazy {SaveLastUseCase(lastLocationRepository) }

    private val locationsRepository by lazy {LocationsRepository(context = applicationContext)}
    private val openLocationsListUseCase by lazy {OpenLocationsListUseCase(locationsRepository)}
    private val updateForecastUseCase by lazy {UpdateForecastUseCase(lastLocationRepository)}

    private val forecastURLReceiver = ForecastURLReceiver()
    private var lastLocation: ForecastLocation? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast2)

        val buttonSelectLocation = findViewById<Button>(R.id.btn_select_location)
        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        val listView = findViewById<LinearLayout>(R.id.listView)
        var lastLocation = updateLocation()

        if (savedInstanceState != null) {
            if (savedInstanceState.isEmpty) {
                val lastLocationName: String = updateForecast(loadLastLocationUseCase.execute())
                val forecastList = loadLastLocationUseCase.execute()
                val forecastLocation = chooseLocationUseCase.execute(forecastList, lastLocationName)
                updateForecastUseCase.execute(forecastLocation)
                // todo: if forecast location from searching is null, make error toast
            }
        }
    }



    private fun updateLocation (): ForecastLocation? {
        /** если есть, принимаем даныне по местоположению из вывавшего интента  */
        val catchLocation = intent
        if (catchLocation.hasExtra("latitude") and catchLocation.hasExtra("longitude")) {
            val latitude: Double = catchLocation.getDoubleExtra("latitude", 0.0)
            val longitude: Double = catchLocation.getDoubleExtra("longitude", 0.0)
            lastLocation = ForecastLocation("DEFAULT", latitude = latitude, longitude = longitude)
        }
        return lastLocation
    }

    private fun updateForecast(forecastLocation: String): String {
        val forecastData = forecastURLReceiver.update(forecastLocation)
    }


}