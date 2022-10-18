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
import com.example.racertimer.forecast.data.LocationsListRepository
import com.example.racertimer.forecast.domain.interfaces.ChooseNameFromListInterface
import com.example.racertimer.forecast.domain.interfaces.UpdateForecastLinesInterface
import com.example.racertimer.forecast.domain.models.ForecastLine
import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.useCases.*
import com.example.racertimer.forecast.presentation.mappers.LocationMapper
import kotlinx.android.synthetic.main.activity_forecast2.*
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
            Log.i("bugfix", "ActivityForecast: forecast queue is null = ${queueForecastLines == null}, size = ${queueForecastLines?.size} ")
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
                //button_update_forecast.text = BUTTON_NAME_CURRENT
            } else {
                if (listLocations != null)
                    forecastLocation = chooseLocationByNameUseCase.execute(listLocations, name)

//                if (forecastLocation != null) {
//                    forecastStatusManager.updateLocation(forecastLocation)
//                    button_update_forecast.text = forecastLocation.name
//                }
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
    // todo: нужно обработать выбор в меню текущей локации: если выбрана текущая, нужно проверить -
    //  есть ли она, если есть - обновляем, если нет - выставляем флаг ожидания (при обновлении локации
    //  автоматически запрашиваем данные и выводим прогноз

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast2)

        val buttonSelectLocation = findViewById<Button>(R.id.btn_select_location)
        buttonSelectLocation.setOnClickListener(View.OnClickListener {
            Log.i("bugfix", "ActivityForecast: the button was pressed")
            //val layoutInflater = layoutInflater
            val locationsList = openLocationsListUseCase.execute()
            if (locationsList != null)
                selectLocationPopupUseCase.execute(buttonSelectLocation, locationsList)
        })

        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        val listView = findViewById<LinearLayout>(R.id.viewToBeFiled)

        currentUserLocation = updateLocationFromIntent()

        // todo: нужно сделать обновление информации прогноза только если таблица не обновлена либо если с момента обновления прошло много времени
        // в рамках MVVM

        updateViewWhenForecastOpening()

        // todo: in release remove fun firstTimeLaunch and locations coordinates hardcode below:
        /**
         * для создания .bin файла запускаем метод с вбитыми координатами точек. В нормльном состоянии
         * финального релиза этот участок программы не нужен
         * //firstTimeLaunch(saveLocationsListUseCase)
         */
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // todo: exit to main
    }

    override fun onResume() {
        super.onResume()
//        if (!isForecastAlreadyShown) {
//            if (chosenLocationToShowForecast != null) {
//                val currentForecastLocation: ForecastLocation = chosenLocationToShowForecast as ForecastLocation
//                updateForecast(currentForecastLocation)
//            }
//        }
    }

    private fun updateViewWhenForecastOpening(){
        // отработка после открытия - берем последнюю локацию (например - текущую, или любую из сохраненных)
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
            else forecastLocation = currentUserLocation
        } else {
            val locationsList = openLocationsListUseCase.execute()
            if (locationsList != null) {
                forecastLocation =
                    chooseLocationByNameUseCase.execute(locationsList, lastLocationName)
            }
        }
        Log.i("bugfix", "ActivityForecast: chosen last location name = ${forecastLocation?.name} ")
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
        initBroadcastListener()
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

//    private fun updateForecast(forecastLocation: ForecastLocation): Boolean {
//        //var forecastStrings = Queue<String>//: Queue<String> = updateForecastUseCase.execute(forecastLocation)
//        Log.i("bugfix", "ActivityForecast: updating forecast by location = ${forecastLocation.name} ")
//
//// далее: нужно проверить сохранение. Вызываем saveLast с локацией CURRENT_POSITION, проверяем через логи.
//// то же самое при загрузке  - логами смотрим что тут лежит
//        if (locationUpdateAwaiting)
//            saveLastLocationUseCase.execute(forecastLocation)
//        // todo: добавить обработку currentPositionIsShowh - должно срабатывать только когда выбрана никакая или текущая позиция
//        var result = false
//        return result
//    }

    private fun initBroadcastListener() {
        val locationBroadcastReceiver = object : BroadcastReceiver() {
            // создаем broadcastlistener
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
            if (currentLine != null) {
                fillForecastLine(
                    lineToFill = item,
                    dateAndTime = currentLine.time,
                    temperature = currentLine.temperature,
                    windSpeed = currentLine.windSpeed,
                    windGust = currentLine.windGust,
                    windDir = currentLine.windDir
                )
            }
        }
        isForecastAlreadyShown = true
    }

    private fun fillTitle() {
        val item = layoutInflater.inflate(R.layout.forecast_line, viewToBeFiled, false)
        fillForecastLine(
            lineToFill = item,
            dateAndTime = "date",
            temperature = "temp",
            windSpeed = "wind",
            windGust = "gust",
            windDir = "dir"
        )
    }

    private fun fillForecastLine(
        lineToFill: View,
        dateAndTime: String,
        temperature: String,
        windSpeed: String,
        windGust: String,
        windDir: String) {

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