package com.example.racertimer.Instruments;

import com.example.racertimer.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StartingProcedureTimer extends MyTimer {
    private MainActivity mainActivity;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");

    public StartingProcedureTimer(TimerStatusUpdater timerStatusUpdater) {
        super(timerStatusUpdater, 1000, 60); // период обновления счетчика 1 секунда
    }

    @Override
    long calculateTimerLeft(Date currentTime) {
        return startedTime.getTime() + currentTimerSize - currentTime.getTime();
    }

    public void correctTimerLeft(long correcter) {
        long correctedTime = currentTime.getTime() + correcter;
        setTimerLeft(correctedTime);
    }
}
