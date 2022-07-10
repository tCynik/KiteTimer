package com.example.racertimer.Instruments;

import java.util.Date;

public class RacingTimer extends MyTimer {
    public RacingTimer(TimerStatusUpdater timerStatusUpdater) {
        super(timerStatusUpdater, 10, 0);
    }

    @Override
    long calculateTimerLeft(Date currentTime) {
        return currentTime.getTime() - startedTime.getTime();
    }
}
