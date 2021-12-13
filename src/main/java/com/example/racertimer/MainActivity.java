package com.example.racertimer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button butPrevTimer; // кнопка сброса на предыдущий таймер
    private Button butCurrTimer; // кнопка сброса текущего аткмера на начало
    private Button butNextTimer; // кнопка сброса на следующий таймер

    private int timerMiliSec; // переменная текущего таймера в миллисекундах
    private int timerSec; // итоговый таймер в секундах
    private String timerMinSec; // значение стринговое для передачи на вывод в формате мм:сс
    private TextView timerResult; // значение для отображения в приложении в формате мм:сс

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
                timerMiliSec = 120;
                timerMinSec = "2:00";
                timerResult.setText(timerMinSec.toString());
            }
        };
        butPrevTimer.setOnClickListener(butPrevIsClicked);

        View.OnClickListener butCurrIsClicked = new View.OnClickListener() { // нажатие второй кнопки
            @Override
            public void onClick(View view) {
                timerMiliSec = 60;
                timerMinSec = "1:00";
                timerResult.setText(timerMinSec.toString());
            }
        };
        butCurrTimer.setOnClickListener(butCurrIsClicked);

        View.OnClickListener butNextIsClicked = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerMiliSec = 0;
                timerMinSec = "0:30";
                timerResult.setText(timerMinSec.toString());
            }
        };
        butNextTimer.setOnClickListener(butNextIsClicked);



    }
}