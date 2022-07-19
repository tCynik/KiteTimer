package com.example.racertimer.Instruments;

import com.example.racertimer.MainActivity;

import java.util.Date;

public class RacingTimer extends MyTimer {
    public RacingTimer(MainActivity mainActivity, InfoBarStatusUpdater infoBarStatusUpdater) {
        super(mainActivity, infoBarStatusUpdater, 10, 0, "mm:ss.SS");
    }

    @Override
    long calculateTimerLeft(Date currentTime) {
        return currentTime.getTime() - startedTime.getTime();
    }

    @Override
    void onTimerTicked(long timerLeft) {
            super.onTimerTicked(timerLeft);
    }

    @Override
    public void stop() {
        super.stop();
        infoBarStatusUpdater.onTimerStatusUpdated("stop race");
    }
}
