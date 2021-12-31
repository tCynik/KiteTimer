package com.example.racertimer;

import android.Manifest;
import android.app.Activity;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

        timerRunning();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double tempVelosity;
                if (location.hasSpeed()) {
                    tempVelosity = (double) location.getSpeed()*3.6;
                    velosity = (velosity + (int) tempVelosity) / 2;
                    course = courseAverage((int) location.getBearing()); // с учетом усреднения
                } else velosity = 0;
//                if (isFirstIteration) { // если это первая итерация - выставляем цифры
//                    isFirstIteration = false;
//                    velosity = 0;
//                    lastCourse = course;
//                }
                countLocationChanged++;
                countLocalChangedTV.setText(String.valueOf(countLocationChanged));
                speedTV.setText(String.valueOf(velosity));
                if (velosity > maxSpeed) maxSpeed = velosity;
                maxSpeedTV.setText(String.valueOf(maxSpeed));
                courseTV.setText(String.valueOf(course));
                textTime.setText(String.valueOf(location.getTime()));
            }
        };

//        task = new LocationAsyncTask();
//        task.execute();

        if (checkLokalPermissoin()) { // проверяем разрешения на GPS, если нет - запрашиваем
//////// задвоение запроса на разрешение, нужно убирать половину
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
            Log.i("ActivityRace", "getSystemService(LOCATION_SERVICE)");
//            locListener.runLocationListener(this, locationManager);
//            Log.i("ActivityRace", "runLocationListener");

        }
//        locListener.setLocListenerInterface(new LocListenerInterface() {
//            @Override
//            public void whenLocationChanged(Location location) {
//                if (location.hasSpeed()) {
//                    velosity = (int) location.getSpeed();
//                    course = (int) location.getBearing();
//                } else velosity = 0;
//                speedTV.setText(String.valueOf(velosity * 3.6));
//                courseTV.setText(String.valueOf(course));
//            }
//        });


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (".MainActivity");
                startActivity(intent);
            }
        };
        buttonExitToMain.setOnClickListener(listener);

    }

    private int courseAverage (int newCourse) {
        int deltaCourse = (newCourse - course); // разница курсов: "курс новый (newCourse) - курс старый (course)"
//        Log.i("ActivityRace", "course = "+course+", newCourse1 = "+ newCourse+ ", deltaCourse = " + deltaCourse);
        if (deltaCourse > 180) deltaCourse = deltaCourse - 360; //newCourse - (360  - course);
        if (deltaCourse < -180) deltaCourse = 360 + deltaCourse;
//        Log.i("ActivityRace", "deltaCourse = " + deltaCourse);

        course = (int) (course + (deltaCourse * 0.75)) ; // усреднение - приращиваем на 75% от разницы
        if (course > 360) course = course - 360;
        if (course < 0) course = course + 360;
        Log.i("ActivityRace", "averageCourse = " + course);
        return course;
    }

    private boolean checkLokalPermissoin() {
        boolean flag= false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&  // если версия СДК выше версии M (API 23)
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        { // если разрешения нет, то запускаем запрос разрешения, код ответа 100
            requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, // запрашиваем разрешение
                    Manifest.permission.ACCESS_FINE_LOCATION}, 100); // ключ 100, такой же как ниже
        } else // в противном случае (если разрешения есть)
        flag = true; // меняем флаг

        if (flag) Toast.makeText(this, "GPS permission OK", Toast.LENGTH_LONG ).show(); // выводим сообщение об отсутствии разрешения га GPS
        else Toast.makeText(this, "no GPS permission", Toast.LENGTH_LONG ).show();
            return flag;
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

}