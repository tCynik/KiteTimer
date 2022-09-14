package com.example.racertimer.SailingTools

import com.example.racertimer.Instruments.CoursesCalculator

class Model(private val fieldUpdaters: Map<Fields, FieldUpdater>) {
    private var windDir = 10000
    private var lastBearing = 10000
    private var maxVelocity = 0
    private var lastVMG = 0
    private var maxVMG = 0
    private var courseToWind = 0
    private var maxUpwindVMG = 0
    private var maxDownwindVMG = 0


    fun onLocationChanged(velocity: Int, bearing: Int) {
        lastBearing = bearing
        checkMaxVelocity(velocity)
        checkCourseToWind()
        checkVMG(velocity, bearing)
    }

    fun onWindChanged(windDir: Int) {
        this.windDir = windDir
        checkCourseToWind()
    }

    private fun checkMaxVelocity(velocity: Int) {
        if (velocity > maxVelocity) {
            maxVelocity = velocity
            fieldUpdaters[Fields.MAX_VELOCITY]?.updateIntField(maxVelocity)
        }
    }

    private fun checkCourseToWind() {
        if (lastBearing != 10000 && windDir != 10000) {
            courseToWind = CoursesCalculator.calcWindCourseAngle(windDir, lastBearing);
            fieldUpdaters[Fields.COURSE_TO_WIND]?.updateIntField(courseToWind)
        }
    }

    private fun checkVMG(velocity: Int, bearing: Int) {
        val currentVMG = CoursesCalculator.VMGByWindBearingVelocity(windDir, bearing, velocity);
        if (lastVMG != currentVMG) {
            lastVMG = currentVMG
            fieldUpdaters[Fields.VMG]?.updateIntField(lastVMG)
        }
        if (currentVMG > maxVMG) {
            maxVMG = currentVMG
            fieldUpdaters[Fields.MAX_VMG]?.updateIntField(maxVMG)
        }
        if (lastVMG > maxUpwindVMG) {
            maxUpwindVMG = lastVMG
            fieldUpdaters[Fields.MAX_UPWIND]?.updateIntField(maxUpwindVMG)
        }
        if (lastVMG < maxDownwindVMG) {
            maxDownwindVMG = lastVMG
            fieldUpdaters[Fields.MAX_DOWNWIND]?.updateIntField(maxDownwindVMG)
        }
    }
}