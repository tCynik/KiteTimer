package com.example.racertimer.main_activity.racing_timer;

import android.os.SystemClock;

import com.example.racertimer.info_bar.InfoBarStatusUpdater;
import com.example.racertimer.main_activity.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class MyTimer {
    public Thread thread;
    Date startedTime;
    long timerLeft;
    InfoBarStatusUpdater infoBarStatusUpdater;
    boolean isTimerRan = false;
    SimpleDateFormat simpleDateFormat;
    long periodCountMilSec;

    MainActivity mainActivity;

    public MyTimer(InfoBarStatusUpdater infoBarStatusUpdater, int countMilSec, int beginningTimerLeftSec, String dateFormatPattern) {
        this.infoBarStatusUpdater = infoBarStatusUpdater;
        this.timerLeft = beginningTimerLeftSec * 1000;
        this.periodCountMilSec = countMilSec;
        simpleDateFormat = new SimpleDateFormat(dateFormatPattern);
    }

    public MyTimer(MainActivity mainActivity, InfoBarStatusUpdater infoBarStatusUpdater, int countMilSec, int beginningTimerLeftSec, String dateFormatPattern) {
        this.mainActivity = mainActivity;
        this.infoBarStatusUpdater = infoBarStatusUpdater;
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
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onTimerTicked(timerLeft);
                        }
                    });
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
            infoBarStatusUpdater.onTimerStatusUpdated(timerStatusString);
        }
    }

    long calculateTimerLeft(Date currentTime) {
        return 0;
    }

}
