package com.example.racertimer.Instruments;

import android.util.Log;

import java.util.Date;

public class RacingTimer extends MyTimer {
    public RacingTimer(TimerStatusUpdater timerStatusUpdater) {
        super(timerStatusUpdater, 10, 0, "mm:ss.SS");
    }

    @Override
    long calculateTimerLeft(Date currentTime) {
        return currentTime.getTime() - startedTime.getTime();
    }

    @Override
    void onTimerTicked(long timerLeft) {
        if (timerLeft < 2000) timerStatusUpdater.onTimerStatusUpdated("GO! GO! GO!!!");
        else super.onTimerTicked(timerLeft);
    }

    @Override
    public void stop() {
        super.stop();
        String timerStatusString = simpleDateFormat.format(timerLeft);
        timerStatusUpdater.onTimerStatusUpdated("last: "+timerStatusString);
    }
}
