package com.example.racertimer.mainActivity.systemUI

import android.util.Log
import com.example.racertimer.mainActivity.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimeOutManager (val context: MainActivity, private val timeoutEnding: TimeoutEndingInterface): TimeoutInterface {
    private val timerScope: CoroutineScope = CoroutineScope(Job())
    override fun sartTimeoutToHide() {
        Log.i("bugfix: timeoutManager", "starting timeout")
        timerScope.launch {
            delay(5000)
            timeoutEnding.onTimeoutEnded()
        }
    }
}