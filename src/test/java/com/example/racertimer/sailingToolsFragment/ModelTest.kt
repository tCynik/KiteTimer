package com.example.racertimer.sailingToolsFragment

import junit.framework.Assert.assertEquals
import org.junit.Test
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class ModelTest {
    private var lastVelocity = 0
    private var windDir = 10000
    private var lastBearing = 10000
    private var maxVelocity = 0
    private var lastVMG = 0
    private var courseToWind = 0
    private var maxUpwindVMG = 0
    private var maxDownwindVMG = 0
// VELOCITY, MAX_VELOCITY, BEARING, COURSE_TO_WIND, VMG, MAX_UPWIND, MAX_DOWNWIND

    var mapOfUpdaters = mutableMapOf<Fields, FieldUpdater>()
    init {
        mapOfUpdaters = fillMapUpdaters()
    }
    val model = Model(mapOfUpdaters)


    fun fillMapUpdaters() : MutableMap<Fields, FieldUpdater> {
        var fieldUpdater = FieldUpdater { value -> lastVelocity = value }
        mapOfUpdaters[Fields.VELOCITY] = fieldUpdater

        fieldUpdater = FieldUpdater { value -> maxVelocity = value }
        mapOfUpdaters[Fields.MAX_VELOCITY] = fieldUpdater

        fieldUpdater = FieldUpdater { value -> lastVMG = value }
        mapOfUpdaters[Fields.VMG] = fieldUpdater

        fieldUpdater = FieldUpdater { value -> maxUpwindVMG = value }
        mapOfUpdaters[Fields.MAX_UPWIND] = fieldUpdater

        fieldUpdater = FieldUpdater { value -> maxDownwindVMG = value }
        mapOfUpdaters[Fields.MAX_DOWNWIND] = fieldUpdater

        return mapOfUpdaters
    }

    @Test
    fun velocityChanging () {
        model.onLocationChanged(5, 0)
        model.onLocationChanged(10, 0)
        assertEquals(lastVelocity, 36)
    }

    @Test
    fun maxVelocity() {
        model.onLocationChanged(10, 0)
        model.onLocationChanged(5, 0)
        assertEquals(36, maxVelocity)
    }

    @Test
    fun currentVelocity() {
        model.onLocationChanged(10, 0)
        model.onLocationChanged(5, 0)
        assertEquals(18, lastVelocity)
    }


    @Test
    fun VMGDirectAngle() {
        model.onWindChanged(270)
        model.onLocationChanged(10, 0)
        assertEquals(0, lastVMG)
    }

    @Test
    fun VMG45degree() {
        model.onWindChanged(90)
        model.onLocationChanged(10, 45)
        val answer = (36 * (sin(45* PI/180))).toInt()
        assertEquals(answer, lastVMG)
    }

    @Test
    fun upwind() {
        model.onWindChanged(90)
        model.onLocationChanged(10, 60)
        assertEquals(31, maxUpwindVMG)
    }

    @Test
    fun downwind() {
        model.onWindChanged(90)
        model.onLocationChanged(5, 240)
        assertEquals(-15, maxDownwindVMG)
    }

    @Test
    fun maxUpwind () {
        model.onWindChanged(90)
        model.onLocationChanged(10, 60)
        model.onLocationChanged(5, 60)
        assertEquals(31, maxUpwindVMG)
    }

    @Test
    fun maxDownwind () {
        model.onWindChanged(90)
        model.onLocationChanged(10, 240)
        model.onLocationChanged(5, 240) // look at fun downwind() {}: location (5, 240) -> -15

        assertEquals(-31, maxDownwindVMG)
    }

    @Test
    fun maxDownwindNotUpwind () {
        model.onWindChanged(90)
        model.onLocationChanged(10, 240)
        model.onLocationChanged(5, 240)

        assertEquals(0, maxUpwindVMG)
    }

    @Test
    fun VMGnewWind90() {
        model.onWindChanged(90)
        model.onLocationChanged(10, 45)
        model.onWindChanged(180)
        assertEquals(0, maxUpwindVMG)
    }

    @Test
    fun VMGWindMinimumChanging() {
        model.onWindChanged(90)
        model.onLocationChanged(10, 60)
        model.onWindChanged(91)
        assertEquals(30, maxUpwindVMG)
    }

    @Test
    fun upwindWindSinMinimumChanging() {
        model.onWindChanged(90)
        model.onLocationChanged(10, 60)
        model.onWindChanged(94)
        assertEquals(29, maxUpwindVMG)
    }

    @Test
    fun maxUpwindSinMaximumChanging() {
        model.onWindChanged(90)
        model.onLocationChanged(10, 60)
        model.onWindChanged(179)
        assertEquals(1, maxUpwindVMG)
    }

    @Test
    fun downwindMinimumChanging() {
        model.onWindChanged(90)
        model.onLocationChanged(10, 240)
        model.onWindChanged(91)
        assertEquals(-30, maxDownwindVMG)
    }

    @Test
    fun downwindSinMinimumChanging() {
        model.onWindChanged(90)
        model.onLocationChanged(10, 240)
        model.onWindChanged(93)
        assertEquals(-30, maxDownwindVMG)
    }

    @Test
    fun maxDownwindSinMaximumChanging() {
        model.onWindChanged(90)
        model.onLocationChanged(10, 240)
        model.onWindChanged(179)
        assertEquals(-1, maxDownwindVMG)
    }


    @Test
    fun maxDownwindSinMediumChanging() {
        model.onWindChanged(90)
        model.onLocationChanged(10, 240)
        model.onWindChanged(30)
        assertEquals(-16, maxDownwindVMG)
    }

}