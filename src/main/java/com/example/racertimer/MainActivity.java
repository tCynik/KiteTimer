package com.example.racertimer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button butPrevTimer; // кнопка сброса на предыдущий таймер
    private Button butCurrTimer; // кнопка сброса текущего аткмера на начало
    private Button butNextTimer; // кнопка сброса на следующий таймер
    private TextView timerResult; // значение для отображения в приложении в формате мм:сс

    private int timerSec = 300; // текущий таймер в секундах
    private String timerMinSec; // значение стринговое для передачи на вывод в формате мм:сс

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        butPrevTimer = findViewById(R.id.but_prev_timer); // Заводим кнопки таймеров
        butCurrTimer = findViewById(R.id.but_curr_timer);
        butNextTimer = findViewById(R.id.but_next_timer);

        timerResult = findViewById(R.id.timer_min_sec);

        View.OnClickListener butPrevIsClicked = new View.OnClickListener() { // описываем нажатие первой кнопки
            @Override
            public void onClick(View view) {
                timerSec = 120;
            }
        };
        butPrevTimer.setOnClickListener(butPrevIsClicked);

        View.OnClickListener butCurrIsClicked = new View.OnClickListener() { // нажатие второй кнопки
            @Override
            public void onClick(View view) {
                timerSec = 60;
            }
        };
        butCurrTimer.setOnClickListener(butCurrIsClicked);

        View.OnClickListener butNextIsClicked = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerSec = 30;
            }
        };
        butNextTimer.setOnClickListener(butNextIsClicked);

        timerRunning(300);

    }

    public String calcTimeMinSec (int timerSec) {
        String result=null;
        int sec;
        int min;
        min = timerSec / 60;
        result = min + ":00";
        sec = timerSec % 60;
        if (sec !=0) result = min + ":" + sec;
        return result;
    }

    public void timerRunning(int timerMiliSec) {
        new CountDownTimer(timerMiliSec * 1000, 1000) {
            @Override
            public void onTick(long l) { // действия во время отсчета
                timerSec--;
                timerMinSec = calcTimeMinSec(timerSec);
                if (timerSec <= 0) timerMinSec = "GO!!!";
                timerResult.setText(timerMinSec.toString()); // выводим значение на экран
            }

            @Override
            public void onFinish() { // действия по окончании отсчета
                if (timerSec > 0) timerRunning(timerMiliSec);
            }
        }.start();
    }

}