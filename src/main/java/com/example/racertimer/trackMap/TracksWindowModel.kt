package com.example.racertimer.trackMap

import android.view.View

class TracksWindowModel {
    private var isWindowFormed = false
    private var height = 0
    private var width = 0

    fun setSizes(height: Int, width: Int){
        this.height = height
        this.width = width
        isWindowFormed = true
    }

    fun setSizesByView(view: View) {
        this.height = view.height
        this.width = view.width
        isWindowFormed = true
    }

    fun getCenterX() : Int {return (width/2).toInt()}
    fun getCenterY() : Int {return (height/2).toInt()}
}