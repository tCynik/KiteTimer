package com.example.racertimer.sailingToolsFragment

import com.example.racertimer.Instruments.CoursesCalculator
import kotlin.math.abs
import kotlin.math.sin

class Model(private val fieldUpdaters: Map<Fields, FieldUpdater>) {
    private var lastVelocity = 0
    private var windDir = 10000
    private var lastBearing = 10000
    private var maxVelocity = 0
    private var lastVMG = 0
    private var courseToWind = 0
    private var maxUpwindVMG = 0
    private var maxDownwindVMG = 0


    fun onLocationChanged(velocityMpS: Int, bearing: Int) {
        var isVMGParamsChanged = false
        if (checkVelocityChanged(velocityMpS)) {
            checkMaxVelocity(lastVelocity)
            fieldUpdaters[Fields.VELOCITY]?.updateIntField(lastVelocity)
            isVMGParamsChanged = true
        }
        if (checkBearingChanged(bearing)) {
            fieldUpdaters[Fields.BEARING]?.updateIntField(lastBearing)
            checkCourseToWind()
        } else isVMGParamsChanged = false
        if (isVMGParamsChanged) checkVMG(velocityMpS, bearing)
    }

    fun onWindChanged(windDir: Int) {
        if (windDir != this.windDir) {
            val windDiff = abs(this.windDir - windDir)
            if (windDiff > 90) setMaximums(maxVelocity, 0, 0)
            else {
                val reduceRate = sin(windDiff.toDouble()) // TODO: проверить правильность расчета
                setMaximums(maxVelocity, (maxUpwindVMG*reduceRate).toInt(), (maxDownwindVMG*reduceRate).toInt() )
            }

            this.windDir = windDir
            checkCourseToWind()
            checkVMG(lastVelocity, lastBearing)
        }
    }

    fun setMaximums(maxVelocity: Int, maxUpwindVMG: Int, maxDownwindVMG: Int) {
        if (maxVelocity != this.maxVelocity) {
            this.maxVelocity = maxVelocity
            fieldUpdaters[Fields.MAX_VELOCITY]?.updateIntField(this.maxVelocity)
        }

        this.maxUpwindVMG = maxUpwindVMG
        fieldUpdaters[Fields.MAX_UPWIND]?.updateIntField(this.maxUpwindVMG)

        this.maxDownwindVMG = maxDownwindVMG
        fieldUpdaters[Fields.MAX_DOWNWIND]?.updateIntField(this.maxDownwindVMG)
    }

    private fun checkVelocityChanged(velocityMpS: Int): Boolean {
        val velocityKmH = (velocityMpS * 3.6).toInt()
        return if (velocityKmH != lastVelocity) {
            lastVelocity = velocityKmH
            true
        } else false
    }

    private fun checkBearingChanged(bearing: Int): Boolean {
        return if (bearing != lastBearing) {
            lastBearing = bearing
            true
        } else false
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