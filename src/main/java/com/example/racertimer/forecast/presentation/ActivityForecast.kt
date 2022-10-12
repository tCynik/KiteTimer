package com.example.racertimer.forecast.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.racertimer.R
import com.example.racertimer.forecast.data.LastForecastLocationRepository
import com.example.racertimer.forecast.data.LocationsRepository
import com.example.racertimer.forecast.domain.interfaces.ChooseNameFromListInterface
import com.example.racertimer.forecast.domain.interfaces.UpdateForecastLinesInterface
import com.example.racertimer.forecast.domain.models.ForecastLine
import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.useCases.*
import com.example.racertimer.forecast.presentation.mappers.LocationMapper
import kotlinx.android.synthetic.main.activity_forecast2.*
import java.util.*

private const val CURRENT_POSITION = "Current"
private const val EMPTY = ""
const val BROADCAST_ACTION =
    "com.example.racertimer.action.new_location" // значение для фильтра приемника


class ActivityForecast : AppCompatActivity() {
    //private val forecastViewModel by lazy{ViewModelProvider(this).get(ForecastViewModel::class.java)}

    private val lastLocationRepository by lazy {LastForecastLocationRepository(context = applicationContext)}
    private val loadLastLocationUseCase by lazy {LoadLastUseCase(lastLocationRepository)}
    private val saveLastLocationUseCase by lazy {SaveLastUseCase(lastLocationRepository) }

    private val locationsRepository by lazy {LocationsRepository(context = applicationContext)}
    private val openLocationsListUseCase by lazy {OpenLocationsListUseCase(locationsRepository)}
    private val saveLocationsListUseCase by lazy {SaveLocationListUseCase(context = applicationContext, locationsRepository)}
    private val chooseLocationByNameUseCase by lazy {ChooseLocationFromListUseCase()}

    private val updateForecastLinesInterface = object: UpdateForecastLinesInterface {
        override fun updateForecastLines(queueForecastLines: Queue<ForecastLine>) {
            fillForecast(queueForecastLines)
        }
    }
    private val updateForecastUseCase by lazy {UpdateForecastUseCase(updateForecastLinesInterface)}

    private val chooseNameFromListInterface = object: ChooseNameFromListInterface {
        override fun choose(name: String) {
            val listLocations = openLocationsListUseCase.execute()
            var forecastLocation: ForecastLocation? = null
            if (listLocations != null)
                forecastLocation = chooseLocationByNameUseCase.execute(listLocations, name)
            if (forecastLocation != null) updateForecastUseCase.execute(forecastLocation)
        }
    }

    private val listLocationsOpenUseCase = ListLocationsOpenUseCase(this, chooseNameFromListInterface)

    private var lastLocation: ForecastLocation? = null

    private var currentPositionLocation: ForecastLocation? = null
    private var currentLocationIsShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast2)

        val buttonSelectLocation = findViewById<Button>(R.id.btn_select_location)
        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        val listView = findViewById<LinearLayout>(R.id.listView)
        currentPositionLocation = updateLocationFromIntent()
        //if (lastLocation != null) updateForecast(lastLocation)

        buttonSelectLocation.setOnClickListener(View.OnClickListener {
            Log.i("bugfix", "ActivityForecast: the button was pressed")
            val layoutInflater = layoutInflater
            val locationsList = openLocationsListUseCase.execute()
            if (locationsList != null)
                listLocationsOpenUseCase.execute(buttonSelectLocation, layoutInflater, locationsList)})

        if (savedInstanceState != null) {
            if (savedInstanceState != null || savedInstanceState.isEmpty) {
                updateByForecastOpening()
            }
        } else updateByForecastOpening()
//        val catchLocation = intent
//        if (catchLocation.hasExtra("latitude") and catchLocation.hasExtra("longitude")) {
//            Log.i("bugfix", "ActivityForecast: has current location from intent ")
//            val latitude = catchLocation.getDoubleExtra("latitude", 0.0)
//            val longitude = catchLocation.getDoubleExtra("longitude", 0.0)
//            val currentLocation = ForecastLocation(name = CURRENT_POSITION, latitude = latitude, longitude = longitude)
//            updateForecast(currentLocation)
//        }

        //firstTimeLaunch(saveLocationsListUseCase)
    }

    override fun onResume() {
        super.onResume()
    }

    private fun updateByForecastOpening(){
        // отработка после открытия - берем последнюю локацию (например - текущую, или любую из сохраненных)
        val lastLocationName: String = loadLastLocationUseCase.execute()//updateForecast(loadLastLocationUseCase.execute())
        // todo: потребуется обработка null. если проходит null, берем по текущему

        if (lastLocationName == EMPTY || lastLocationName == CURRENT_POSITION) {
            Log.i("bugfix", "ActivityForecast: LastLocation is empty ")
            Log.i("bugfix", "ActivityForecast: current location name = $ ")
            updateForecastByCurrentPosition()
        }
        val locationsList = openLocationsListUseCase.execute()
        if (locationsList != null) {
            val forecastLocation = chooseLocationByNameUseCase.execute(locationsList, lastLocationName)
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

//    fun selectLocation(view: View){ // call from layout
//        //val layoutInflater = layoutInflater
//    }

    private fun updateLocationFromIntent (): ForecastLocation? {
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
        Log.i("bugfix", "ActivityForecast: updating forecast by location = ${forecastLocation.name} ")
        Log.i("bugfix", "ActivityForecast: longitude = ${forecastLocation.longitude}, latitude = ${forecastLocation.latitude} ")

        saveLastLocationUseCase.execute(forecastLocation)
        // todo: добавить обработку currentPositionIsShowh - должно срабатывать только когда выбрана никакая или текущая позиция
        var result = false
//        result = viewModelScope.launch {
//            val forecastStrings: Queue<String> = updateForecastUseCase.execute(forecastLocation)
//           return@launch fillForecastTable(forecastStrings)
//        }
        return result
    }

    private fun updateForecastByCurrentPosition() {
        if (currentPositionLocation == null) {
            currentLocationIsShown = false
            Log.i("bugfix", "ActivityForecast: current position is null ")

        } else {
            currentLocationIsShown = updateForecast(currentPositionLocation!!)
            Log.i("bugfix", "ActivityForecast: currentPosition is not null ")
        }
    }

    private fun initBroadcastListener() {
        val locationBroadcastReceiver = object : BroadcastReceiver() {
            // создаем broadcastlistener
            override fun onReceive(context: Context, intent: Intent) { // обработка интента
                if (intent.hasExtra("location")) { // если в сообщении есть геолокация            Log.i("bugfix", "ActivityForecast: LastLocation is empty ")
                    Log.i("bugfix", "ActivityForecast: broadcast listener has new location ")

                    currentPositionLocation = (intent.extras!!["location"] as Location?)?.let {
                        LocationMapper.androidLocationToForecastLocation(it)
                    }
                    if (!currentLocationIsShown) {
                        updateForecastByCurrentPosition()
                        currentLocationIsShown = true
                    }
                } else Log.i("bugfix", "ActivityForecast: receiver has broadcast but no location ")
            }
        }
        val locationIntentFilter =
            IntentFilter(BROADCAST_ACTION) // прописываем интент фильтр для слушателя
        registerReceiver(locationBroadcastReceiver, locationIntentFilter) // регистрируем слушатель
    }

    private fun fillForecast(forecastLines: Queue<ForecastLine>) {
        // todo: move to separate class when MVVM realization
        while (!forecastLines.isEmpty()) {
            val item = layoutInflater.inflate(R.layout.forecast_line, listView, false)
            val currentLine = forecastLines.poll()
            if (currentLine != null) {
                val timeTV = item.findViewById<TextView>(R.id.forecast_string_time)
                timeTV.text = currentLine.time

                val tempTV = item.findViewById<TextView>(R.id.forecast_string_temp)
                tempTV.text = currentLine.temperature

                val windSpeedTV = item.findViewById<TextView>(R.id.forecast_string_wind)
                windSpeedTV.text = currentLine.windSpeed

                val windGustTV = item.findViewById<TextView>(R.id.forecast_string_gust)
                windGustTV.text = currentLine.windGust

                val windDirTV = item.findViewById<TextView>(R.id.forecast_string_dir)
                windDirTV.text = currentLine.windDir
            }
        }
    }
}