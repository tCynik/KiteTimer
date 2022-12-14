package com.tcynik.racertimer.main_activity.presentation.systemUI

import com.tcynik.racertimer.main_activity.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimeOutManager (val context: com.tcynik.racertimer.main_activity.MainActivity, private val timeoutEnding: TimeoutEndingInterface): TimeoutInterface {
    private val timerScope: CoroutineScope = CoroutineScope(Job())
    override fun sartTimeoutToHide() {
        timerScope.launch {
            delay(5000)
            timeoutEnding.onTimeoutEnded()
        }
    }
}