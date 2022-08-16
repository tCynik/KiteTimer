package com.example.racertimer;

import android.os.SystemClock;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class InfoBarPresenter {
    private final int BLINKING_PERIOD_MILSEC = 1000;

    private TextViewController infoBarTVInterface;
    private boolean isGpsConnected = false;
    private boolean theBarIsNotLocked = true;
    private boolean blinkingInProgress = false;
    private String currentBarStatus = "";
    private String nextStatusAfterUnlock = "empty";

    private TimeMessage lastTimeMessage;

    public BarUpdater barUpdater;


    public InfoBarPresenter(TextViewController infoBarTVInterface) {
        this.infoBarTVInterface = infoBarTVInterface;
        barUpdater = new BarUpdater() {
            @Override
            public void instantUpdateStatus(String statusName) {
                updateTheBar(statusName);
            }

            @Override
            public void setTimer(String timerSrting) {

            }
        };
    }


    public void updateTheBar(String nextBarStatus) {
        String message = "empty";
        switch (nextBarStatus) {
            case "greetings":
                greetings();
            case "stop race":
                stopRace();
                //message = "last: " + infoBarTVInterface.getTextFromView();
                break;
            case "cancel race":
                cancelRace();
                //message = "Go chase!";
                break;
            case "ready to go":
                readyToGo();
                //message = "Go chase!";
                break;
            case "timer":
                initTimer();
                //message = "Get ready";
                break;
            case "timer three":
                timerTicking("THEE!");
                //message = "THREE!";
                break;
            case "timer two":
                timerTicking("TWO!");
                //message = "TWO!";
                break;
            case "timer one":
                timerTicking("ONE!!!");
                //message = "ONE!!!";
                break;
            case "start":
                startTheRace();
//                message = "GO! GO! GO!!!";
//                infoBarTVInterface.updateTextView(message);
//                lockTheBar(2000);
                break;
            case "set wind":
                askWindDirection(false);
                Log.i("bugfix", "call the blinky to set wind");
                blinkingNotificationOn("SET WIND DIR!");
                break;
            case "wind ok":
                askWindDirection(true);
                //blinkingNotificationOff();
                break;
            case "gps":
                setGPSOnline();
//                message = "GPS online";
//                isGpsConnected = true;
                break;
            default:
                break;
        }

        Log.i("bugfix", "the bar updater got new command: " +nextBarStatus+", bar is  NOT locked = " +theBarIsNotLocked);
        if (theBarIsNotLocked) {
            manageTheStatus(nextBarStatus);
        }
        else {
            nextStatusAfterUnlock = nextBarStatus;
            Log.i("bugfix", "status -" +nextBarStatus+"- added to queue");
        }
    }



    private void manageTheStatus(String nextBarStatus) {
        Log.i("bugfix", "managing next: " +nextBarStatus);
        String message = "empty";
        switch (nextBarStatus) {
            case "greetings":
                greetings();
            case "stop race": message = "last: " + infoBarTVInterface.getTextFromView();
                break;
            case "cancel race": message = "Go chase!";
                break;
            case "ready to go": message = "Go chase!";
                break;
            case "timer": message = "Get ready";
                break;
            case "timer three": message = "THREE!";
                break;
            case "timer two": message = "TWO!";
                break;
            case "timer one": message = "ONE!!!";
                break;
            case "start": message = "GO! GO! GO!!!";
                infoBarTVInterface.updateTextView(message);
                lockTheBar(2000);
                break;
            case "set wind":
                Log.i("bugfix", "call the blinky to set wind");
                blinkingNotificationOn("SET WIND DIR!");
                break;
            case "wind ok":
                blinkingNotificationOff();
                break;
            case "gps": message = "GPS online";
                isGpsConnected = true;
                break;
            default: break;
        }
        if (! message.equals("empty"))
            if (theBarIsNotLocked) fillTextView(message);
    }

    private void fillTextView(String string) {
        currentBarStatus = string;
        infoBarTVInterface.updateTextView(string);
    }

    private void blinkingNotificationOn(String message) {
        blinkingInProgress = true;
        Log.i("bugfix", "start blinking");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (blinkingInProgress) {
                    Log.i("bugfix", "next blink");
                    SystemClock.sleep(BLINKING_PERIOD_MILSEC);
                    fillTextView(message);
                    SystemClock.sleep(BLINKING_PERIOD_MILSEC);
                    fillTextView(currentBarStatus);
                }
            }
        });
    }

    private void blinkingNotificationOff() {
        blinkingInProgress = false;
    }

    private void lockTheBar (long timeToLockMilSec) {
        theBarIsNotLocked = false;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                theBarIsNotLocked = true;
                if (!nextStatusAfterUnlock.equals("empty"))
                    manageTheStatus(nextStatusAfterUnlock);
                nextStatusAfterUnlock = "empty";
                cancel();
            }
        }, timeToLockMilSec, 1);
    }

    private void printDefferedMessage(String message) {
        updateTheBar(message);
        nextStatusAfterUnlock = "empty";
    }

    void greetings() {
        RegularMessage hello = new RegularMessage("Hello!", 2000);
        hello.print();
        updateTheBar("Hello!");
        lockTheBar(3000);
    }
}

interface BarUpdater {
    public void instantUpdateStatus(String statusName);
    public void setTimer(String timerSrting);
        }

abstract class BarStatus{
    private final String statusName;

    BarStatus(String statusName) {
        this.statusName = statusName;
    }



}

class Mode extends BarStatus {

    Mode(String statusName) {
        super(statusName);
    }
}

class TimeMessage extends BarStatus{

    public TimeMessage(String statusName) {
        super(statusName);
    }
}

class RegularMessage extends BarStatus{
    long barLockTimeMilSec;
    public RegularMessage(String statusName, long lockTime) {
        super(statusName);
        this.barLockTimeMilSec = lockTime;
    }
}