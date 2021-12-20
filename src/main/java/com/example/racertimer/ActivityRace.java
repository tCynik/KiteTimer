package com.example.racertimer;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.racertimer.GPSContent.LocListenerInterface;
import com.example.racertimer.GPSContent.MainLocal;

public class ActivityRace extends AppCompatActivity implements LocListenerInterface {
    private Activity thisActivity; // эта активность - для простоты перехода между экранами
    private TextView timerRace; // таймер гонки
    private TextView textTime; // переменная времени в левом вехнем углу
    private Button exitToMain;

    private String timerString = "00:00.00"; // переменная для вывода текущего секундомера чч:мм:сс.сот
    private int timerHour = 0; // переменная в часах
    private int timerMin = 0; // переменная счетчика в минутах
    private int timerSec = 0; // текущее значение таймера в сотых долей секунды

    private TextView speedTV, courseTV; // переменные для привызки полей скорости и курса

    private int velosity = 0; // скорость в кмч
    private int course; // курс в градусах
    private int countLocarionChanged = 0; // счетчик сколько раз изменялось геоположение


    private MainLocal mainLocal; // обьект для работы с главным классом по GPS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);

        thisActivity = this;

        timerRace = findViewById(R.id.timer_race);
        textTime = findViewById(R.id.currentTime);
        exitToMain = findViewById(R.id.exit_to_main);

        speedTV = findViewById(R.id.speed);
        courseTV = findViewById(R.id.course);

        mainLocal = new MainLocal();
        timerRunning();
        ///// продолжение урока с 11:50

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
        new CountDownTimer(60000, 1000) {

            @Override
            public void onTick(long l) {
                //timerRace.setText("blabla");

                timerSec++;
                if (timerSec > 59) {
                    timerSec -= 60;
                    timerMin ++;
                }
                if (timerMin == 60) {
                    timerMin = 0;
                    timerHour ++;
                }
                timerString = calcTimer(timerHour, timerMin, timerSec);

                timerRace.setText(timerString.toString());

                speedTV.setText(String.valueOf(velosity));

                courseTV.setText(String.valueOf(countLocarionChanged));
//                courseTV.setText(countLocarionChanged);
            }

            @Override
            public void onFinish() {
                timerRunning();

            }
        }.start();
    }

    private String calcTimer (int timerHour, int timerMin, int timerSec) {
//        timerString = timerSec / 10+ "." + (int) timerSec % 10; // тут таймер с сотыми секунды, но там время неверное
//        if (timerSec < 100 ) timerString = "" + 0 + timerString;
//        timerString = timerMin + ":" + timerString;
        if (timerSec < 10) timerString = timerMin + ":0" + timerSec;
        else timerString = timerMin + ":" + timerSec;
        if (timerMin < 10 ) timerString = "0" + timerString;
        if (timerHour !=0 ) timerString = timerHour + ":" + timerString;
        return timerString;
    }

    @Override
    public void whenLocationChanged(Location location) {
        velosity = (int) location.getSpeed(); // когда изменилось местоположение, получаем скорость
//        speedTV.setText(String.valueOf(velosity));
        countLocarionChanged ++;
//        speedTV.setText(String.valueOf(countLocarionChanged));

    }
}