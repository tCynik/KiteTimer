package com.example.racertimer.forecast.data.urlequest

import org.json.JSONObject

interface ResultJsonInterface {
    fun gotResult(jsonOnObject: JSONObject?);

    fun errorOccurs(error: String)
}