package com.example.racertimer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.racertimer.Instruments.LocationService;

////////////// это меню главного экрана, в котором выбираем тип стартовой процедуры - 5 минут, 3 минуты, немедленный старт.
////////////// после выбора типа процедуры открывается окно стартового таймера:
////////////// после нажатия Instant start открывается экран гонки
////////////// при запуске главного экрана происходит запуск работы GPS модуля

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String PROJECT_LOG_TAG = "racer_timer";
    final String BROADCAST_ACTION = "com.example.racertimer.action.new_location"; // значение для фильтра приемника

    private Button butTimer5Min, butTimer3Min, butTimerInstant, butForecast; // кнопки выбора стартовой процедуры

    private TextView textTime, changeMain, velMain; // переменная времени в левом вехнем углу (дата и время)

    private Intent intentLocationService; // интент для создания сервиса геолокации

    public Activity MainActivityThis;
    public boolean flagGps = true; // флаг работы Loclistener
    private int velosity = 0; // скорость в кмч
    private int course; // курс в градусах
    private int countLocationChanged = 0; // счетчик сколько раз изменялось геоположение
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

        Context context = this;

        /////// при выходе из приложения сделать меню выхода. в меню сообщить что с закрытием программы
/////// убиваем сервис если нажато "да"

        if (!checkPermission()) askPermission(); // проверяем разрешения на геолокацию и зпрашиваем
        createLocationService(); // запускаем сервис для полученич геоданных

    }
    /** Слушатель кнопок */
    @Override
    public void onClick(View view) { // view - элемент, на который произошло нажатие (его id)
        Context context = this; // создаем контекст относительно текущего активити
        Class nextActivity = ActivityTimer.class; // активити, в которое будем переходить чаще всего
        switch (view.getId()) {
            case R.id.but_5mins:
                intent = new Intent(context, nextActivity); // по умолчанию 5 минут (ничего не передаем)
                break;
            case R.id.but_3mins:
                intent = new Intent(context, nextActivity); // при трехминутке передаем тайминг 3 мин
                intent.putExtra("procedureTiming", 3);
                break;
            case R.id.but_instant:
                intent = new Intent(context, ActivityRace.class);
                break;
            case R.id.but_forecast:

                intent = new Intent(context, ActivityForecast.class);
            break;
            default:
                break;
        }
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /** Определяем View элементы UI */
    private void findViews() {
        butTimer5Min = findViewById(R.id.but_5mins); // Заводим кнопки таймеров
        butTimer5Min.setOnClickListener(this);
        butTimer3Min = findViewById(R.id.but_3mins);
        butTimer3Min.setOnClickListener(this);
        butTimerInstant = findViewById(R.id.but_instant);
        butTimerInstant.setOnClickListener(this);
        butForecast = findViewById(R.id.but_forecast);
        butForecast.setOnClickListener(this);
        textTime = findViewById(R.id.currentTime);
    }

    /** Настраиваем и запускаем сервис для приема и трансляции данных геолокации */
    private void createLocationService() {
        if (checkPermission()) { // если разрешение есть, запускаем сервис
            Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + " permission good, starting service ");
            intentLocationService = new Intent(this, LocationService.class);
            intentLocationService.setPackage("com.example.racertimer.Instruments");
            this.startService(intentLocationService);
        } // если разрешения нет, выводим тост
        else Toast.makeText(this, "No GPS permission", Toast.LENGTH_LONG);
    }

    /** Методы для работы с разрешениями на геолокацию */
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
    private void askPermission() { // запрос разрешения
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, // запрашиваем разрешение
                Manifest.permission.ACCESS_FINE_LOCATION}, 100); // ключ 100, такой же как ниже
    }

}