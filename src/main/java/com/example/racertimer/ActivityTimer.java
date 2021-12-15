package com.example.racertimer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ActivityTimer extends AppCompatActivity {
    private Button butPrevTimer; // кнопка сброса на предыдущий таймер
    private Button butCurrTimer; // кнопка сброса текущего аткмера на начало
    private Button butNextTimer; // кнопка сброса на следующий таймер
    private TextView timerResult; // значение для отображения в приложении в формате мм:сс
    private TextView textTime; // переменная времени в левом вехнем углу


    private int procedureTiming; // тип стартовой процедуры в минутах
    private int timerSec = 180; // текущий таймер в секундах
    private int period = 2; // участок времени, на котором значения счетчика по умолчанию начало
    private String timerString2Print; // значение стринговое для передачи на вывод в формате мм:сс

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        butPrevTimer = findViewById(R.id.but_prev_timer); // Заводим кнопки таймеров
        butCurrTimer = findViewById(R.id.but_curr_timer);
        butNextTimer = findViewById(R.id.but_next_timer);

        textTime = findViewById(R.id.currentTime);


        timerResult = findViewById(R.id.timer_min_sec); // привязка таймера к полю вывода таймера

        procedureTiming = 5;

        View.OnClickListener butPrevIsClicked = new View.OnClickListener() { // описываем нажатие ПЕРВОЙ кнопки
            @Override
            public void onClick(View view) {
                switch (period) { // в зависимости от того, к каком периоде:
                    case 3: { // если в начальном периоде
                        // выход на предыдущий экран
                        break;
                    }
                    case 2: { // если в промежуточном периоде
                        timerSec = 60 * procedureTiming; // начало таймера заново
                        break;
                    }
                    case 1: { // если в предстартовом периоде
                        timerSec = 60 * 2; // переход на двухминутный таймер
                        break;
                    }
                    default: break;
                }
            }
        };
        butPrevTimer.setOnClickListener(butPrevIsClicked);

        View.OnClickListener butCurrIsClicked = new View.OnClickListener() { // нажатие ВТОРОЙ кнопки
            @Override
            public void onClick(View view) {
                switch (period) { // в зависимости от того, к каком периоде:
                    case 3: {
                        timerSec = 60 * procedureTiming; // перезапускаем таймер заново
                        break;
                    }
                    case 2: {
                        timerSec = 60 * 2; // перезапускаем двухминутный
                        break;
                    }
                    case 1: {
                        timerSec = 60; // перезапускаем минутный
                        break;
                    }
                }
            }
        };
        butCurrTimer.setOnClickListener(butCurrIsClicked);

        View.OnClickListener butNextIsClicked = new View.OnClickListener() { // нажатие ТРЕТЬЕЙ кнопки
            @Override
            public void onClick(View view) {
                switch (period) { // в зависимости от того, к каком периоде:
                    case 3: {
                        timerSec = 60 * 2; // запускаем двухминутный
                        break;
                    }
                    case 2: {
                        timerSec = 60; // запускаем минутный
                        break;
                    }
                    case 1: {
                        timerSec = 0; // стартуем
                        break;
                    }
                }
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
                period = checkPeriod(timerSec); // определяем период, в котором таймер
                textTime.setText(period+"");
                buttonsNames(period); // выставляем надписи на кнопках в зависимости от периода

                timerString2Print = calcTimeMinSec(timerSec); // получаем стринговое отобржение таймера
                if (timerSec <= 0) timerString2Print = "GO!!!";
                timerResult.setText(timerString2Print.toString()); // выводим значение на экран
            }

            @Override
            public void onFinish() { // действия по окончании отсчета
                if (timerSec > 0) timerRunning(timerMiliSec);
            }
        }.start();
    }

    public int checkPeriod(int timer) {
        int period = 1; // Значения периода: 0 - старт, 1 - от 0 до 1, 2 - от 1 до 2, 3 - от 2 до макс
        if (timer > 1 & timer <2) period = 2;
        if (timer > 2 ) period = 3;
        return period;
    }

    public void buttonsNames (int period) { // метод именования кнопок экрана в зависимости от периода времени
        switch (period) {
            case 3: {
                butPrevTimer.setText("back");
                butCurrTimer.setText(procedureTiming + " min");
                butNextTimer.setText("2 min");
                break;
            }

            case 2: {
                butPrevTimer.setText(procedureTiming + " min");
                butCurrTimer.setText("2 min");
                butNextTimer.setText("1 min");
                break;
            }

            case 1: {
                butPrevTimer.setText("2 min");
                butCurrTimer.setText("1 min");
                butNextTimer.setText("START");
                break;
            }
            default: break;
        }

    }
}