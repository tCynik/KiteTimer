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
import androidx.lifecycle.Observer
import com.example.racertimer.R
import com.example.racertimer.forecast.domain.models.ForecastLine
import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.use_cases.SelectLocationByPopupUseCase
import com.example.racertimer.forecast.presentation.interfaces.SelectLocationInterface
import com.example.racertimer.forecast.presentation.models_mappers.LocationMapper
import kotlinx.android.synthetic.main.activity_forecast2.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

private const val CURRENT_POSITION = "current position"//"DEFAULT"
private const val BUTTON_NAME_CURRENT = "current_position"
private const val EMPTY = ""
const val BROADCAST_ACTION =
    "com.example.racertimer.action.new_location" // значение для фильтра приемника

// old Manifest transcription in Activity:             android:name=".forecast.presentation.ActivityForecast"
class ActivityForecast : AppCompatActivity() {
    private val forecastViewModel by viewModel<ForecastViewModel>()

//    private val openLocationsListUseCase by lazy {OpenLocationsListUseCase(locationsListRepository)}
//    private val saveLocationsListUseCase by lazy { SaveLocationListUseCase(context = applicationContext, locationsListRepository) }
//    private val selectLocationFromListByName by lazy { SelectLocationFromListByName() }

//    private val linesUpdater = LinesUpdater(forecastLinesLive = forecastViewModel.forecastLinesLive)
    //private val forecastShownManager = ForecastShownManager(updateForecastUseCase)

    private val locationSelector = object: SelectLocationInterface {
        override fun onLocationSelected(forecastLocation: ForecastLocation?) {
            if (forecastLocation == null) forecastViewModel.updateForecastByUserLocation()
        }
    }
    private val selectLocationByPopupUseCase = SelectLocationByPopupUseCase(
        this,
        locationSelector = locationSelector)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast2)

        val buttonSelectLocation = findViewById<Button>(R.id.btn_select_location)
        buttonSelectLocation.setOnClickListener(View.OnClickListener {
            val locationsList = forecastViewModel.getLocationsList()
            if (locationsList != null)
                selectLocationByPopupUseCase.execute(buttonSelectLocation, locationsList)
        })

        updateLocationFromIntent()
        // todo: in release remove fun firstTimeLaunch and locations coordinates hardcode below:
        /**
         * для создания .bin файла запускаем метод с вбитыми координатами точек. В нормльном состоянии
         * финального релиза этот участок программы не нужен (потребуется при изменении структуры ForecastLocation)
         * //firstTimeLaunch(saveLocationsListUseCase)
         */
    }

    override fun onResume() {
        super.onResume()
        //initObservers()
    }

    private fun initObservers() {
        Log.i("bugfix", "ForecastActivity: initing observers")

        forecastViewModel.forecastLinesLive.observe(this, Observer {
            if (it != null) {
                val linesQueue = it.getData()// todo: сделать обертку ForecastLines, из которой доставать очередь?
                fillForecast(linesQueue) // вот тут проблема! Обнуление происходит в этой строчке

                forecastViewModel.showLines()
            }
        })

        forecastViewModel.buttonLocationNameLive.observe(this, androidx.lifecycle.Observer {
            val buttonSelectLocation = findViewById<Button>(R.id.btn_select_location)
            buttonSelectLocation.text = it
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // todo: exit to main
    }

    override fun onStart() {
        super.onStart()
        initLocationBroadcastListener()
        initObservers()
        forecastViewModel.showLines()
    }

    private fun updateLocationFromIntent (){
        /** если есть, принимаем даныне по местоположению из вывавшего интента  */
        var lastUsersLocation: ForecastLocation? = null
        val catchLocation = intent
        if (catchLocation.hasExtra("latitude") and catchLocation.hasExtra("longitude")) {
            val latitude: Double = catchLocation.getDoubleExtra("latitude", 0.0)
            val longitude: Double = catchLocation.getDoubleExtra("longitude", 0.0)
            lastUsersLocation = ForecastLocation(CURRENT_POSITION, latitude = latitude, longitude = longitude)
            forecastViewModel.updateUserLocation(lastUsersLocation)
        }
    }

    private fun initLocationBroadcastListener() {
        val locationBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) { // обработка интента
                if (intent.hasExtra("location")) { // если в сообщении есть геолокация            Log.i("bugfix", "ActivityForecast: LastLocation is empty ")
                    Log.i("bugfix", "ActivityForecast: broadcast listener has new location ")

                    val currentUserLocation = (intent.extras!!["location"] as Location?)?.let {
                        LocationMapper.androidLocationToForecastLocation(it)
                    }
                    currentUserLocation?.let { forecastViewModel.updateUserLocation(it) }
                } else Log.i("bugfix", "ActivityForecast: receiver has broadcast but no location ")
            }
        }
        val locationIntentFilter =
            IntentFilter(BROADCAST_ACTION) // прописываем интент фильтр для слушателя
        registerReceiver(locationBroadcastReceiver, locationIntentFilter) // регистрируем слушатель
    }

    private fun fillForecast(forecastLines: Queue<ForecastLine>) {
        Log.i("bugfix", "ForecastActivity: filling the table, queue size = ${forecastLines.size}")
        viewToBeFiled.removeAllViews()
        fillTitle()
//        // todo: move to separate class when MVVM realization
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
        forecastViewModel.updateForecastShownStatus(true)
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