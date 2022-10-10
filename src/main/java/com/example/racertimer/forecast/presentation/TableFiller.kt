package com.example.racertimer.forecast.presentation

import android.view.View
import android.widget.LinearLayout
import com.example.racertimer.forecast.domain.models.ForecastLine
import java.util.*

class TableFiller(lineToFill: LinearLayout, layoutToFill: LinearLayout) {
    fun execute (forecastLinesArray: Queue<ForecastLine>) {
        while (!forecastLinesArray.isEmpty()) {
            val item =
            val currentLine = forecastLinesArray.poll()
            item.
            if (currentLine!=null) fillForecastTable(currentLine)
        }

    }
    // pass the layout inti constructor, and queue<ForecastLine> interface (to UpdateForecast both).
    // here is layout filling method (st.289 in old activity)
}