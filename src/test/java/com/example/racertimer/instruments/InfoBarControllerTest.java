package com.example.racertimer.instruments;

import static org.junit.Assert.assertEquals;

import com.example.racertimer.InfoBarController;
import com.example.racertimer.TextViewController;

import org.junit.Test;

import java.util.Timer;
import java.util.TimerTask;

public class InfoBarControllerTest {
    String currentResult;

    TextViewController infoBarTVInterface = new TextViewController() {

        @Override
        public void updateTextView(String nexText) {
            currentResult = nexText;
        }

        @Override
        public String getTextFromView() {
            return "10:20.33";
        }
    };
    InfoBarController infoBarController = new InfoBarController(infoBarTVInterface);

    @Test
    public void checkCommandStop () throws Exception {
        infoBarController.updateTheBar("stop race");
        assertEquals("last: 10:20.33", currentResult);
    }

    @Test
    public void checkTimerCount() throws Exception {
        infoBarController.updateTheBar("12:41.02");
        assertEquals("12:41.02", currentResult);
    }

    @Test
    public void checkTimerOne() throws Exception {
        infoBarController.updateTheBar("timer one");
        assertEquals("ONE!!!", currentResult);
    }

    @Test
    public void checkStartWithNoDelay() throws Exception {
        infoBarController.updateTheBar("start");
        infoBarController.updateTheBar("12:41.02");
        assertEquals("GO! GO! GO!!!", currentResult);
    }

    @Test
    public void checkStartWithDelay() throws Exception {
        infoBarController.updateTheBar("start");
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                infoBarController.updateTheBar("12:41.02");
                cancel();
                assertEquals("12:41.02", currentResult);
            }
        },3000, 1);
    }
}
