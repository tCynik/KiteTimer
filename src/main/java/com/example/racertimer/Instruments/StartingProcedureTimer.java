package com.example.racertimer.Instruments;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StartingProcedureTimer extends MyTimer {
    long currentTimePeriod;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");

    public StartingProcedureTimer(TimerStatusUpdater timerStatusUpdater) {
        super(timerStatusUpdater, 1000, 60); // период обновления счетчика 1 секунда
    }

    @Override
    long calculateTimerLeft(Date currentTime) {
        return startedTime.getTime() + currentTimePeriod - currentTime.getTime();
    }

    public void setTimerPeriod(long currentTimerSize) {
        startedTime = Calendar.getInstance().getTime();
        this.currentTimePeriod = currentTimerSize;
        onTimerTicked(currentTimerSize);
    }

    public void correctTimerLeft(int correcter) {
        currentTimePeriod = currentTimePeriod + correcter;
    }

    public void resume() {
        if (!isTimerRan) {
            startedTime = Calendar.getInstance().getTime();
            isTimerRan = true;
        }
    }
}
