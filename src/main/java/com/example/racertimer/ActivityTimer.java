package com.example.racertimer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityTimer extends AppCompatActivity implements View.OnClickListener {
    private Activity thisActivity; // эта активность - для простоты перехода между экранами
    private Button butPrevTimer; // кнопка сброса на предыдущий таймер
    private Button butCurrTimer; // кнопка сброса текущего аткмера на начало
    private Button butNextTimer; // кнопка сброса на следующий таймер
    private Button butMinus1Sec, butMinus5Sec, butPlus1Sec, butPlus5Sec; // кнопки корректировки времени
    private TextView timerResult; // значение для отображения в приложении в формате мм:сс
    private TextView textTime; // переменная времени в левом вехнем углу


    private int procedureTiming; // тип стартовой процедуры в минутах
    private int timerSec; // текущий таймер в секундах
    private int period; // участок времени, на котором значения счетчика по умолчанию начало
    private String timerString2Print; // значение стринговое для передачи на вывод в формате мм:сс
    private boolean flasher = false; // переменная для реализации мигания
    private boolean timerPaused = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        thisActivity = this;

        butPrevTimer = findViewById(R.id.but_prev_timer); // Заводим кнопки таймеров
            butPrevTimer.setOnClickListener((View.OnClickListener) thisActivity); // на вход идет текущее Activity - "this"
        butCurrTimer = findViewById(R.id.but_curr_timer);
            butCurrTimer.setOnClickListener((View.OnClickListener) thisActivity);
        butNextTimer = findViewById(R.id.but_next_timer);
            butNextTimer.setOnClickListener((View.OnClickListener) thisActivity);

        butMinus1Sec = findViewById(R.id.but_minus_1sec);
        butMinus5Sec = findViewById(R.id.but_minus_5sec);
        butPlus1Sec = findViewById(R.id.but_plus_1sec);
        butPlus5Sec = findViewById(R.id.but_plus_5sec);

        butMinus1Sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerSec -= 1;
                timerString2Print = calcTimeMinSec(timerSec);
                timerResult.setText(timerString2Print.toString());
            }
        });
        butMinus5Sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerSec -= 5;
                timerString2Print = calcTimeMinSec(timerSec);
                timerResult.setText(timerString2Print.toString());
            }
        });
        butPlus1Sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerSec += 1;
                timerString2Print = calcTimeMinSec(timerSec);
                timerResult.setText(timerString2Print.toString());
            }
        });
        butPlus5Sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerSec += 5;
                timerString2Print = calcTimeMinSec(timerSec);
                timerResult.setText(timerString2Print.toString());
            }
        });

        timerResult = findViewById(R.id.timer_min_sec); // привязка таймера к полю вывода таймера

        timerResult.setOnClickListener(new View.OnClickListener() { // пауза таймера вручную
            @Override
            public void onClick(View view) {
                if (timerPaused) {
                    timerPaused = false;
                } else timerPaused = true;
            }
        });

        textTime = findViewById(R.id.currentTime); // привязываем переменную полю в активити

        Context context = ActivityTimer.this; // выводим Toast подсказку
        Toast.makeText(context, "tap the timer to pause", Toast.LENGTH_LONG).show();

        procedureTiming = 5; // по умолчанию тайминг 5 минут
        Intent catchProcedureTiming = getIntent(); // получаем данные по таймингу процедуры из шлавного меню
        if (catchProcedureTiming.hasExtra("procedureTiming")) { // проверка, получили ли данные с нужным ключом
            procedureTiming = catchProcedureTiming.getIntExtra("procedureTiming", 5); //принимаем данные
        }
        timerSec = procedureTiming * 60; // задаем начальное значение таймера
        timerRunning(procedureTiming * 60);

    }

    public String calcTimeMinSec (int timerSec) {
        String result=null;
        int sec;
        int min;
        min = timerSec / 60;
        result = min + ":00";
        sec = timerSec % 60;
        if (sec !=0) result = min + ":" + sec;
        if (sec < 10) result = min + ":0" + sec;
        return result;
    }

    public void timerRunning(long timerMiliSec) {
        new CountDownTimer(timerMiliSec * 1000, 1000) {
            @Override
            public void onTick(long l) { // действия во время отсчета
                if (timerPaused == false) timerSec--;
                period = checkPeriod(timerSec); // определяем период, в котором таймер
                buttonsNames(period); // выставляем надписи на кнопках в зависимости от периода

                timerString2Print = calcTimeMinSec(timerSec); // получаем стринговое отобржение таймера
                if (timerSec <= 0) timerString2Print = "GO!!!";
                timerResult.setText(timerString2Print.toString()); // выводим значение на экран

                if (timerSec == 0) {
                    Intent intent = new Intent (thisActivity, ActivityRace.class); // запускаем активность "Race"
                    startActivity(intent); // запуск активити
                }
            }

            @Override
            public void onFinish() { // действия по окончании отсчета
                if (timerSec > 0) timerRunning(timerSec * 1000);
            }
        }.start();
    }

    public int checkPeriod(int timer) {
        int period = 1; // Значения периода: 0 - старт, 1 - от 0 до 1, 2 - от 1 до 2, 3 - от 2 до макс
        if (timer > 60 & timer < 120) period = 2;
        if (timer > 120 ) period = 3;
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

    public boolean flashing (boolean flasher) {
        if (flasher) flasher = false;
        else flasher = true;
        return flasher;
    }

    @Override
    public void onClick(View view) { // отработка действия кнопок
        switch (view.getId()) {
            case R.id.but_prev_timer: // при нажатии первой кнопки
                switch (period) { // в зависимости от периода
                    case 3: { // если в начальном периоде
                        Intent intent = new Intent (thisActivity, MainActivity.class);
                        startActivity(intent); // выход на предыдущий экран
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
                break;
            case R.id.but_curr_timer:
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
                break;
            case R.id.but_next_timer:
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
                        Intent intent = new Intent (thisActivity, ActivityRace.class);
                        startActivity(intent); // начинаем гонку
                        break;
                    }
                }
                break;
            default:
                break;

        }
    }
}