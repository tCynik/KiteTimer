package com.tcynik.racertimer.main_activity.racing_timer;

import com.tcynik.racertimer.info_bar.InfoBarStatusUpdater;
import com.tcynik.racertimer.main_activity.MainActivity;
import com.tcynik.racertimer.info_bar.InfoBarStatusUpdater;

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
