package com.example.racertimer;

import android.util.Log;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class InfoBarPresenter {
    private final static String PROJECT_LOG_TAG = "racer_timer_info_bar";
    private final int BLINKING_PERIOD_MILSEC = 1000;

    private TextViewController infoBarTVInterface;
    private boolean theBarIsNotLocked = false;
    private boolean blinkingInProgress = false;

    private Status currentStatus;
    private LinkedList<Message> messageList;
    private LinkedList<BarStatement> statementQueue = new LinkedList<>();

    public BarUpdater barUpdater;

    private String lastTimer = "null";
    private boolean waitNextStatus = false;

    public InfoBarPresenter() {
        initBarUpdaters();
        currentStatus = new Status(barUpdater, "No GPS");
    }

    public void setInfoBarTVInterface (TextViewController infoBarTVInterface) {
        theBarIsNotLocked = true;
        this.infoBarTVInterface = infoBarTVInterface;
        if (currentStatus == null)
            currentStatus = (Status) statementQueue.get(0);
        Message helloMessage = new Message(barUpdater, "Hello!", 3000);
        helloMessage.print();
    }

    private void initBarUpdaters() {
        barUpdater = new BarUpdater() {

            @Override
            public void printStatement(String message) {
                infoBarTVInterface.updateTextView(message);
            }

            @Override
            public void lockTheBar(long timeoutMilSec) {
                theBarIsNotLocked = false;
                //todo: runOnUiThread()
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        timeoutEndedCheckQueue();
                        theBarIsNotLocked = true;
                    }
                }, timeoutMilSec);
            }
        };
    }

    private void timeoutEndedCheckQueue() {
        if (statementQueue.isEmpty())
            currentStatus.print();
        else
            //makeNextMessage();
            nextMessageFromQueue().print();

    }

    private void manageQueueStatement(BarStatement barStatement) {
        if (theBarIsNotLocked)
            barStatement.print();
        else
            statementQueue.add(barStatement);
    }

    private BarStatement nextMessageFromQueue() {
        BarStatement nextStatement = null;
        if (!statementQueue.isEmpty())
            nextStatement = statementQueue.get(0);
        statementQueue.remove(0);
        return nextStatement;
    }

    private void updateCurrentStatus (Status status){
        currentStatus = status;
        if (theBarIsNotLocked){
            Log.i("racer_timer_info_bar", "bugfix: printing new status ="+ status.statusName);
            status.print();
        }
    }

    public void stopRaceOnTimer(String timerNumbers) {
        Status status = new Status(barUpdater, " "+timerNumbers);
        updateCurrentStatus(status);
    }

    public void updateTheBar(String nextBarStatus) {
        Status status;
        Message message;
        switch (nextBarStatus) {

            /** --- STATUSES --- */
            case "ready to go":
                status = new Status(barUpdater, "Go chase!");
                updateCurrentStatus(status);
                break;
            case "timer":
                status = new Status(barUpdater, "Get ready!");
                updateCurrentStatus(status);
                break;
            case "timer three":
                status = new Status(barUpdater, "THEE!");
                updateCurrentStatus(status);
                break;
            case "timer two":
                status = new Status(barUpdater, "TWO!");
                updateCurrentStatus(status);
                break;
            case "timer one":
                status = new Status(barUpdater, "ONE!");
                updateCurrentStatus(status);
                break;

            //TODO: case "stop race":
//                stopRace();
//                //message = "last: " + infoBarTVInterface.getTextFromView();
//                break;

            /** --- MESSAGES --- */

            case "greetings":
                message = new Message(barUpdater, "Hello!", 3000);
                manageQueueStatement(message);
                break;
            case "gps":
                currentStatus = new Status(barUpdater, "App ready");
                message = new Message(barUpdater, "GPS online", 2000);
                manageQueueStatement(message);
                break;
            case "start":
                message = new Message(barUpdater, "GO! GO! GO!!!", 2000);
                manageQueueStatement(message);
                break;
            case "set wind":
                message = new Message(barUpdater, "wind dir upd.", 2000);
                manageQueueStatement(message);
                break;
            case "stop race":
                status = new Status(barUpdater, "last: "+lastTimer);
                updateCurrentStatus(status);
                message = new Message(barUpdater, "FINISH!", 2000);
                manageQueueStatement(message);
                break;

            // todo:
            /** --- WARNINGS --- */
//            case "wind ok":
//                Warning message = new Message(barUpdater, "Hello!", 2000);
//                manageQueueStatement(Message);
//                askWindDirection(true);
//                //blinkingNotificationOff();
//                break;
//
//                break;
//            case "set wind":
//                askWindDirection(false);
//                Log.i("bugfix", "call the blinky to set wind");
//                blinkingNotificationOn("SET WIND DIR!");
//                break;
            default:
                lastTimer = nextBarStatus;
                InstantMessage instantMessage = new InstantMessage(barUpdater, nextBarStatus);
                if (theBarIsNotLocked)
                    instantMessage.print();
        }
    }


//    private void blinkingNotificationOn(String message) {
//        blinkingInProgress = true;
//        Log.i("bugfix", "start blinking");
//
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (blinkingInProgress) {
//                    Log.i("bugfix", "next blink");
//                    SystemClock.sleep(BLINKING_PERIOD_MILSEC);
//                    fillTextView(message);
//                    SystemClock.sleep(BLINKING_PERIOD_MILSEC);
//                    fillTextView(currentBarStatus);
//                }
//            }
//        });
//    }
//
//    private void blinkingNotificationOff() {
//        blinkingInProgress = false;
//    }
}

interface BarUpdater {
    void printStatement(String message);
    void lockTheBar(long timeoutMilSec);
    // TODO: here must be set of the next statement executing
    // plus starting sheduleAtFixedRate with checking the time and after that executing
        }

abstract class BarStatement {
    private final static String PROJECT_LOG_TAG = "racer_timer_info_bar";
    final String statusName;
    final BarUpdater barUpdater;
    long timeoutMilSec;

    BarStatement(BarUpdater barUpdater, String statusName) {
        this.barUpdater = barUpdater;
        this.statusName = statusName;
    }

    public void print() {
        Log.i(PROJECT_LOG_TAG, "printing the next message " +statusName);
        printToBar(statusName);
        lockTheBar(timeoutMilSec);
    }

    protected void printToBar(String name) {
        barUpdater.printStatement(statusName);
    }

    private void lockTheBar(long timeout) {
        barUpdater.lockTheBar(timeout);
    }
}

class Message extends BarStatement {

    public Message(BarUpdater barUpdater, String statusName, long lockTime) {
        super(barUpdater, statusName);
        this.timeoutMilSec = lockTime;
    }
}

class InstantMessage extends BarStatement {
    public InstantMessage(BarUpdater barUpdater, String statusName) {
        super(barUpdater, statusName);
        Log.i("racer_timer_info_bar", "creating new default message ="+ statusName);
    }

    @Override
    public void print() {
        printToBar(statusName);
    }
}

class Status extends BarStatement {
    public Status(BarUpdater barUpdater, String statusName) {
        super(barUpdater, statusName);
    }

    @Override
    public void print() {
        printToBar(statusName);
    }
}

class Warning extends BarStatement {
    public Warning(BarUpdater barUpdater, String statusName, long lockTime) {
        super(barUpdater, statusName);
        this.timeoutMilSec = lockTime;
    }

}