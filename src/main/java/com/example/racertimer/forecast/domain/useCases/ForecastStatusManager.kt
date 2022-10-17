package com.example.racertimer.forecast.domain.useCases

import com.example.racertimer.forecast.domain.models.ForecastLocation

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