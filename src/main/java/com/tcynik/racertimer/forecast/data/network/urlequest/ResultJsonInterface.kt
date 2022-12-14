package com.tcynik.racertimer.forecast.data.network.urlequest

import org.json.JSONObject

interface ResultJsonInterface {
    fun gotResult(jsonOnObject: JSONObject?);

    fun errorOccurs(error: String)
}