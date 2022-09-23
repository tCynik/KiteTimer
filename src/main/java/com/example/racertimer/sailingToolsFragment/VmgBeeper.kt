package com.example.racertimer.sailingToolsFragment

import android.content.Context
import android.util.Log
import com.example.racertimer.multimedia.BeepSounds

class VmgBeeper(context: Context) {
    private val voiceover = BeepSounds(context)
    private var vmgBeeperSensitivity = 0
    private var velocity = 0
    private var velocityMadeGood = 0
    private var lastVMG = 0
    private var isRaceStarted = false
    private var bestUpwind = 0
    private var bestDownwind = 0
    private var isVoiceoverMuted = false

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
        if (!isVoiceoverMuted) voiceover.stopRepeatSound()
    }

    fun updateBestUpwind(value: Int) {
        bestUpwind = value
    }

    fun updateBestDownwind(value: Int) {
        bestDownwind = value
    }

    private fun makeBeeping() {
        val threshold: Int
        val percent: Int
        Log.i("bugfix", "make beeping called. VMG = ")
        if ((velocityMadeGood != 0) and (velocityMadeGood != lastVMG) and isRaceStarted) { // если изменилась VMG, перезапускаем прищалку
            lastVMG = velocityMadeGood
            if (velocityMadeGood > 0) { // обрабатываем апвинд
                threshold =
                    (bestUpwind * vmgBeeperSensitivity).toInt() // высчитываем порог чувствительности ВМГ
                if (velocityMadeGood > threshold) { // если ВМГ выше порога,
                    percent = calculateBeepingPercent(
                        bestUpwind,
                        threshold
                    ) // считаем % от максимульной частоты пиков
                    if (velocity > 5) voiceover.playRepeatSound(percent) // перезапуск пищалки (с автоматической остановкой)
                } else voiceover.stopRepeatSound()
            } else { // обрабатываем даунвинд
                threshold =
                    (bestDownwind * vmgBeeperSensitivity).toInt() // высчитываем порог чувствительности ВМГ
                if (velocityMadeGood < threshold) { // если ВМГ меньше порога (больше по модулю, т.к. и то и то минус)
                    percent =
                        calculateBeepingPercent(bestDownwind, threshold) // запускаем/меняем пищалку
                    if (velocity > 5) voiceover.playRepeatSound(percent)
                } else voiceover.stopRepeatSound()
            }
        }
    }

    private fun calculateBeepingPercent(VMGmax: Int, threshold: Int): Int {
        val activeVMG = velocityMadeGood - threshold
        val activeVMGmax = VMGmax - threshold
        return Math.abs(activeVMG * 100 / activeVMGmax)
    }


}