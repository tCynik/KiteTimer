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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.racertimer.R
import com.example.racertimer.databinding.ActivityForecastBinding
import com.example.racertimer.forecast.domain.models.ForecastLine
import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.use_cases.SelectLocationByPopupUseCase
import com.example.racertimer.forecast.presentation.interfaces.SelectLocationInterface
import com.example.racertimer.forecast.presentation.models_mappers.LocationMapper
import kotlinx.android.synthetic.main.activity_forecast2.*
import kotlinx.android.synthetic.main.forecast_line.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

private const val CURRENT_POSITION = "current position"//"DEFAULT"
private const val BUTTON_NAME_CURRENT = "current_position"
private const val EMPTY = ""
const val BROADCAST_ACTION =
    "com.example.racertimer.action.new_location" // значение для фильтра приемника

class ActivityForecast : AppCompatActivity() {
    private val forecastViewModel by viewModel<ForecastViewModel>()
    private lateinit var recyclerAdapter: ForecastLinesAdapter

    private val locationSelector = object: SelectLocationInterface {
        override fun onLocationSelected(forecastLocation: ForecastLocation?) {
            if (forecastLocation == null) forecastViewModel.updateForecastByUserLocation()
            else {
                forecastViewModel.updateForecastByLocation(forecastLocation)
            }
        }
    }
    private val selectLocationByPopupUseCase = SelectLocationByPopupUseCase(
        this,
        locationSelector = locationSelector)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutDataBinding: ActivityForecastBinding = DataBindingUtil.setContentView(this, R.layout.activity_forecast)
        layoutDataBinding.lifecycleOwner = this
        layoutDataBinding.viewmodel = forecastViewModel

        recyclerAdapter = ForecastLinesAdapter()
        val layoutManager = LinearLayoutManager(this)
        layoutDataBinding.viewToBeFiled.layoutManager = layoutManager
        layoutDataBinding.viewToBeFiled.adapter = recyclerAdapter

        val buttonSelectLocation = layoutDataBinding.btnSelectLocation
        buttonSelectLocation.setOnClickListener(View.OnClickListener {
            val locationsList = forecastViewModel.getLocationsList()
            if (locationsList != null)
                selectLocationByPopupUseCase.execute(buttonSelectLocation, locationsList)
        })

        fillTitle(layoutDataBinding.titleLayout)
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
    }

    private fun initObservers() {
        forecastViewModel.forecastLinesLive.observe(this, Observer {
            if (it != null) {
                val forecastLinesData = it
                val forecastLines = forecastLinesData.getData() as List<ForecastLine>
                recyclerAdapter.lines = forecastLines
                // TODO: добавить заголовок таблицы
            }
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

// todo: make screen rotation
    private fun fillTitle(viewLayout: LinearLayout) {
        //val viewForTitle = findViewById<LinearLayout>(R.layout.forecast_line)
        val item = layoutInflater.inflate(R.layout.forecast_line, viewLayout, false)
        item.findViewById<TextView>(R.id.forecast_string_time).text = "date"
        item.findViewById<TextView>(R.id.forecast_string_temp).text = "temp"
        item.findViewById<TextView>(R.id.forecast_string_wind).text = "wind"
        item.findViewById<TextView>(R.id.forecast_string_gust).text = "gust"
        item.findViewById<TextView>(R.id.forecast_string_dir).text = "dir"
        viewLayout.addView(item)
    }
//
//    private fun fillNoData() {
//        val item = layoutInflater.inflate(R.layout.forecast_line, viewToBeFiled, false)
//        fillForecastLine(item, "", false, "NO", "DATA", "", "")
//    }
}