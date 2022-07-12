package com.example.racertimer.Instruments;

import android.os.SystemClock;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class MyTimer {
    public Thread thread;
    Date startedTime;
    long timerLeft;
    TimerStatusUpdater timerStatusUpdater;
    boolean isTimerRan = false;
    long periodCountMilSec;
    SimpleDateFormat simpleDateFormat;

    public MyTimer(TimerStatusUpdater timerStatusUpdater, int countMilSec, int beginningTimerLeftSec, String dateFormatPattern) {
        this.timerStatusUpdater = timerStatusUpdater;
        this.timerLeft = beginningTimerLeftSec * 1000;
        periodCountMilSec = countMilSec;
        initTheThread(countMilSec);
        simpleDateFormat = new SimpleDateFormat(dateFormatPattern);
    }

    public void initTheThread(long periodCountMilSec) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isTimerRan) {
                    SystemClock.sleep(periodCountMilSec);
                    Date currentTime = Calendar.getInstance().getTime();
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
        if (isTimerRan) {
            String timerStatusString = simpleDateFormat.format(timerLeft);
            timerStatusUpdater.onTimerStatusUpdated(timerStatusString);
        }
    }

    long calculateTimerLeft(Date currentTime) {
        return 0;
    }

    public void stop() {
        if (isTimerRan) isTimerRan = false;
    }
}
