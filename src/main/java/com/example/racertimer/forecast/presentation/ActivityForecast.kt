package com.example.racertimer.forecast.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.racertimer.R
import com.example.racertimer.forecast.data.LastForecastLocationRepository
import com.example.racertimer.forecast.data.LocationsListRepository
import com.example.racertimer.forecast.domain.ForecastStatusManager
import com.example.racertimer.forecast.domain.interfaces.ChooseNameFromListInterface
import com.example.racertimer.forecast.domain.interfaces.UpdateDataErrorInterface
import com.example.racertimer.forecast.domain.interfaces.UpdateForecastLinesInterface
import com.example.racertimer.forecast.domain.models.ForecastLine
import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.useCases.*
import com.example.racertimer.forecast.presentation.mappers.LocationMapper
import kotlinx.android.synthetic.main.activity_forecast2.*
import java.text.SimpleDateFormat
import java.util.*

private const val CURRENT_POSITION = "current position"//"DEFAULT"
private const val BUTTON_NAME_CURRENT = "current_position"
private const val EMPTY = ""
const val BROADCAST_ACTION =
    "com.example.racertimer.action.new_location" // значение для фильтра приемника


class ActivityForecast : AppCompatActivity() {
    //private val forecastViewModel by lazy{ViewModelProvider(this).get(ForecastViewModel::class.java)}
    private lateinit var forecastViewModel: ForecastViewModel

    private val toastMakingUseCase = ToastMakingUseCase(this) // todo: pass to viewModel
    private val lastLocationRepository by lazy {LastForecastLocationRepository(context = applicationContext)}
    //private val loadLastLocationUseCase by lazy {LoadLastUseCase(lastLocationRepository)}
    private val saveLastLocationUseCase by lazy {SaveLastUseCase(lastLocationRepository) }

    private val locationsListRepository by lazy {LocationsListRepository(context = applicationContext)}
    private val openLocationsListUseCase by lazy {OpenLocationsListUseCase(locationsListRepository)}
    private val saveLocationsListUseCase by lazy {SaveLocationListUseCase(context = applicationContext, locationsListRepository)}
    private val chooseLocationByNameUseCase by lazy {ChooseLocationFromListUseCase()}

    private val updateDataErrorInterface = object: UpdateDataErrorInterface {
        override fun errorOccurs(errorDescription: String) {
            fillNoData() //todo: i need toastMaker:String
            Toast.makeText(applicationContext, "Error: $errorDescription", Toast.LENGTH_LONG).show()
        }
    }
//    private val updateDataErrorUseCase = UpdateDataErrorUseCase(updateDataErrorInterface)
//    private val updateForecastLinesInterface = object: UpdateForecastLinesInterface {
//        override fun updateForecastLines(queueForecastLines: Queue<ForecastLine>?) {
//        }
//    }
//    private val updateForecastUseCase by lazy {UpdateForecastUseCase(updateForecastLinesInterface, updateDataErrorUseCase)}
    private val forecastStatusManager by lazy { ForecastStatusManager(updateForecastUseCase) }
    private val chooseNameFromListInterface = object: ChooseNameFromListInterface {
        override fun choose(name: String) {  //todo: pass by DI
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
                Log.i("bugfix", "ActivityForecast: updating forecast by location named = ${forecastLocation.name}")
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast2)

        forecastViewModel = ViewModelProvider(this,
            ForecastViewModelFactory(
                context = this,
                updateDataErrorInterface = updateDataErrorInterface,
                updateForecastLinesInterface = updateForecastLinesInterface,
                chooseNameFromListInterface = chooseNameFromListInterface
            )).get(ForecastViewModel::class.java)

        val buttonSelectLocation = findViewById<Button>(R.id.btn_select_location)
        buttonSelectLocation.setOnClickListener(View.OnClickListener {
            forecastViewModel.selectLocationClicked()
            val locationsList = openLocationsListUseCase.execute()
            if (locationsList != null)
                selectLocationPopupUseCase.execute(buttonSelectLocation, locationsList)
        })

        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        val listView = findViewById<LinearLayout>(R.id.viewToBeFiled)

        currentUserLocation = updateLocationFromIntent()

        forecastViewModel.updateForecastWhenActivityOpened()
        //updateViewWhenForecastOpening() // todo: заменить метод

        // todo: in release remove fun firstTimeLaunch and locations coordinates hardcode below:
        /**
         * для создания .bin файла запускаем метод с вбитыми координатами точек. В нормльном состоянии
         * финального релиза этот участок программы не нужен (потребуется при изменении структуры ForecastLocation)
         * //firstTimeLaunch(saveLocationsListUseCase)
         */
    }

    override fun onResume() {
        super.onResume()
        initObservers()
    }

    private fun initObservers() {
        forecastViewModel.forecastLinesLive.observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                fillForecast(it)
                urlReceivedStatus(true)
            } else {
                urlReceivedStatus(false)
            }
        })
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
            Log.i("bugfix", "ActivityForecast: current forecast location = ${forecastLocation.name}, lat = ${forecastLocation.latitude} ")
        forecastStatusManager.updateLocation(forecastLocation)
        btn_select_location.text = forecastLocation.name
        }
    }

    private fun lastForecastLocation(): ForecastLocation? {
        var forecastLocation: ForecastLocation? = null

        val lastLocationName: String = loadLastLocationUseCase.execute()//updateForecast(loadLastLocationUseCase.execute())
        Log.i("bugfix", "ActivityForecast: lastLocation name = $lastLocationName ")
        if (lastLocationName == EMPTY || lastLocationName == CURRENT_POSITION) {
            Log.i("bugfix", "ActivityForecast: LastLocation is empty ")
            if (currentUserLocation == null) {
                Log.i("bugfix", "ActivityForecast: current location is null ")
                locationUpdateAwaiting = true
            }
            else {
                forecastLocation = currentUserLocation
                Log.i("bugfix", "ActivityForecast: forecastLocation was switched to current location ")
            }
        } else {
            val locationsList = openLocationsListUseCase.execute()
            if (locationsList != null) {
                forecastLocation =
                    chooseLocationByNameUseCase.execute(locationsList, lastLocationName)
            }
        }
        Log.i("bugfix", "ActivityForecast: last location - chosen name = ${forecastLocation?.name} ")
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
            lastUsersLocation = ForecastLocation(CURRENT_POSITION, latitude = latitude, longitude = longitude)
        }
        return lastUsersLocation
    }

    private fun initLocationBroadcastListener() {
        val locationBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) { // обработка интента
                if (intent.hasExtra("location")) { // если в сообщении есть геолокация            Log.i("bugfix", "ActivityForecast: LastLocation is empty ")
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

    private fun fillNoData() {
        val item = layoutInflater.inflate(R.layout.forecast_line, viewToBeFiled, false)
        fillForecastLine(item, "", false, "NO", "DATA", "", "")
    }
}