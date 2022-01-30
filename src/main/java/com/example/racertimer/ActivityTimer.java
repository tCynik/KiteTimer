package com.example.racertimer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.racertimer.multimedia.Voiceover;

public class ActivityTimer extends AppCompatActivity implements View.OnClickListener {
    private final static String PROJECT_LOG_TAG = "racer_timer";
    private Activity thisActivity; // эта активность - для простоты перехода между экранами
    private Button butPrevTimer; // кнопка сброса на предыдущий таймер
    private Button butCurrTimer; // кнопка сброса текущего аткмера на начало
    private Button butNextTimer; // кнопка сброса на следующий таймер
    private Button butMinus1Sec, butMinus5Sec, butPlus1Sec, butPlus5Sec; // кнопки корректировки времени
    private TextView timerResult; // значение для отображения в приложении в формате мм:сс
    private TextView textTime; // переменная времени в левом вехнем углу


    private int procedureTiming; // тип стартовой процедуры в минутах
    private int timerSec; // текущий таймер в секундах
    private int period = 1; // участок времени, на котором значения счетчика по умолчанию начало
    private String timerString2Print; // значение стринговое для передачи на вывод в формате мм:сс
    private boolean flasher = false; // переменная для реализации мигания
    private boolean timerPaused = false;

    private Voiceover voiceover;

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

        timerResult = findViewById(R.id.timer_min_sec); // привязка таймера к полю вывода таймера

        /** слушатели кнопок*/ ///// сделать моментальное обновление счетчика при нажатии кнопки (бесит)
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

        /** пауза отсчета при нажатии на счетчик*/
        timerResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerPaused) {
                    timerPaused = false;
                } else {
                    timerPaused = true;
                    voiceover.makeSound(voiceover.pause);
                }
            }
        });

        textTime = findViewById(R.id.currentTime); // время и дата

        Context context = ActivityTimer.this; // выводим Toast подсказку про паузу таймера
        Toast.makeText(context, "tap the timer to pause", Toast.LENGTH_LONG).show();

        /** выставляем тайминг отсчета на основе выбранного в MainActivity типа процедуры */
        procedureTiming = 5; // по умолчанию тайминг 5 минут
        Intent catchProcedureTiming = getIntent(); // получаем данные по таймингу процедуры из шлавного меню
        if (catchProcedureTiming.hasExtra("procedureTiming")) { // проверка, получили ли данные с нужным ключом
            procedureTiming = catchProcedureTiming.getIntExtra("procedureTiming", 5); //принимаем данные
        }
        timerSec = procedureTiming * 60; // задаем начальное значение таймера

        /** запускаем озвучку */
        voiceover = new Voiceover(this);

        /** запуск счетчика таймера*/
        timerRunning(procedureTiming * 60);
    }

    /** счетчик таймера */ //////// вывести в отдельный поток??
    public void timerRunning(long timerMiliSec) {
        new CountDownTimer(timerMiliSec * 1000, 1000) {
            @Override
            public void onTick(long l) { // действия во время отсчета
                if (timerSec > 0) onTimerTicked();
//                Log.i("Race", " Thread: "+Thread.currentThread().getName() + " timerSec = " +timerSec);

            }
            @Override
            protected void finalize() throws Throwable {
                super.finalize();
            }
            @Override
            public void onFinish() { // действия по окончании отсчета
                if (timerSec > 0) timerRunning(timerSec * 1000);
            }
        }.start();
    }

    /** обновление и обработка данных таймера*/
    private void onTimerTicked () { // таймер обновился
        if (timerPaused == false) {
            timerSec -- ;
            switch (timerSec) {
                case 130: voiceover.makeSound(voiceover.twoMinutesReady); break;
                case 120: voiceover.makeSound(voiceover.twoMinutes); break;
                case 70: voiceover.makeSound(voiceover.oneMinutesReady); break;
                case 60: voiceover.makeSound(voiceover.oneMinute); break;
                case 50: voiceover.makeSound(voiceover.fivety); break;
                case 40: voiceover.makeSound(voiceover.fourty); break;
                case 30: voiceover.makeSound(voiceover.thrity); break;
                case 20: voiceover.makeSound(voiceover.twenty); break;
                case 10: voiceover.makeSound(voiceover.ten); break;
                case 9: voiceover.makeSound(voiceover.nine); break;
                case 8: voiceover.makeSound(voiceover.eight); break;
                case 7: voiceover.makeSound(voiceover.seven); break;
                case 6: voiceover.makeSound(voiceover.six); break;
                case 5: voiceover.makeSound(voiceover.five); break;
                case 4: voiceover.makeSound(voiceover.four); break;
                case 3: voiceover.makeSound(voiceover.three); break;
                case 2: voiceover.makeSound(voiceover.two); break;
                case 1: voiceover.makeSound(voiceover.one); break;
                case 0: voiceover.makeSound(voiceover.startSound);
                startRace(); break;
                default: break;
            }
        }
        period = checkPeriod(timerSec);
        buttonsNames(period); // выставляем надписи на кнопках в зависимости от периода

        timerString2Print = calcTimeMinSec(timerSec); // получаем стринговое отобржение таймера
        if (timerSec <= 0) timerString2Print = "GO!!!";
        timerResult.setText(timerString2Print.toString()); // выводим значение на экран
    }

    /** получение стринговых показаний для TextView */
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

    /** обработка запуска гонки */
    void startRace(){
        Intent intent = new Intent (thisActivity, ActivityRace.class); // запускаем активность "Race"
        Log.i("Race", Thread.currentThread().getName() + timerSec);
        startActivity(intent); // запуск активити
    }

    /** Вычисление текущей стадии стартовой процедуры */
    public int checkPeriod(int timer) {
//        int period = 1; // Значения периода: 0 - старт, 1 - от 0 до 1, 2 - от 1 до 2, 3 - от 2 до макс
        if (timer < 60) period = 1;
        if (timer > 60 & timer < 120) period = 2;
        if (timer > 120 ) period = 3;
        return period;
    }

    /** Переименование кнопок в зависимости от стадии стартовой процедуры */
    public void buttonsNames (int period) { // метод именования кнопок экрана в зависимости от периода времени
        butPrevTimer.setBackgroundColor(Color.BLUE);
        butCurrTimer.setBackgroundColor(Color.BLUE);
        switch (period) {
            case 3: {
                butPrevTimer.setText("back");
                butCurrTimer.setText(procedureTiming + " min");
                butNextTimer.setText("2 min");
                butNextTimer.setBackgroundColor(Color.BLUE);
                break;
            }
            case 2: {
                butPrevTimer.setText(procedureTiming + " min");
                butCurrTimer.setText("2 min");
                butNextTimer.setText("1 min");
                butNextTimer.setBackgroundColor(Color.BLUE);
                break;
            }
            case 1: {
                butPrevTimer.setText("2 min");
                butCurrTimer.setText("1 min");
                butNextTimer.setText("START");
                butNextTimer.setBackgroundColor(Color.RED);
                break;
            }
            default: break;
        }
    }

    /** обработчик действия кнопок в зависимости от периода стартовой процедуры */
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
                        timerSec = 0; // обнуляем таймер на старт
                        Intent intent = new Intent (thisActivity, ActivityRace.class); // запускаем активность "Race"
                        startActivity(intent); // запуск активити
                        finish();
                        break;
                    }
                }
                break;
            default:
                break;

        }
    }
}