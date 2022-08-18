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


    public InfoBarPresenter() {
        initBarUpdaters();
        updateStartingStatus();
        currentStatus = new Status(barUpdater, "App ready");
    }

    public void setInfoBarTVInterface (TextViewController infoBarTVInterface) {
        theBarIsNotLocked = true;
        this.infoBarTVInterface = infoBarTVInterface;
        if (currentStatus == null)
            currentStatus = (Status) statementQueue.get(0);
        Message helloMessage = new Message(barUpdater, "Hello!", 3000);
        helloMessage.print();
        //updateBarByStatement(currentStatus);
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
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        timeoutIsEnded();
                    }
                }, timeoutMilSec, 1);
            }
        };
    }

    private void updateStartingStatus() {
        currentStatus = new Status(barUpdater, "No GPS");
        updateBarByStatement(currentStatus);
    }

    private void updateBarByStatement(BarStatement barStatement) {
        if (infoBarTVInterface != null || theBarIsNotLocked) {
            barStatement.print();
        }
        else
            addToQueue(barStatement);
    }

    private void timeoutIsEnded() {
        checkQueueHasMessages();
    }

    private void checkQueueHasMessages () {
        if (statementQueue.isEmpty())
            currentStatus.print();
        else
            makeNextMessage();
    }

    private void makeNextMessage() {
        BarStatement nextMessage = ejectNextFromQueue();
        nextMessage.print();
    }

    private void manageQueueStatement(BarStatement barStatement) {
        if (theBarIsNotLocked)
            barStatement.print();
        else
            addToQueue(barStatement);
    }

    private void addToQueue(BarStatement barStatement) {
        statementQueue.add(barStatement);
    }

    private BarStatement ejectNextFromQueue() {
        BarStatement nextStatement = null;
        if (!statementQueue.isEmpty())
            nextStatement = statementQueue.get(0);
        statementQueue.remove(0);
        return nextStatement;
    }

    public void updateTheBar(String nextBarStatus) {
        Status status;
        Message message;
        switch (nextBarStatus) {

            /** --- STATUSES --- */
            case "ready to go":
                status = new Status(barUpdater, "Go chase!");
                currentStatus = status;
                break;
            case "timer on":
                status = new Status(barUpdater, "Get ready!");
                currentStatus = status;
                break;
            case "timer three":
                status = new Status(barUpdater, "THEE!");
                currentStatus = status;
                break;
            case "timer two":
                status = new Status(barUpdater, "TWO!");
                currentStatus = status;
                break;
            case "timer one":
                status = new Status(barUpdater, "ONE!");
                currentStatus = status;
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
                message = new Message(barUpdater, "GPS online", 2000);
                manageQueueStatement(message);
                break;
            case "start":
                message = new Message(barUpdater, "GO! GO! GO!!!", 2000);
                manageQueueStatement(message);
                break;
            case "new wind":
                message = new Message(barUpdater, "wind dir updated", 2000);
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
                Log.i(PROJECT_LOG_TAG, "unknown command "+nextBarStatus);
        }

        //Log.i("bugfix", "the bar updater got new command: " +nextBarStatus+", bar is  NOT locked = " +theBarIsNotLocked);
    }

//
//    private void manageTheStatus(String nextBarStatus) {
//        Log.i("bugfix", "managing next: " +nextBarStatus);
//        String message = "empty";
//        switch (nextBarStatus) {
//            case "greetings":
//                greetings();
//            case "stop race": message = "last: " + infoBarTVInterface.getTextFromView();
//                break;
//            case "cancel race": message = "Go chase!";
//                break;
//            case "ready to go": message = "Go chase!";
//                break;
//            case "timer": message = "Get ready";
//                break;
//            case "timer three": message = "THREE!";
//                break;
//            case "timer two": message = "TWO!";
//                break;
//            case "timer one": message = "ONE!!!";
//                break;
//            case "start": message = "GO! GO! GO!!!";
//                infoBarTVInterface.updateTextView(message);
//                lockTheBar(2000);
//                break;
//            case "set wind":
//                Log.i("bugfix", "call the blinky to set wind");
//                blinkingNotificationOn("SET WIND DIR!");
//                break;
//            case "wind ok":
//                blinkingNotificationOff();
//                break;
//            case "gps": message = "GPS online";
//                isGpsConnected = true;
//                break;
//            default: break;
//        }
//        if (! message.equals("empty"))
//            if (theBarIsNotLocked) fillTextView(message);
//    }

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
        //Log.i(PROJECT_LOG_TAG, "printing the next message " +statusName);
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