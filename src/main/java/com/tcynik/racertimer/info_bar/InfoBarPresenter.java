package com.tcynik.racertimer.info_bar;

import android.util.Log;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
//todo: implement the blinking of warnings
public class InfoBarPresenter {
    private final static String PROJECT_LOG_TAG = "racer_timer_info_bar";

    private TextViewController infoBarTVInterface;
    private boolean theBarIsNotLocked = false;
    private boolean blinkingInProgress = false;
    private boolean isRaceStarted = false;

    private Status currentStatus;
    private LinkedHashSet<String> warningsList = new LinkedHashSet<>();
    private LinkedList<BarStatement> statementQueue = new LinkedList<>();

    private EmptyMessage emptyMessage;
    public BarUpdater barUpdater;
    private InstantMessage instantMessage = null;

    private String lastTimer = "null";
    private boolean waitNextStatus = false;

    public InfoBarPresenter() {
        initBarUpdaters();
        currentStatus = new Status(barUpdater, "No GPS");
        emptyMessage = new EmptyMessage(barUpdater);
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
                lockBarForTime(timeoutMilSec);
            }
        };
    }

    private void lockBarForTime (long timeoutMilSec) {
        //Log.i(PROJECT_LOG_TAG, "bugfix: timeout ="+timeoutMilSec+ " is started ");
        theBarIsNotLocked = false;
        //todo: runOnUiThread()
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                theBarIsNotLocked = true;
                onTimeoutEnded();
            }
        }, timeoutMilSec);
    }

    private void raceStartedSetTo (boolean isRaceStarted) {
        Message message;
        if (isRaceStarted) {
            theBarIsNotLocked = true;
            message = new Message(barUpdater, "GO! GO! GO!!!", 2000);
            incomingStatementCheckQueue(message);
        } else {
            theBarIsNotLocked = true;
            message = new Message(barUpdater, "FINISH!", 2000);
            incomingStatementCheckQueue(message);
        }
        this.isRaceStarted = isRaceStarted;
    }

    private void onTimeoutEnded() {
        if (!statementQueue.isEmpty())
            statementQueue.remove(0);
        if (statementQueue.isEmpty()) {
            if (!warningsList.isEmpty()) {
                EmptyMessage emptyMessage = new EmptyMessage(barUpdater);
                statementQueue.add(emptyMessage);
                for (String nextWarning: warningsList) {
                    Warning warning = new Warning(barUpdater, nextWarning, 1000);
                    incomingStatementCheckQueue(warning);
                }
                statementQueue.get(0).print();
            }
            theBarIsNotLocked = true;
            if (!isRaceStarted)
                currentStatus.print();
        } else {
            theBarIsNotLocked = true;
            BarStatement nextStatement = nextMessageFromQueue();
            if (nextStatement != null) nextStatement.print();
        }
    }

    private void incomingStatementCheckQueue(BarStatement barStatement) {
        if (statementQueue.isEmpty()) {
            if (theBarIsNotLocked)
                barStatement.print();
            else
                statementQueue.add(barStatement);
        } else
            statementQueue.add(barStatement);
    }

    private BarStatement nextMessageFromQueue() {
        BarStatement nextStatement = null;
        if (statementQueue.size()>0) {
            nextStatement = statementQueue.get(0);
        }
        return nextStatement;
    }

    private void updateCurrentStatus (Status status){
        currentStatus = status;
        if (!isRaceStarted){
            if (theBarIsNotLocked)
                status.print();
        }
    }

    public void stopRaceOnTimer(String timerNumbers) {
        Status status = new Status(barUpdater, " "+timerNumbers);
        updateCurrentStatus(status);
    }

    public void updateTheBar(String nextBarStatus) {
        instantMessage = null;
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
            //cancel race

            /** --- MESSAGES --- */

            case "greetings":
                message = new Message(barUpdater, "Hello!", 3000);
                incomingStatementCheckQueue(message);
                break;
            case "gps":
                currentStatus = new Status(barUpdater, "App ready");
                message = new Message(barUpdater, "GPS online", 2000);
                incomingStatementCheckQueue(message);
                break;
            case "start":
                raceStartedSetTo(true);
                break;
            case "set wind":
                message = new Message(barUpdater, "wind dir upd.", 2000);
                incomingStatementCheckQueue(message);
                break;
            case "stop race":
                raceStartedSetTo(false);
                status = new Status(barUpdater, "last: "+lastTimer);
                lastTimer = null;
                updateCurrentStatus(status);
                break;

            /** --- WARNINGS --- */
            case "wind old":
                warningsList.add("set wind dir!");
                break;
            case "wind ok":
                warningsList.remove("set wind dir!"); // TODO: extract the resourses!
                break;

            default:
                lastTimer = nextBarStatus;
                if (isRaceStarted) {
                    if (theBarIsNotLocked) {
                        instantMessage = new InstantMessage(barUpdater, nextBarStatus);
                        instantMessage.print();
                    }
                } else {
                    message = new Message(barUpdater, nextBarStatus, 1000);
                    incomingStatementCheckQueue(message);
                }
        }
    }
}

interface BarUpdater {
    void printStatement(String message);
    void lockTheBar(long timeoutMilSec);
        }

abstract class BarStatement {
    final static String PROJECT_LOG_TAG = "racer_timer_info_bar";
    final String statusName;
    final BarUpdater barUpdater;
    long timeoutMilSec;

    BarStatement(BarUpdater barUpdater, String statusName) {
        this.barUpdater = barUpdater;
        this.statusName = statusName;
    }

    public void print() {
//        Log.i(PROJECT_LOG_TAG, "printing the next message " +statusName);
        printToBar(statusName);
        lockTheBar(timeoutMilSec);
    }

    protected void printToBar(String name) {
        barUpdater.printStatement(statusName);
    }

    protected void lockTheBar(long timeout) {
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
        Log.i(PROJECT_LOG_TAG, "creating new default message ="+ statusName);
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

class EmptyMessage extends BarStatement {
    public EmptyMessage(BarUpdater barUpdater) {
        super(barUpdater, "");
    }

    @Override
    public void print() {
        lockTheBar(1000);
    }
}