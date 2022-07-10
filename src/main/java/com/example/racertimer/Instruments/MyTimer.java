package com.example.racertimer.Instruments;

import android.os.SystemClock;

import java.util.Calendar;
import java.util.Date;

public abstract class MyTimer {
    public Thread thread;
    Date currentTime, startedTime;
    long currentTimerSize, timerLeft;
    TimerStatusUpdater timerStatusUpdater;
    boolean isTimerRan = true;

    public MyTimer(TimerStatusUpdater timerStatusUpdater, int countMilSec, int beginningTimerLeftSec) {
        this.timerStatusUpdater = timerStatusUpdater;
        this.timerLeft = beginningTimerLeftSec * 1000;
        startedTime = Calendar.getInstance().getTime();
        initTheThread(countMilSec);
    }

    public void initTheThread(long periodCountMilSec) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isTimerRan) {
                    SystemClock.sleep(periodCountMilSec);
                    currentTime = Calendar.getInstance().getTime();
                    timerLeft = calculateTimerLeft(currentTime);
                    onTimerTicked(timerLeft);
                }
            }
        });
    }

    public void start() {
        thread.start();
    }

    void onTimerTicked (long timerLeft) {
        timerStatusUpdater.onTimerStatusUpdated(timerLeft);
    }

    long calculateTimerLeft(Date currentTime) {
        return 0;
    }

    public void setTimerLeft(long timerLeft) {
        this.timerLeft = timerLeft;
    }

    public void stopTheTimer() {
        isTimerRan = false;
    }

    public void setCurrentTimerSize(long currentTimerSize) {
        this.currentTimerSize = currentTimerSize;
    }
}
