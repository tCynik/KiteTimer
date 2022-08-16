package com.example.racertimer;

import static org.junit.Assert.assertEquals;

import android.os.SystemClock;
import android.util.Log;

import org.junit.Test;

import java.util.ArrayList;

public class InfoParPresenterTest {
    final String LOG_TAG = "InfoParPresenterTest";
    String[] sampleHistory;
    ArrayList<String> messagesHistory = new ArrayList<>();

    MessagesChecker messagesChecker = new MessagesChecker() {
        @Override
        public boolean checkHistory(ArrayList<String> history, String[] sample) {
            boolean messagesHistoryCorrect = true;
            if (sample.length != history.size()) {
                messagesHistoryCorrect = false;
                //Log.i(LOG_TAG, "history length incorrect! it's " +history.size()+", must be "+sample.length);
            }
            else {
                for (int i = 0; i < sample.length; i++) {
                    if (!sample[i].equals(history.get(i))) {
                        //Log.i(LOG_TAG, "message #"+i+" is incorrect! It is /"+history.get(i)+"/, must be /"+sample[i]+"/");
                        messagesHistoryCorrect = false;
                    }
                }
            }
            messagesHistory.clear();
            return messagesHistoryCorrect;
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

    InfoBarPresenter testingPresenter = new InfoBarPresenter(testingInfoBarController);

    @Test
    public void regularMessageNoTimeout() throws Exception {
        Log.i(LOG_TAG, " checking ");
        sampleHistory = new String[]{"Hello!"};
        testingInfoBarController.updateTextView("greetings");
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
}
