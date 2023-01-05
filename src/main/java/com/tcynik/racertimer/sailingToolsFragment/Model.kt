package com.tcynik.racertimer.sailingToolsFragment

import com.tcynik.racertimer.main_activity.domain.CoursesCalculator
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

class Model(private val fieldUpdaters: Map<Fields, FieldUpdater>) {

    private val PRIORITY_BEEP = 2 // звуки индикации положения VMG


//    private val voiceover: BeepSounds = BeepSounds(this)

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
            fieldUpdaters[Fields.PERCENT_VELOCITY]?.updateIntField(checkPercentVelocity())
            isVMGParamsChanged = true
        }
        if (checkBearingChanged(bearing)) {
            fieldUpdaters[Fields.BEARING]?.updateIntField(lastBearing)
            checkCourseToWind()
        } else isVMGParamsChanged = false
        if (isVMGParamsChanged) checkVMG(lastVelocity, bearing)
    }


    fun onWindChanged(windDir: Int) {
        if (windDir != this.windDir) {
            val windDiff = abs(this.windDir - windDir)
            if (windDiff >= 90) setMaximums(maxVelocity, 0, 0)
            else {
                val reduceRate = sin(windDiff * PI / 180)
                var reduceUpwind = (maxUpwindVMG * reduceRate).toInt()
                if (reduceUpwind < 1 ) reduceUpwind = 1
                var reduceDownwind = abs(maxDownwindVMG * reduceRate).toInt()
                if (reduceDownwind < 1) reduceDownwind = 1

                setMaximums(maxVelocity,
                    (maxUpwindVMG - reduceUpwind),
                    (maxDownwindVMG + reduceDownwind) )
            }

            this.windDir = windDir
            checkCourseToWind()
            checkVMG(lastVelocity, lastBearing)
        }
    }

    private fun reduceMaximumsVMG (rate: Int) {

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

    private fun checkPercentVelocity(): Int {
        return if (maxVelocity != 0)
            (lastVelocity * 100 / maxVelocity).toInt()
        else 0
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
            courseToWind = com.tcynik.racertimer.main_activity.domain.CoursesCalculator.calcWindCourseAngle(windDir, lastBearing);
            fieldUpdaters[Fields.COURSE_TO_WIND]?.updateIntField(courseToWind)
        }
    }

    private fun checkVMG(velocity: Int, bearing: Int) {
        val currentVMG = com.tcynik.racertimer.main_activity.domain.CoursesCalculator.VMGByWindBearingVelocity(windDir, bearing, velocity);
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