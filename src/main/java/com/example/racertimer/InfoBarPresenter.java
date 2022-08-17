package com.example.racertimer;

import android.os.SystemClock;
import android.util.Log;

import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class InfoBarPresenter {
    private final static String PROJECT_LOG_TAG = "racer_timer_info_bar";
    private final int BLINKING_PERIOD_MILSEC = 1000;

    private TextViewController infoBarTVInterface;
    private boolean isGpsConnected = false;
    private boolean theBarIsNotLocked = true;
    private boolean blinkingInProgress = false;
    private String currentBarStatus = "";
    private String nextStatusAfterUnlock = "empty";

    private Status currentStatus;
    private LinkedList<Message> messageList;
    private LinkedList<BarStatement> statementQueue;
    private Date nextStatementTime;
    private Message lastMessage;

    public BarUpdater barUpdater, statusBarUpdater, messageBarUpdater, warningBarUpdater;


    public InfoBarPresenter(TextViewController infoBarTVInterface) {
        initBarUpdaters();
        this.infoBarTVInterface = infoBarTVInterface;
        updateStartingStatus();
    }

    public void setInfoBarTVInterface (TextViewController infoBarTVInterface) {
        this.infoBarTVInterface = infoBarTVInterface;
        if (currentStatus == null)
            currentStatus = (Status) statementQueue.get(0);
        updateBarByStatement(currentStatus);
    }

    private void updateStartingStatus() {
        currentStatus = new Status(barUpdater, "No GPS");
        updateBarByStatement(currentStatus);
    }

    private void updateBarByStatement(BarStatement barStatement) {
        if (infoBarTVInterface != null || theBarIsNotLocked) {
            barStatement.printWithHolding(infoBarTVInterface);
        }
        else
            addToQueue(barStatement);
    }

    private void addToQueue(BarStatement barStatement) {
        statementQueue.add(barStatement);
    }

    private void makeNextMessage() {
        theBarIsNotLocked = true;
        BarStatement nextStatement = ejectNextFromQueue();
        if (nextStatement == null)
            nextStatement = currentStatus;
        nextStatement.printWithHolding(infoBarTVInterface);
    }

    private BarStatement ejectNextFromQueue() {
        BarStatement nextStatement = null;
        if (!statementQueue.isEmpty())
            nextStatement = statementQueue.get(0);
        statementQueue.remove(0);
        return nextStatement;
    }

    void greetings() {
        Message helloMessage = new Message(barUpdater, "Hello!", 2000);
        helloMessage.print();
    }

    private void initBarUpdaters() {
        barUpdater = new BarUpdater() {
            @Override
            public void makeDeferredStatusUpdating(long timeoutMilSec) {
                theBarIsNotLocked = false;
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        makeNextMessage();
                    }
                }, timeoutMilSec, 1);
            }

            @Override
            public void printMeNow(String message) {
                infoBarTVInterface.updateTextView(message);
            }
        };
    }

    public void updateTheBar(String nextBarStatus) {
        String message = "empty";
        Statement statement;
        switch (nextBarStatus) {

            /** --- STATUSES --- */
            case "cancel race":
                statement = new Status(barUpdater, "Go chase!");
                //message = "Go chase!";
                break;
            case "ready to go":
                readyToGo();
                //message = "Go chase!";
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

            case "stop race":
                stopRace();
                //message = "last: " + infoBarTVInterface.getTextFromView();
                break;

            /** --- MESSAGES --- */

            case "greetings":
                greetings();
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

            /** --- WARNINGS --- */

            case "timer":
                initTimer();
                //message = "Get ready";
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
            default:
                Log.i(PROJECT_LOG_TAG, "unknown command "+message);
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

    private void timerTicking(String message) {
        Status status = new Status(statusBarUpdater, message);
        changeStatus(status);
        if (theBarIsNotLocked) status.print();
    }

    private void changeStatus(Status nextStatus){
        currentStatus = nextStatus;
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
                showNextMessage();
                if (!nextStatusAfterUnlock.equals("empty"))
                    manageTheStatus(nextStatusAfterUnlock);
                nextStatusAfterUnlock = "empty";
                cancel();
            }
        }, timeToLockMilSec, 1);
    }

    private void showNextMessage() {
        Message nextMessage;
        if (!messageList.isEmpty()) {
            nextMessage = messageList.get(0);
            messageList.remove(0);
            nextMessage.print();
        }
    }

    private void printDefferedMessage(String message) {
        updateTheBar(message);
        nextStatusAfterUnlock = "empty";
    }

}

interface BarUpdater {
    void makeDeferredStatusUpdating(long timeoutMilSec);
    void printMeNow(String message); // todo:
    // TODO: here must be set of the next statement executing
    // plus starting sheduleAtFixedRate with checking the time and after that executing
        }

abstract class BarStatement {
    final String statusName;
    final BarUpdater barUpdater;
    long timeoutMilSec = 1000;

    BarStatement(BarUpdater barUpdater, String statusName) {
        this.barUpdater = barUpdater;
        this.statusName = statusName;
    }

    protected void printWithHolding(TextViewController infoBarTVInterface) {
        printSelf(infoBarTVInterface);
        barUpdater.makeDeferredStatusUpdating(timeoutMilSec);
    }

    protected void printSelf(TextViewController infoBarTVInterface) {
        infoBarTVInterface.updateTextView(statusName);
    }

    public void print() {
        barUpdater.printMeNow(statusName);
    }
}

class Message extends BarStatement {

    public Message(BarUpdater barUpdater, String statusName, long lockTime) {
        super(barUpdater, statusName);
        this.timeoutMilSec = lockTime;
    }

    @Override
    public void print() {
        barUpdater.printMeNow(statusName);
        barUpdater.lockTheBar(timeoutMilSec);
    }
}

class Status extends BarStatement {
    public Status(BarUpdater barUpdater, String statusName) {
        super(barUpdater, statusName);
    }

    public String getStatusName() {
        return statusName;
    }
}

class Warning extends BarStatement {
    public Warning(BarUpdater barUpdater, String statusName) {
        super(barUpdater, statusName);
    }
}