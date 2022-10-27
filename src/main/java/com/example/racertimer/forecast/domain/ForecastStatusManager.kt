package com.example.racertimer.forecast.domain

import com.example.racertimer.forecast.domain.models.ForecastLocation
import com.example.racertimer.forecast.domain.useCases.UpdateForecastUseCase

/**
 * Менеджер, который отслеживает состояние прогноза (обновлен или нет, отображен или нет),
 * хранит текущую локацию, а так же отвечает за своевременное обновление прогноза через время.
 * Изменить:
 * 1. состояние прогноза должно храниться в VM (непосредственно или во вложенном менеджере)
 * 2. обновление через время надо сделать там же, через короутину
 *
 * короче, надо тупо создавать данный менеджер в VM
 */

class ForecastStatusManager(private val updateForecastUseCase: UpdateForecastUseCase) {
    // todo: как-то нужно будет сохранять этот обьект чтобы не перегружать каждый раз прогноз
    private var currentLocation: ForecastLocation? = null
    private var forecastShown = false

    fun updateLocation(forecastLocation: ForecastLocation?) {
        currentLocation = forecastLocation
        if (forecastLocation != null) {
            updateForecastUseCase.execute(forecastLocation)
        }
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
}