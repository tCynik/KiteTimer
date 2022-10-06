package com.example.racertimer.forecast.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.racertimer.R
import com.example.racertimer.forecast.data.LastForecastLocationRepository
import com.example.racertimer.forecast.data.LocationsRepository
import com.example.racertimer.forecast.domain.interfaces.UpdateForecastLinesInterface
import com.example.racertimer.forecast.domain.models.ForecastLine
import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.useCases.*
import com.example.racertimer.forecast.presentation.mappers.LocationMapper
import java.util.*

private const val CURRENT_POSITION = "Current"
const val BROADCAST_ACTION =
    "com.example.racertimer.action.new_location" // значение для фильтра приемника


class ActivityForecast : AppCompatActivity() {
    private val forecastViewModel by lazy{ViewModelProvider(this).get(ForecastViewModel::class.java)}

    private val lastLocationRepository by lazy {LastForecastLocationRepository(context = applicationContext)}
    private val loadLastLocationUseCase by lazy {LoadLastUseCase(lastLocationRepository)}
    private val saveLastLocationUseCase by lazy {SaveLastUseCase(lastLocationRepository) }

    private val locationsRepository by lazy {LocationsRepository(context = applicationContext)}
    private val openLocationsListUseCase by lazy {OpenLocationsListUseCase(locationsRepository)}
    private val saveLocationsListUseCase by lazy {SaveLocationListUseCase(context = applicationContext, locationsRepository)}
    private val chooseLocationUseCase by lazy {ChooseLocationUseCase()}

    private val updateForecastLinesInterface = object: UpdateForecastLinesInterface {
        override fun updateForecastLines(queueForecastLines: Queue<ForecastLine>) {
            while (!queueForecastLines.isEmpty()) {
                val currentLine = queueForecastLines.poll()
                if (currentLine!=null) fillForecastTable(currentLine)
            }
        }
    }
    private val updateForecastUseCase by lazy {UpdateForecastUseCase(updateForecastLinesInterface)}

    private var lastLocation: ForecastLocation? = null

    private var currentPositionLocation: ForecastLocation? = null
    private var currentLocationIsShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast2)

        val buttonSelectLocation = findViewById<Button>(R.id.btn_select_location)
        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        val listView = findViewById<LinearLayout>(R.id.listView)
        var lastLocation = updateLocation()

        if (savedInstanceState != null) {
            if (savedInstanceState.isEmpty) {
                val lastLocationName: String = loadLastLocationUseCase.execute()//updateForecast(loadLastLocationUseCase.execute())
                // todo: потребуется обработка null. если проходит null, берем по текущему
                if (lastLocationName == CURRENT_POSITION) {
                    updateForecastByCurrentPosition()
                }
                val locationsList = openLocationsListUseCase.execute()
                if (locationsList != null) {
                    val forecastLocation = chooseLocationUseCase.execute(locationsList, lastLocationName)
                    if (forecastLocation != null) {
                        updateForecastUseCase.execute(forecastLocation)
                    } else {
                        updateForecastByCurrentPosition()
                    }
                } else {
                    updateForecastByCurrentPosition()
                }
                // todo: if forecast location from searching is null, make error toast
                // todo: make request by geoLocation (when last location name = "default")
            }
        }
        firstTimeLaunch(saveLocationsListUseCase)
    }

    private fun firstTimeLaunch(saveLocationListUseCase: SaveLocationListUseCase) {
        val firstTimeMaker = FirstTimeListMakingUseCase()
        val locationsList = firstTimeMaker.execute()
        saveLocationListUseCase.setLocationsList(locationsList)
        saveLocationListUseCase.save()
    }

    override fun onStart() {
        super.onStart()
        initBroadcastListener()
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

    private fun updateForecast(forecastLocation: ForecastLocation): Boolean {
        //var forecastStrings = Queue<String>//: Queue<String> = updateForecastUseCase.execute(forecastLocation)
        saveLastLocationUseCase.execute(forecastLocation)
        // todo: добавить обработку currentPositionIsShowh - должно срабатывать только когда выбрана никакая или текущая позиция
        var result = false
//        result = viewModelScope.launch {
//            val forecastStrings: Queue<String> = updateForecastUseCase.execute(forecastLocation)
//           return@launch fillForecastTable(forecastStrings)
//        }
        return result
    }

    private fun fillForecastTable(forecastLine: ForecastLine) {

    }


    private fun updateForecastByCurrentPosition() {
        if (currentPositionLocation == null) {
            currentLocationIsShown = false
        } else {
            updateForecast(currentPositionLocation!!)
        }
    }

    private fun initBroadcastListener() {
        val locationBroadcastReceiver = object : BroadcastReceiver() {
            // создаем broadcastlistener
            override fun onReceive(context: Context, intent: Intent) { // обработка интента
                if (intent.hasExtra("location")) { // если в сообщении есть геолокация
                    currentPositionLocation = (intent.extras!!["location"] as Location?)?.let {
                        LocationMapper.androidLocationToForecastLocation(it)
                    }
                    if (!currentLocationIsShown) {
                        updateForecastByCurrentPosition()
                        currentLocationIsShown = true
                    }
                }
            }
        }
        val locationIntentFilter =
            IntentFilter(BROADCAST_ACTION) // прописываем интент фильтр для слушателя
        registerReceiver(locationBroadcastReceiver, locationIntentFilter) // регистрируем слушатель
    }


}