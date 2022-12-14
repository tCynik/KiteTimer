package com.tcynik.racertimer.main_activity.data.network

interface WeatherResultInterface {
    fun gotResult(windDir: Int)
    fun gorError(error: String)
}