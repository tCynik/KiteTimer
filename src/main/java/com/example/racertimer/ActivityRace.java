package com.example.racertimer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ActivityRace extends AppCompatActivity {
    private Activity thisActivity; // эта активность - для простоты перехода между экранами
    private TextView timerRace; // таймер гонки
    private TextView textTime; // переменная времени в левом вехнем углу
    private Button exitToMain;

    private String timerString = "00:00.00"; // переменная для вывода текущего секундомера чч:мм:сс.сот
    private int timerHour = 0; // переменная в часах
    private int timerMin = 0; // переменная счетчика в минутах
    private int timerSec = 0; // текущее значение таймера в сотых долей секунды

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);

        thisActivity = this;

        timerRace = findViewById(R.id.timer_race);
        textTime = findViewById(R.id.currentTime);
        exitToMain = findViewById(R.id.exit_to_main);

        timerRunning();
        // секундомер нужно будет откалибровать или переписать или хз - в эмуляторе время идет

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (".MainActivity");
                startActivity(intent);
            }
        };
        exitToMain.setOnClickListener(listener);

    }

    private void timerRunning () {
        new CountDownTimer(60000, 10) {

            @Override
            public void onTick(long l) {
                //timerRace.setText("blabla");

                timerSec++;
                if (timerSec > 600) {
                    timerSec -= 600;
                    timerMin ++;
                }
                if (timerMin == 60) {
                    timerMin = 0;
                    timerHour ++;
                }
                timerString = calcTimer(timerHour, timerMin, timerSec);

                timerRace.setText(timerString.toString());
            }

            @Override
            public void onFinish() {
                timerRunning();

            }
        }.start();
    }

    private String calcTimer (int timerHour, int timerMin, int timerSec) {
        timerString = timerSec / 10+ "." + (int) timerSec % 10;
        if (timerSec < 100 ) timerString = "" + 0 + timerString;
        timerString = timerMin + ":" + timerString;
        if (timerMin < 10 ) timerString = "" + 0 + timerString;
        if (timerHour !=0 ) timerString = timerHour + ":" + timerString;
        return timerString;
    }
}