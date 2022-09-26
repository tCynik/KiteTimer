package com.example.racertimer.sailingToolsFragment

import android.content.Context
import android.util.Log
import com.example.racertimer.multimedia.BeepSounds
import kotlin.math.abs

class VmgBeeper(context: Context) {
    private val voiceover = BeepSounds(context)
    private var vmgBeeperSensitivity = 20
    private var velocity = 0
    private var velocityMadeGood = 0
    private var lastVMG = 0
    private var isRaceStarted = false
    private var bestUpwind = 0
    private var bestDownwind = 0
    private var isVoiceoverMuted = false

    init {
        Log.i("bugfix", "instance of beeper was created")
    }

    fun updateVelocity(value: Int) {
        velocity = value
    }

    fun updateVMG(value: Int) {
        velocityMadeGood = value
        makeBeeping()
        lastVMG = value
    }

    fun setRaceStatus(isRaceStarted: Boolean) {
        this.isRaceStarted = isRaceStarted
        if (!isRaceStarted) voiceover.stopRepeatSound()
    }

    fun setMuteStatus(isVoiceoverMuted: Boolean) {
        this.isVoiceoverMuted = isVoiceoverMuted
        if (isVoiceoverMuted) voiceover.stopRepeatSound()
        if (!isVoiceoverMuted) { // after unmuting it must start to make beeping
            makeBeeping()
            lastVMG -= 1
        }
    }

    fun updateBestUpwind(value: Int) {
        bestUpwind = value
    }

    fun updateBestDownwind(value: Int) {
        bestDownwind = value
    }

    private fun makeBeeping() {
        val percent: Int

        if ((velocityMadeGood != 0) and (velocityMadeGood != lastVMG) and isRaceStarted) { // если изменилась VMG, перезапускаем прищалку
            lastVMG = velocityMadeGood
            percent = if (velocityMadeGood > 0) { // обрабатываем апвинд
                calculateBeepingPercent(bestUpwind) // считаем % от максимульной частоты пиков
            } else
                calculateBeepingPercent(abs(bestDownwind)) // считаем % от максимульной частоты пиков
            if (velocity > 5 && percent > vmgBeeperSensitivity)
                playBeepingIfNotMuted(percent)
            else
                voiceover.stopRepeatSound()
        }
    }

    private fun playBeepingIfNotMuted(percent: Int) {
        if (!isVoiceoverMuted) voiceover.playRepeatSound(percent)
    }

    private fun calculateBeepingPercent(VMGmax: Int): Int {
        var calculatedPercent = 0
        if (VMGmax > 0) calculatedPercent = abs(velocityMadeGood * 100 / VMGmax).toInt()
        if (calculatedPercent > 100) calculatedPercent = 110
        return calculatedPercent
    }
}