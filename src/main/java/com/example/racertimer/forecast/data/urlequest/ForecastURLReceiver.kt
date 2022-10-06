package com.example.racertimer.forecast.data.urlequest

import com.example.racertimer.forecast.domain.models.ForecastLine
import com.example.racertimer.forecast.domain.models.ForecastLocation
import java.util.*

class ForecastURLReceiver {

    fun update(forecastLocation: ForecastLocation): Queue<ForecastLine> {
        val latitude = forecastLocation.latitude
        val longitude = forecastLocation.longitude
        val urlRequestManager = URLRequestManager()
        return urlRequestManager.makeRequest(latitude = latitude, longitude = longitude)
    }
}

/**
 * ЮРЛ запрос выполняется асинхронно. выходит так, что пока что:
 * отправляем запрос в отдельном треде (пока изнутри Java класса JavaForecastManager)
 * При возвращении ответа через хендлер вызывается коллбек (интерфейс)
 * Этим колбеком инициируется изменение вьюшки
 *
 * Таким образом, необходимо иметь менеджер, который имеет связь с обновлением UI (создает интерфейс),
 * и одновременно инициирует запрос (передает этот интерейс для обратной связи)
 */