package com.example.racertimer.Instruments;

import android.os.SystemClock;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public abstract class MyTimer {
    public Thread thread;
    Date currentTime, startedTime;
    long timerLeft;
    TimerStatusUpdater timerStatusUpdater;
    boolean isTimerRan = true;
    long periodCountMilSec;

    public MyTimer(TimerStatusUpdater timerStatusUpdater, int countMilSec, int beginningTimerLeftSec) {
        this.timerStatusUpdater = timerStatusUpdater;
        this.timerLeft = beginningTimerLeftSec * 1000;
        periodCountMilSec = countMilSec;
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
                    Log.i("bugfix", "Thread: "+Thread.currentThread().getName()+", making next tick ");
                }
            }
        });
    }

    public void start() {
        startedTime = Calendar.getInstance().getTime();
        isTimerRan = true;
        thread.start();
    }

    void onTimerTicked (long timerLeft) {
        timerStatusUpdater.onTimerStatusUpdated(timerLeft);
    }

    long calculateTimerLeft(Date currentTime) {
        return 0;
    }

    public void stop() {
        if (isTimerRan) isTimerRan = false;
    }
}
