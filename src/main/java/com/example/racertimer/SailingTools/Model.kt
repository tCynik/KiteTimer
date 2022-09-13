package com.example.racertimer.SailingTools

import com.example.racertimer.Instruments.CoursesCalculator

class Model {
    private var windDir = 10000
    private var lastBearing = 10000
    private var maxVelocity = 0
    private var lastVMG = 0
    private var courseToWind = 0

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
            updateMaxVelocity(maxVelocity)
        }
    }

    private fun checkCourseToWind() {
        if (lastBearing != 10000 && windDir != 10000) {
            courseToWind = CoursesCalculator.calcWindCourseAngle(windDir, lastBearing);
            updateCourseToWind(courseToWind)
        }
    }

    private fun checkVMG(velocity: Int, bearing: Int) {
        lastVMG = CoursesCalculator.VMGByWindBearingVelocity(windDir, bearing, velocity);
        updateVmg(lastVMG)
        if (lastVMG > )
    }

}