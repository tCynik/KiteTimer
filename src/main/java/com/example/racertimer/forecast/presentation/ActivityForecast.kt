package com.example.racertimer.forecast.presentation

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import com.example.racertimer.R
import com.example.racertimer.forecast.data.ForecastURLReceiver
import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.useCases.OpenLocationsListUseCase
import com.example.racertimer.forecast.domain.useCases.UpdateForecastUseCase

class ActivityForecast : AppCompatActivity() {

    private val openLocationsListUseCase = OpenLocationsListUseCase()
    private val updateForecastUseCase = UpdateForecastUseCase()

    private val forecastURLReceiver = ForecastURLReceiver()
    private var lastLocation: ForecastLocation? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast2)

        val buttonSelectLocation = findViewById<Button>(R.id.btn_select_location)
        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        val listView = findViewById<LinearLayout>(R.id.listView)
        var lastLocation = updateLocation()


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

    private fun updateForecast(forecastLocation: ForecastLocation) {
        val forecastData = forecastURLReceiver.update(forecastLocation)
    }


}