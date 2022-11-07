package com.example.racertimer.forecast.domain

import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.use_cases.UpdateForecastUseCase

/**
 * Менеджер, который отслеживает состояние прогноза (обновлен или нет, отображен или нет),
 * хранит текущую локацию, а так же отвечает за своевременное обновление прогноза через время.
 * Изменить:
 * 1. состояние прогноза должно храниться в VM (непосредственно или во вложенном менеджере)
 * 2. обновление через время надо сделать там же, через короутину
 *
 * короче, надо тупо создавать данный менеджер в VM
 */

class ForecastShownManager(private val updateForecastUseCase: UpdateForecastUseCase) {
    // todo: удаляем, т.к. этот функционал есть во VM. Либо выделить из VM в отдельный (этот) класс
    private var currentLocationToShow: ForecastLocation? = null
    private var currentUserLocation: ForecastLocation? = null
    private var forecastShown = false

    fun updateUserLocation(forecastLocation: ForecastLocation) {
        currentUserLocation = forecastLocation
        // todo: если нужно было показать текущую локазию, выводим ее
    }

    fun updateLocationToShow(forecastLocation: ForecastLocation?) {
        currentLocationToShow = forecastLocation
        if (forecastLocation != null) {
            updateForecastUseCase.execute(forecastLocation)
        } else updateByUserPosition()
    }

    fun updateUrlResponseStatus(isForecastShown: Boolean) {
        forecastShown = isForecastShown
        if (isForecastShown) {
            runAutoUpdateTimeout()
        } else {
            // todo: значит, прогноз не отобразился (нет доступа к сети?) - тогда нужно через какое-то время пытаться снова
        }
    }

    private fun runAutoUpdateTimeout() {
        // todo: запускаем в коурутине таймаут, после которого на локации обновлем прогноз.
        // если работает предыдущий таймаут, его отменяем и запускаем новый
    }

    private fun updateByUserPosition() {
        if (currentUserLocation != null) updateForecastUseCase.execute(currentUserLocation!!)
        else {
            //todo: обработка не вывода текущей локации с тем, чтобы вывести когда получим точку
        }
    }
}