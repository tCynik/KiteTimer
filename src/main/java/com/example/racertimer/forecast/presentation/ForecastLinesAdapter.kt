package com.example.racertimer.forecast.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.racertimer.databinding.ActivityForecastBinding
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
            forecastStringWind.text = line.windSpeed
            forecastStringGust.text = line.windGust
            forecastStringDir.text = line.windDir
        }
    }

    override fun getItemCount(): Int = lines.size
}