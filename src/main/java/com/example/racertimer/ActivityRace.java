package com.example.racertimer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.racertimer.GPSContent.LocListener;
import com.example.racertimer.GPSContent.LocListenerInterface;

// из этой активити начинаем работу с GPS.
// испрашиваем разрешение на получение геоданных
// если разрешение получено - сознаем экземпляр класса, отвечающего за получение геоданных

public class ActivityRace extends AppCompatActivity { // добавить интерфейс
    private Activity thisActivity; // эта активность - для простоты перехода между экранами
    private TextView timerRace; // таймер гонки
    private TextView textTime; // переменная времени в левом вехнем углу
    private Button buttonExitToMain;

    private String timerString = "00:00.00"; // переменная для вывода текущего секундомера чч:мм:сс.сот
    private int timerHour = 0; // переменная в часах
    private int timerMin = 0; // переменная счетчика в минутах
    private int timerSec = 0; // текущее значение таймера в сотых долей секунды

    private TextView speedTV, maxSpeedTV, courseTV, countLocalChangedTV; // переменные для привызки полей скорости и курса

    private LocListener locListener; // слушатель геолокации в отдельном потоке
    private LocListenerInterface locListenerInterface;

    private int velosity = 0; // скорость в кмч
    private int maxSpeed = 0; // максимальная зарегистрированная скорость
    private int course; // курс в градусах
    private boolean isFirstIteration = true; // флаг о том, что это первая итерация для выставления первичных цифр
    private int countLocationChanged = 0; // счетчик сколько раз изменялось геоположение

    private LocationManager locationManager;

    private LocationListener locationListener;

    private AsyncTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);

        thisActivity = this;

        timerRace = findViewById(R.id.timer_race);
        textTime = findViewById(R.id.currentTime);
        buttonExitToMain = findViewById(R.id.exit_to_main);

        speedTV = findViewById(R.id.speed);
        maxSpeedTV = findViewById(R.id.max_speed);
        courseTV = findViewById(R.id.course);
        countLocalChangedTV = findViewById(R.id.counter_loc_changed);

        /** запускаем таймер */
        timerRunning(); // запускаем отсчет и обработку таймера

        initLocationListener(); // запускаем процедуру приема данных GPS
        if (checkPermission()) // если есть все разрешения, запускаем прием геолокации с передачей в поток листенеар
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locListener.locationListener);
        locListener.start();


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (".MainActivity");
                startActivity(intent);
            }
        };
        buttonExitToMain.setOnClickListener(listener);
    }

    /** усреднитель курса и обработчик перехода через нулевой азимут */
    private int courseAverage (int newCourse) {
        int deltaCourse = (newCourse - course); // разница курсов: "курс новый (newCourse) - курс старый (course)"
        if (deltaCourse > 180) deltaCourse = deltaCourse - 360; //newCourse - (360  - course);
        if (deltaCourse < -180) deltaCourse = 360 + deltaCourse;

        course = (int) (course + (deltaCourse * 0.75)) ; // усреднение - приращиваем на 75% от разницы
        if (course > 360) course = course - 360;
        if (course < 0) course = course + 360;
        Log.i("ActivityRace", "averageCourse = " + course);
        return course;
    }


    /** Счетчик таймера*/
    private void timerRunning () {
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                onTimerTicked();
            }
            @Override
            public void onFinish() {
                timerRunning();

            }
        }.start();
    }

    /** обработка изменения таймера*/
    private void onTimerTicked () {
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
    }

    /** Калькулятор гоночного таймера */
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

    /** Запуск приема данных геолокации */
    private void initLocationListener () {
        /** запускаем сервис геолокации */
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        /** создаем отдельный поток - листенер */
        locListener = new LocListener();
        locListenerInterface = new LocListenerInterface() {
            @Override
            public void whenLocationChanged(Location location) {
                Log.i("Main", " Thread: " + Thread.currentThread().getName() + "locListInterf get new info");
                double tempVelosity;
                if (location.hasSpeed()) {
                    tempVelosity = (double) location.getSpeed()*3.6;
                    velosity = (velosity + (int) tempVelosity) / 2;
                    course = courseAverage((int) location.getBearing()); // с учетом усреднения
                } else velosity = 0;
                countLocationChanged++;
                countLocalChangedTV.setText(String.valueOf(countLocationChanged));
                speedTV.setText(String.valueOf(velosity));
                if (velosity > maxSpeed) maxSpeed = velosity;
                maxSpeedTV.setText(String.valueOf(maxSpeed));
                courseTV.setText(String.valueOf(course));
                textTime.setText(String.valueOf(location.getTime()));
            }
        };

        /** связываем интерфейс листенера */
        locListener.setLocationListenerInterface(locListenerInterface);

        /** процедура запроса разрешений и запуска приема сигналов GPS */
        if (!checkPermission()) { // если разрешения нет,
            askPermission(); //  запрашиваем разрешение. Далее если не будет разрешения,
        }
    }

        /** обработчик проверки наличия разрешений     */
        public boolean checkPermission() { // проверяем наличие разрешения на геоданные
            // если разрешения нет:
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&  // если версия СДК выше версии M (API 23)
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) // если разрешения нет, то запускаем запрос разрешения, код ответа 100
            {
                return false; // если разрешения нет, возвращаем false
            } else
                return true; // в противном случае разрешение есть, возвращаем true
        }

        /** запроса разрешения на геолокацию     */
        private void askPermission() { // запрос разрешения
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, // запрашиваем разрешение
                    Manifest.permission.ACCESS_FINE_LOCATION}, 100); // ключ 100, такой же как ниже
        }

}
