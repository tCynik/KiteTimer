package com.tcynik.racertimer.forecast.data.parsers

import com.tcynik.racertimer.forecast.domain.models.ForecastLine
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ParserJsonToQueueLines {
    fun execute (jSon: JSONObject): Queue<ForecastLine> {
        val jsonArray = jSon.getJSONArray("list")
        var index = 0
        val queue: Queue<ForecastLine> = LinkedList<ForecastLine>()
        while (index < jsonArray.length()){
            val currentJson: JSONObject = jsonArray.getJSONObject(index)
            index++
            val timeFormat = SimpleDateFormat("d MMM HH:mm")
            val time = currentJson.getLong("dt") * 1000
            val temperature = currentJson.getJSONObject("main").getInt("temp")

            val windJson = currentJson.getJSONObject("wind")
            val windSpeed = windJson.getInt("speed")
            val windDir = windJson.getInt("deg")
            val windGust = windJson.getInt("gust")

            val currentLine = ForecastLine(
                time = time,
                temperature = temperature.toString(),
                windSpeed = windSpeed.toString(),
                windDir = windDir.toString(),
                windGust = windGust.toString()
            )
            queue.add(currentLine)
        }
        return queue
    }
}