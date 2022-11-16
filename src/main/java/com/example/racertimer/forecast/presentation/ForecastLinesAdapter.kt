package com.example.racertimer.forecast.presentation

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.racertimer.R
import com.example.racertimer.databinding.ForecastLineBinding
import com.example.racertimer.forecast.domain.models.ForecastLine
import java.text.SimpleDateFormat

class ForecastLinesAdapter:  RecyclerView.Adapter<ForecastLinesAdapter.LinesViewHolder>() {

    class LinesViewHolder (val binding: ForecastLineBinding): RecyclerView.ViewHolder(binding.root)

    var lines: List<ForecastLine> = emptyList()
    set(newValue) {
        field = newValue
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ForecastLineBinding.inflate(inflater, parent, false)
        return LinesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LinesViewHolder, position: Int) {
        val line: ForecastLine = lines[position]
        with(holder.binding) {
            val timeFormat = SimpleDateFormat("d MMM HH:mm")
            val dateTimeString: String = timeFormat.format(line.time)
            forecastStringTime.text = dateTimeString // поля, созданные биндингом из полей лайаута
            forecastStringTemp.text = line.temperature
            forecastStringWind.text = (line.windSpeed + "/")
            forecastStringGust.text = line.windGust
            forecastStringDir.text = line.windDir

            coloringDayNight(isItDay = checkDaytime(line.time),
                forecastStringTime,
                forecastStringTemp,
                forecastStringWind,
                forecastStringGust,
                forecastStringDir)
        }
    }

    override fun getItemCount(): Int = lines.size

    private fun checkDaytime(time: Long): Boolean {
        val timeFormat = SimpleDateFormat("HH")
        val timeHour = timeFormat.format(time).toInt()
        Log.i("bugfix", "ForecastLinesUpdater: time = $timeHour, is it day = ${timeHour in 8..19}")
        return timeHour in 8..19
    }

    private fun coloringDayNight(isItDay: Boolean, vararg views: TextView) {
        if (isItDay)
            views.forEach { e ->
                e.setTextColor(android.graphics.Color.BLACK)
                e.setBackgroundColor(android.graphics.Color.GRAY)
            }
        else
            views.forEach { e ->
                e.setTextColor(android.graphics.Color.GRAY)
                e.setBackgroundColor(R.color.color_primary)
            }
    }
}