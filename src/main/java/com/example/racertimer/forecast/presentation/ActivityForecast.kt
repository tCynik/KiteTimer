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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.racertimer.R
import com.example.racertimer.forecast.data.LastForecastLocationRepository
import com.example.racertimer.forecast.data.LocationsListRepository
import com.example.racertimer.forecast.domain.interfaces.ChooseNameFromListInterface
import com.example.racertimer.forecast.domain.interfaces.UpdateForecastLinesInterface
import com.example.racertimer.forecast.domain.models.ForecastLine
import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.useCases.*
import com.example.racertimer.forecast.presentation.mappers.LocationMapper
import kotlinx.android.synthetic.main.activity_forecast.*
import java.text.SimpleDateFormat
import java.util.*

private const val CURRENT_POSITION = "Current"
private const val BUTTON_NAME_CURRENT = "current_position"
private const val EMPTY = ""
const val BROADCAST_ACTION =
    "com.example.racertimer.action.new_location" // значение для фильтра приемника


class ActivityForecast : AppCompatActivity() {
    //private val forecastViewModel by lazy{ViewModelProvider(this).get(ForecastViewModel::class.java)}

    private val lastLocationRepository by lazy {LastForecastLocationRepository(context = applicationContext)}
    private val loadLastLocationUseCase by lazy {LoadLastUseCase(lastLocationRepository)}
    private val saveLastLocationUseCase by lazy {SaveLastUseCase(lastLocationRepository) }

    private val locationsListRepository by lazy {LocationsListRepository(context = applicationContext)}
    private val openLocationsListUseCase by lazy {OpenLocationsListUseCase(locationsListRepository)}
    private val saveLocationsListUseCase by lazy {SaveLocationListUseCase(context = applicationContext, locationsListRepository)}
    private val chooseLocationByNameUseCase by lazy {ChooseLocationFromListUseCase()}

    private val updateForecastLinesInterface = object: UpdateForecastLinesInterface {
        override fun updateForecastLines(queueForecastLines: Queue<ForecastLine>?) {
            if (queueForecastLines != null) {
                fillForecast(queueForecastLines)
                urlReceivedStatus(true)
            } else {
                urlReceivedStatus(false)
            }
        }
    }
    private val updateForecastUseCase by lazy {UpdateForecastUseCase(updateForecastLinesInterface)}
    private val forecastStatusManager by lazy {ForecastStatusManager(updateForecastUseCase)}
    private val chooseNameFromListInterface = object: ChooseNameFromListInterface {
        override fun choose(name: String) {
            val listLocations = openLocationsListUseCase.execute()
            var forecastLocation: ForecastLocation? = null
            if (name == "current") {
                forecastLocation = currentUserLocation
                forecastStatusManager.updateLocation(forecastLocation)
            } else {
                if (listLocations != null)
                    forecastLocation = chooseLocationByNameUseCase.execute(listLocations, name)
            }

            if (forecastLocation != null) {
                forecastStatusManager.updateLocation(forecastLocation)
                btn_select_location.text = forecastLocation.name
                saveLastLocationUseCase.execute(forecastLocation)
            }
        }
    }

    private val selectLocationPopupUseCase = SelectLocationPopupUseCase(this, chooseNameFromListInterface)

    private var currentUserLocation: ForecastLocation? = null
    private var chosenLocationToShowForecast: ForecastLocation? = null
    private var isCurrentForecastDataReceived = false
    private var isForecastAlreadyShown = false
    private var locationUpdateAwaiting = false

//todo: make the onBackPressed move to mainActivity, not closing the app
//todo: on forecast running current location does'nt updating the forecast automaticly
//todo: broadcast not receiving location updates

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        val buttonSelectLocation = findViewById<Button>(R.id.btn_select_location)
        buttonSelectLocation.setOnClickListener(View.OnClickListener {
            val locationsList = openLocationsListUseCase.execute()
            if (locationsList != null)
                selectLocationPopupUseCase.execute(buttonSelectLocation, locationsList)
        })

        currentUserLocation = updateLocationFromIntent()

        // todo: нужно сделать обновление информации прогноза только если таблица не обновлена либо если с момента обновления прошло много времени
        // в рамках MVVM

        updateViewWhenForecastOpening()

        // todo: in release remove fun firstTimeLaunch and locations coordinates hardcode below:
        /**
         * для создания .bin файла запускаем метод с вбитыми координатами точек. В нормльном состоянии
         * финального релиза этот участок программы не нужен (потребуется при изменении структуры ForecastLocation)
         * //firstTimeLaunch(saveLocationsListUseCase)
         */
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // todo: exit to main
    }

    private fun updateViewWhenForecastOpening(){
        val forecastLocation = lastForecastLocation()
        if (forecastLocation == null) {
            locationUpdateAwaiting = true
        }
        else {
        forecastStatusManager.updateLocation(forecastLocation)
        btn_select_location.text = forecastLocation.name
        }
    }

    private fun lastForecastLocation(): ForecastLocation? {
        var forecastLocation: ForecastLocation? = null

        val lastLocationName: String = loadLastLocationUseCase.execute()//updateForecast(loadLastLocationUseCase.execute())
        if (lastLocationName == EMPTY || lastLocationName == CURRENT_POSITION) {
            if (currentUserLocation == null) {
                locationUpdateAwaiting = true
            }
            else forecastLocation = currentUserLocation
        } else {
            val locationsList = openLocationsListUseCase.execute()
            if (locationsList != null) {
                forecastLocation =
                    chooseLocationByNameUseCase.execute(locationsList, lastLocationName)
            }
        }
        return forecastLocation
    }

    private fun firstTimeLaunch(saveLocationListUseCase: SaveLocationListUseCase) {
        val firstTimeMaker = FirstTimeListMakingUseCase()
        val locationsList = firstTimeMaker.execute()
        saveLocationListUseCase.setLocationsList(locationsList)
        saveLocationListUseCase.save()
    }

    override fun onStart() {
        super.onStart()
        initLocationBroadcastListener()
    }

    private fun urlReceivedStatus (isResponseReceived: Boolean) {
        forecastStatusManager.updateUrlResponseStatus(isResponseReceived)
    }

    private fun updateLocationFromIntent (): ForecastLocation? {
        /** если есть, принимаем даныне по местоположению из вывавшего интента  */
        var lastUsersLocation: ForecastLocation? = null
        val catchLocation = intent
        if (catchLocation.hasExtra("latitude") and catchLocation.hasExtra("longitude")) {
            val latitude: Double = catchLocation.getDoubleExtra("latitude", 0.0)
            val longitude: Double = catchLocation.getDoubleExtra("longitude", 0.0)
            lastUsersLocation = ForecastLocation("DEFAULT", latitude = latitude, longitude = longitude)
        }
        return lastUsersLocation
    }

    private fun initLocationBroadcastListener() {
        val locationBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) { // обработка интента
                Log.i("bugfix", "ActivityForecast: receiver has new broadcast ")
                if (intent.hasExtra("location")) { // если в сообщении есть геолокация
                    Log.i("bugfix", "ActivityForecast: broadcast listener has new location ")

                    currentUserLocation = (intent.extras!!["location"] as Location?)?.let {
                        LocationMapper.androidLocationToForecastLocation(it)
                    }
                    if (locationUpdateAwaiting) {
                        forecastStatusManager.updateLocation(currentUserLocation)
                        locationUpdateAwaiting = false
                    }
                } else Log.i("bugfix", "ActivityForecast: receiver has broadcast but no location ")
            }
        }
        val locationIntentFilter =
            IntentFilter(BROADCAST_ACTION) // прописываем интент фильтр для слушателя
        registerReceiver(locationBroadcastReceiver, locationIntentFilter) // регистрируем слушатель
    }

    private fun fillForecast(forecastLines: Queue<ForecastLine>) {
        viewToBeFiled.removeAllViews()
        fillTitle()
        // todo: move to separate class when MVVM realization
        while (!forecastLines.isEmpty()) {
            val item = layoutInflater.inflate(R.layout.forecast_line, viewToBeFiled, false)
            val currentLine = forecastLines.poll()

            val timeFormat = SimpleDateFormat("d MMM HH:mm")
            val time: Long = currentLine.time
            val dateTimeString: String = timeFormat.format(time)
            val isItDay = checkDaytime(time)

            if (currentLine != null) {
                fillForecastLine(
                    lineToFill = item,
                    dateAndTime = dateTimeString,
                    isItDay = isItDay,
                    temperature = currentLine.temperature,
                    windSpeed = currentLine.windSpeed,
                    windGust = currentLine.windGust,
                    windDir = currentLine.windDir
                )
            }
        }
        isForecastAlreadyShown = true
    }

    private fun checkDaytime(time: Long): Boolean {
        val timeFormat = SimpleDateFormat("HH")
        val timeHour = timeFormat.format(time).toInt()

        return timeHour in 9..19
    }

    private fun fillTitle() {
        val item = layoutInflater.inflate(R.layout.forecast_line, viewToBeFiled, false)
        fillForecastLine(
            lineToFill = item,
            dateAndTime = "date",
            isItDay = false,
            temperature = "temp",
            windSpeed = "wind",
            windGust = "gust",
            windDir = "dir"
        )
    }

    private fun fillForecastLine(
        lineToFill: View,
        dateAndTime: String,
        isItDay: Boolean,
        temperature: String,
        windSpeed: String,
        windGust: String,
        windDir: String) {
        if (isItDay) {
            lineToFill.setBackgroundColor(android.graphics.Color.GRAY)
            val textColor = android.graphics.Color.BLACK
            lineToFill.findViewById<TextView>(R.id.forecast_string_time).setTextColor(textColor)
            lineToFill.findViewById<TextView>(R.id.forecast_string_temp).setTextColor(textColor)
            lineToFill.findViewById<TextView>(R.id.forecast_string_wind).setTextColor(textColor)
            lineToFill.findViewById<TextView>(R.id.forecast_string_gust).setTextColor(textColor)
            lineToFill.findViewById<TextView>(R.id.forecast_string_dir).setTextColor(textColor)
            lineToFill.findViewById<TextView>(R.id.wind_speed_gust_separator).setTextColor(textColor)
        }

        val timeTV = lineToFill.findViewById<TextView>(R.id.forecast_string_time)
        timeTV.text = dateAndTime

        val tempTV = lineToFill.findViewById<TextView>(R.id.forecast_string_temp)
        tempTV.text = temperature

        val windSpeedTV = lineToFill.findViewById<TextView>(R.id.forecast_string_wind)
        windSpeedTV.text = windSpeed

        val windGustTV = lineToFill.findViewById<TextView>(R.id.forecast_string_gust)
        windGustTV.text = windGust

        val windDirTV = lineToFill.findViewById<TextView>(R.id.forecast_string_dir)
        windDirTV.text = windDir

        viewToBeFiled.addView(lineToFill)
    }
}