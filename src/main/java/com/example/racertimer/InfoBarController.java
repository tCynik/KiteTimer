package com.example.racertimer;

import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class InfoBarController {
    private TextView textView;
    private boolean isGpsConnected = false;

    public InfoBarController(TextView textView) {
        this.textView = textView;
        greetings();
    }

    public void updateTheBar(String nextBarStatus) {
        textView.setText(nextBarStatus);
    }

    void greetings() {
        updateTheBar("Hello!");
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isGpsConnected) updateTheBar("Go chase!");
                else updateTheBar("No GPS");
                cancel();
            }
        }, 4000, 1);
    }

    void onGpsConnected () {
        isGpsConnected = true;
        updateTheBar("Go chase!");
    }

}
