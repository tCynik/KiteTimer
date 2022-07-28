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

import com.example.racertimer.windDirection.WindCalcByCompareManager;
import com.example.racertimer.windDirection.WindByStatistics;
import com.example.racertimer.windDirection.WindChangedHerald;

import java.util.ArrayList;

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
    private int selectedWindCalculationWay = 2;

    private boolean isAppResumed = true;
    private ArrayList<Location> tempLocationsData;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private int windDirection = 10000;

    WindChangedHerald windChangedHerald; // интерфейс для передачи данных в класс хранения и расчета статистических данных

    private Intent intent; // интент для отправки сообщений из данного сервиса

    private WindByStatistics windByStatistics;
    private WindCalcByCompareManager windCalcByCompareManager;
    public MyBinder binder = new MyBinder();

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("racer_timer", "service: i was binded to something... " );
        return binder; // этот биндер типа MyBinder
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + " location service is created");
        makeWindChangeHerald(); // экземпляр интерфейса для формировани бродкаста с новым ветром
        selectWindCalculator(selectedWindCalculationWay);

        createLocationListener();
        requestLocationUpdates();
    }

    private void makeWindChangeHerald() {
        windChangedHerald = new WindChangedHerald() {
            @Override
            public void onWindDirectionChanged(int windDirection) { // если обновляется инфа по направлению ветра
                intent = new Intent(BROADCAST_ACTION); // готовим передачу с новыми данными
                intent.putExtra("windDirection", windDirection);
                sendBroadcast(intent); // отправляем передачу
                Log.i(PROJECT_LOG_TAG, " sending new wind direction into broadcastListener by herald. the win = " + windDirection);
            }
        };
    }

    public void selectWindCalculator(int selectedWindCalculationWay) {
        switch (selectedWindCalculationWay) {
            case NO_WIND_CALCULATION:
                break;
            case CALCULATE_BY_DIAGRAM:
                windByStatistics = new WindByStatistics(5, windChangedHerald);
                break;
            case CALCULATE_BY_VMG_COMPARE:
                windCalcByCompareManager = new WindCalcByCompareManager(windChangedHerald, windDirection);
                windCalcByCompareManager.setWindDirection(202);
                break;
        }
    }

    private void createLocationListener() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + " service is get new location ");
                if (isAppResumed) {
                    if (location != null) { // когда поступила ненулевая геолокация, отправляем сообщение
                        intent = new Intent(BROADCAST_ACTION);
                        intent.putExtra("location", location);
                        sendBroadcast(intent);
                        calculateAndSendWindDirection(location);
                    }
                } else { // app is paused
                    addLocationToTempData(location);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { // на случай ошибки
            }
        };
    }

    private void calculateAndSendWindDirection (Location location) {
        // TODO: нужно допилить расчет сравнением и реализовать управление обработкой
        //  геоданных и получения результата в зависимости от выбранного типа расчета
        switch (selectedWindCalculationWay) {
            case NO_WIND_CALCULATION:
                break;
            case CALCULATE_BY_DIAGRAM:
                windByStatistics.onLocationChanged(location);
                break;
            case CALCULATE_BY_VMG_COMPARE:
                windCalcByCompareManager.onLocationChanged(location);
                break;
        }
    }

    private void requestLocationUpdates() {
        /** создаем менеджер, проверяем разрешение, и запускаем прием геолокации */
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkLocationPermission()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
            Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + " request location updating ");
        }
    }
    public boolean checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&  // если версия СДК выше версии M (API 23)
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) // если разрешения нет, то запускаем запрос разрешения, код ответа 100
        {
            return false; // если разрешения нет, возвращаем false
        } else
            return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + " location service is started");
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + " the service was destroyed ");
        //Log.i("bugfix", " the service was destroyed ");
        super.onDestroy();
    }

    public void appWasResumedOrStopped(boolean isAppResumed) {
        this.isAppResumed = isAppResumed;
        if (isAppResumed) {
            Log.i(PROJECT_LOG_TAG, " app is resumed ");

            sendLocationData();
            updateWindDirection();
            tempLocationsData = null;
        } else {
            tempLocationsData = new ArrayList<>();
            Log.i(PROJECT_LOG_TAG, " app is paused ");
        }
    }

    private void addLocationToTempData(Location location) {
        tempLocationsData.add(location);
    }

    public int getWindDirection () { // если напр ветра 10000 = значит, пока результатов нет
        return windByStatistics.getWindDirection();
    }

    public void setWindDirection () {
        // TODO: реализовать передачу сюда напрвления ветра, загруженного из памяти,
        //  взятого из интернета, либо установленного вручную.
    }

    public void updateWindDirection () {
        Log.i("racer_timer", "manually sending actual wind direction " );
        windChangedHerald.onWindDirectionChanged(windByStatistics.getWindDirection());
    }

    private void sendLocationData() {
        if (tempLocationsData != null & tempLocationsData.size() != 0) {
            intent = new Intent(BROADCAST_ACTION);
            intent.putExtra("locationsData", tempLocationsData);
            //Log.i("bugfix", " sending the location data. tempDataSize = " + tempLocationsData.size());

            sendBroadcast(intent);
        }
    }

    // организуем получение экземпляра данного сервиса через биндер
    public class MyBinder extends Binder { // содаем кастомный байндер - наследник байндера
        public LocationService getService() { // добавляем метод, возвращающий обьект сервиса
            Log.i("racer_timer", "activity getting service instance from MyBinder " );
            return LocationService.this;
        }
    }
}

