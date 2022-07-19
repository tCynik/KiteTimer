package com.example.racertimer;

import java.util.Timer;
import java.util.TimerTask;

public class InfoBarPresenter {
    private TextViewController infoBarTVInterface;
    private boolean isGpsConnected = false;
    private boolean theBarIsNotLocked = true;

    public InfoBarPresenter(TextViewController infoBarTVInterface) {
        this.infoBarTVInterface = infoBarTVInterface;
        //greetings();
    }

    public void updateTheBar(String nextBarStatus) {
        //Log.i("bugfix", "incoming new bar status = "+ nextBarStatus);
        switch (nextBarStatus) {
            case "stop race": nextBarStatus = "last: " + infoBarTVInterface.getTextFromView();
                break;
            case "cancel race": nextBarStatus = "Go chase!";
                break;
            case "ready to go": nextBarStatus = "Go chase!";
                break;
            case "timer": nextBarStatus = "Get ready";
                break;
            case "timer three": nextBarStatus = "THREE!";
                break;
            case "timer two": nextBarStatus = "TWO!";
                break;
            case "timer one": nextBarStatus = "ONE!!!";
                break;
            case "start": nextBarStatus = "GO! GO! GO!!!";
                infoBarTVInterface.updateTextView(nextBarStatus);
                lockTheBar(2000);
                break;
            case "gps": nextBarStatus = "GPS online";
                isGpsConnected = true;
                break;
        }
        if (theBarIsNotLocked) infoBarTVInterface.updateTextView(nextBarStatus);
}

    private void lockTheBar (long timeToLockMilSec) {
        theBarIsNotLocked = false;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                theBarIsNotLocked = true;
                cancel();
            }
        }, timeToLockMilSec, 1);
    }

    public void unlockTheBar() {
        theBarIsNotLocked = true;
    }

    void greetings() {
        updateTheBar("Hello!");
        lockTheBar(3000);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isGpsConnected) updateTheBar("Go chase!");
                else updateTheBar("No GPS");
                cancel();
            }
        }, 3000, 1);
    }

}
