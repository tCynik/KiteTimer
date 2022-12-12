package com.example.racertimer.forecast.data.network.retrofit

import com.example.racertimer.forecast.data.network.retrofit.response.TimeHourForecast
import com.example.racertimer.forecast.domain.models.ForecastLine
import java.util.*

class ResponseMapper {
    fun transferToLines(linesList: List<TimeHourForecast>): Queue<ForecastLine> {
        var result: Queue<ForecastLine> = LinkedList()
        for(line in linesList) {
            result.add(gsonToForecastLine(line))
        }
        return result
    }

    fun runHZ(){}

    private fun gsonToForecastLine(gsonLine: TimeHourForecast): ForecastLine {
        return ForecastLine(
            time = gsonLine.daytime*1000,
            temperature = gsonLine.forecastMain.temp.toInt().toString(),
            windSpeed = gsonLine.forecastWind.windSpeed.toInt().toString(),
            windDir = gsonLine.forecastWind.windDir.toString(),
            windGust = gsonLine.forecastWind.windGust.toInt().toString()
        )
    }
}