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
import com.example.racertimer.Instruments.WindProvider
import com.example.racertimer.R
import com.example.racertimer.databinding.ActivityForecastBinding
import com.example.racertimer.forecast.domain.models.ForecastLine
import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.use_cases.SelectLocationByPopupUseCase
import com.example.racertimer.forecast.presentation.interfaces.SelectLocationInterface
import com.example.racertimer.forecast.presentation.models_mappers.LocationMapper
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val CURRENT_POSITION = "current position"//"DEFAULT"
const val BROADCAST_ACTION =
    "com.example.racertimer.action.new_location" // значение для фильтра приемника

class ActivityForecast : AppCompatActivity() {
    private var locationBroadcastReceiver: BroadcastReceiver? = null

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
        layoutDataBinding.viewToBeFiled.layoutManager = LinearLayoutManager(this)
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
         * для создания .bin файла запускаем метод с вбитыми координатами точек. В нормальном состоянии
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
        } else
            if (catchLocation.hasExtra("no location")) {
                Toast.makeText(this, "No GPS location!", Toast.LENGTH_LONG).show()
                // todo: open last location?
                }
    }

    private fun initLocationBroadcastListener() {
        Log.i("bugfix: ActivityForecast", "init the broadcast listener")
        val locationBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) { // обработка интента
                Log.i("bugfix: ActivityForecast", "broadcast listener received some intent")
                if (intent.hasExtra("location")) { // если в сообщении есть геолокация
                    Log.i("bugfix: ActivityForecast", "broadcast listener has a new location")
                    val currentUserLocation = (intent.extras!!["location"] as Location?)?.let {
                        LocationMapper.androidLocationToForecastLocation(it)
                    }
                    currentUserLocation?.let { forecastViewModel.updateUserLocation(it) }
                }
            }
        }
        val locationIntentFilter =
            IntentFilter(BROADCAST_ACTION) // прописываем интент фильтр для слушателя
        val a = registerReceiver(locationBroadcastReceiver, locationIntentFilter) // регистрируем слушатель
        Log.i("bugfix: ActivityForecast", "registration of the broadcast receiver = $a")
    }

// todo: make screen rotation
    private fun fillTitle(viewLayout: LinearLayout) {
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