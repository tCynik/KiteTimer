package com.example.racertimer.Instruments;

import android.os.SystemClock;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class MyTimer {
    public Thread thread;
    Date startedTime;
    long timerLeft;
    TimerStatusUpdater timerStatusUpdater;
    boolean isTimerRan = false;
    SimpleDateFormat simpleDateFormat;
    long periodCountMilSec;

    public MyTimer(TimerStatusUpdater timerStatusUpdater, int countMilSec, int beginningTimerLeftSec, String dateFormatPattern) {
        this.timerStatusUpdater = timerStatusUpdater;
        this.timerLeft = beginningTimerLeftSec * 1000;
        this.periodCountMilSec = countMilSec;
        simpleDateFormat = new SimpleDateFormat(dateFormatPattern);
    }

    public void initTheThread() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isTimerRan) {
                    SystemClock.sleep(periodCountMilSec);
                    Date currentTime = Calendar.getInstance().getTime();
                    timerLeft = calculateTimerLeft(currentTime);
                    onTimerTicked(timerLeft);
                    //Log.i("bugfix", "Thread: "+Thread.currentThread().getName()+", making next tick ");
                }
            }
        });
    }

    public boolean isTimerRan() {
        return isTimerRan;
    }

    public void start() {
        startedTime = Calendar.getInstance().getTime();
        isTimerRan = true;
        initTheThread();
        thread.start();
    }

    public void stop() {
        if (isTimerRan) isTimerRan = false;
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

}
