package com.tcynik.racertimer.sailingToolsFragment

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RetryTimer(private val endingTimer: EndingTimerInterface) {
    fun execute() {
        val scope = CoroutineScope(Job())
        scope.launch {
            delay(5000) // todo: set 500
            endingTimer()
        }
    }

    private fun endingTimer() {
        val handler =  Handler(Looper.getMainLooper())
        val runnable = Runnable {
            endingTimer.onTimerEnded()
        }
        handler.post(runnable)
    }

    interface EndingTimerInterface {
        fun onTimerEnded()
    }
}