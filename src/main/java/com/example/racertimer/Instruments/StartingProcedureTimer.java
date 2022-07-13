package com.example.racertimer.Instruments;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StartingProcedureTimer extends MyTimer {
    long currentTimePeriod;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");

    public StartingProcedureTimer(TimerStatusUpdater timerStatusUpdater) {
        super(timerStatusUpdater, 1000, 60, "mm:ss"); // период обновления счетчика 1 секунда
    }

    @Override
    long calculateTimerLeft(Date currentTime) {
        return startedTime.getTime() + currentTimePeriod - currentTime.getTime();
    }

    public void setTimerPeriod(long currentTimerSize) {
        startedTime = Calendar.getInstance().getTime();
        this.currentTimePeriod = currentTimerSize;
        String timerStatusString = simpleDateFormat.format(currentTimerSize);
        timerStatusUpdater.onTimerStatusUpdated(timerStatusString);
    }

    @Override
    public void stop() {
        super.stop();
        currentTimePeriod = timerLeft;
    }
}
