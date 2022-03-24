package com.example.racertimer.Instruments;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

/**
 * сервис, осуществляющий получение обновления данных геолокации
 * новые данные рассылаются через широковещательные Broadcast сообщения
 */

public class LocationService extends Service {
    private final static String PROJECT_LOG_TAG = "racer_timer";
    final String BROADCAST_ACTION = "com.example.racertimer.action.new_location"; // значение для фильтра приемника

    // блок опций с методами определения направления ветра
    final static int NO_WIND_CALCULATION = 0; // без подсчета
    final static int CALCULATE_BY_DIAGRAM = 1; // по диаграмме скоростей
    final static int CALCULATE_BY_VMG_COMPARE = 2; // по сравнению ВМГ - требует первоначального направления

    private LocationManager locationManager;
    private LocationListener locationListener;

    WindChangedHerald windChangedHerald; // интерфейс для передачи данных в класс хранения и расчета статистических данных

    private Intent intent; // интент для отправки сообщений из данного сервиса

    private WindByStatistics windByStatistics;
    private WindByCompare windByCompare;
    public MyBinder binder = new MyBinder();

    public LocationService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.i("racer_timer", "service: i was binded to something... " );
        return binder; // этот биндер типа MyBinder
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + " location service is started");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + " location service is started");
        /** создаем листенер и описываем его действия */

        // создаем экземпляр интерфейса для генерации передачи из сервиса в обьект расчета статистики WindStatistics
        windChangedHerald = new WindChangedHerald() {
            @Override
            public void onWindDirectionChanged(int windDirection) { // если обновляется инфа по направлению ветра
                intent = new Intent(BROADCAST_ACTION); // готовим передачу с новыми данными
                intent.putExtra("windDirection", windDirection);
                sendBroadcast(intent); // отправляем передачу
                Log.i(PROJECT_LOG_TAG, " sending new wind direction into broadcastListener by herald. the win = " + windDirection);
            }
        };

        // создаем экземпляры классов для расчета направлений ветра
        windByStatistics = new WindByStatistics(5, windChangedHerald);
        // TODO: создать механизм первоначальноц настройки направления ветра
        windByCompare = new WindByCompare(202, windChangedHerald);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + " service is get new location ");
                if (location != null) { // когда поступила ненулевая геолокация, отправляем сообщение
                    intent = new Intent(BROADCAST_ACTION);
                    intent.putExtra("location", location);
                    sendBroadcast(intent);

                    // передаем новые геоданные в расчетчик направления ветра
                    // TODO: реализовать управление обработкой геоданных и получения результата в зависимости от выбранного типа расчета
                    //windByStatistics.onLocationChanged(location);
                    windByCompare.onLocationChanged(location);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { // на случай ошибки
            }
        };

        /** создаем менеджер, проверяем разрешение, и запускаем прием геолокации */
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkPermission()) {// если есть все разрешения, запускаем прием геолокации с передачей в поток листенер
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
            Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + " request location updating ");
        }
    }

    public int getWindDirection () { // если напр ветра 10000 = значит, пока результатов нет
        return windByStatistics.getWindDirection();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + " the service eas destroyed ");
    }

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

    public void updateWindDirection () {
        Log.i("racer_timer", "manually sending actual wind direction " );
        // TODO: вынеси в отдельный метод отправку интента с ветром, тут и в onWindChanged вызов этого метода
        // нужно допиливать фрагмент прибора
        windChangedHerald.onWindDirectionChanged(windByStatistics.getWindDirection());
    }

    // организуем получение экземпляра данного сервиса через биндер
    public class MyBinder extends Binder { // содаем кастомный байндер - наследник байндера
        public LocationService getService() { // добавляем метод, возвращающий обьект сервиса
            Log.i("racer_timer", "activity getting service instance from MyBinder " );
            return LocationService.this;
        }
    }
}

