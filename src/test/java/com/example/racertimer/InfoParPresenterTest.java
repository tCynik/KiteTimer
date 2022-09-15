package com.example.racertimer;

import static org.junit.Assert.assertEquals;

import android.os.SystemClock;

import org.junit.Test;

import java.util.ArrayList;

public class InfoParPresenterTest {
    String[] sampleHistory;
    ArrayList<String> messagesHistory = new ArrayList<>();

    MessagesChecker messagesChecker = new MessagesChecker() {
        @Override
        public boolean checkHistory(ArrayList<String> history, String[] sample) {
            boolean isMessagesHistoryCorrect = true;
            if (sample.length != history.size()) {
                isMessagesHistoryCorrect = false;
                //Log.i(LOG_TAG, "history length incorrect! it's " +history.size()+", must be "+sample.length);
            }
            else {
                for (int i = 0; i < sample.length; i++) {
                    if (!sample[i].equals(history.get(i))) {
                        //Log.i(LOG_TAG, "message #"+i+" is incorrect! It is /"+history.get(i)+"/, must be /"+sample[i]+"/");
                        isMessagesHistoryCorrect = false;
                    }
                }
            }
            messagesHistory.clear();
            return isMessagesHistoryCorrect;
        }

        public String getLastMessage(ArrayList<String> history) {
            return history.get(history.size()-1);
        }

    };

    TextViewController testingInfoBarController = new TextViewController() {
        @Override
        public void updateTextView(String nexText) {
            messagesHistory.add(nexText);
        }

        @Override
        public String getTextFromView() {
            return null;
        }
    };

    private void initTestingPresenter() {
        InfoBarPresenter testingPresenter = new InfoBarPresenter();
        testingPresenter.setInfoBarTVInterface(testingInfoBarController);
    }

    @Test
    public void statusStartBarWorking() throws Exception{
        initTestingPresenter();
        sampleHistory = new String[]{"Hello!"};
        boolean checkigFlag = messagesChecker.checkHistory(messagesHistory, sampleHistory);
        assertEquals(true, checkigFlag);
    }

    @Test
    public void lastMessageItOneNoTimeout() throws Exception {
        initTestingPresenter();
        String correctAnswer = "Hello!";
        String answer = messagesHistory.get(messagesHistory.size() - 1);
        assertEquals(correctAnswer, answer);
    }

    @Test
    public void lastMessageItTwoNoTimeout() throws Exception {
        initTestingPresenter();
        testingInfoBarController.updateTextView("ready to go");
        String correctAnswer = "Hello!";
        String answer = messagesHistory.get(messagesHistory.size() - 1);
        assertEquals(correctAnswer, answer);
    }


    @Test
    public void regularMessageNoTimeout() throws Exception {
        initTestingPresenter();
        sampleHistory = new String[]{"Hello!", "Go chase!"};
        testingInfoBarController.updateTextView("ready to go");
        boolean flag = messagesChecker.checkHistory(messagesHistory, sampleHistory);
        assertEquals(true, flag);
    }

    @Test
    public void regularMessageWithTimeoutNoEnded() throws Exception {
        sampleHistory = new String[]{"Hello!"};
        testingInfoBarController.updateTextView("greetings");
        testingInfoBarController.updateTextView("gps");
        boolean flag = messagesChecker.checkHistory(messagesHistory, sampleHistory);
        assertEquals(true, flag);
    }

    @Test
    public void regularMessageWithTimeoutIsEnded() throws Exception {
        sampleHistory = new String[]{"Hello!", "GPS online"};
        testingInfoBarController.updateTextView("greetings");
        testingInfoBarController.updateTextView("gps");
        SystemClock.sleep(4000);
        boolean flag = messagesChecker.checkHistory(messagesHistory, sampleHistory);
        assertEquals(true, flag);
    }

}

interface MessagesChecker {
    public boolean checkHistory(ArrayList<String> history, String[] sample);
    String getLastMessage(ArrayList<String> history);
}
