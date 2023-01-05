package com.tcynik.racertimer.forecast.presentation.models_mappers

import com.tcynik.racertimer.forecast.domain.models.ForecastLine
import java.util.*

class ForecastLinesData (private val linesData: Queue<ForecastLine>) {

    fun getData(): Queue<ForecastLine>{
        val exportData: Queue<ForecastLine> = LinkedList<ForecastLine>()
        linesData.forEach { e -> exportData.add(e) }
        return exportData
    }
}