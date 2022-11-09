package com.example.racertimer.forecast.presentation.models_mappers

import android.util.Log
import com.example.racertimer.forecast.domain.models.ForecastLine
import java.util.*

class ForecastLinesData (private val linesData: Queue<ForecastLine>) {
    init {
        Log.i("bugfix", "ForecastLinesData: created new instance")
    }

    fun getData(): Queue<ForecastLine>{
        val exportData: Queue<ForecastLine> = LinkedList<ForecastLine>()
        linesData.forEach { e -> exportData.add(e) }
        return exportData
    }
}