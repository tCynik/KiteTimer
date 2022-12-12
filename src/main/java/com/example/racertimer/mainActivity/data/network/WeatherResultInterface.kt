package com.example.racertimer.mainActivity.data.network

interface WeatherResultInterface {
    fun gotResult(windDir: Int)
    fun gorError(error: String)
}