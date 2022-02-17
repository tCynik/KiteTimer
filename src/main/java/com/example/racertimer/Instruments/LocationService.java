package com.example.racertimer.Instruments;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

    private LocationManager locationManager;
    private LocationListener locationListener;
    private int lastWindDirection;

    WindChangedHerald windChangedHerald;

    private Intent intent; // интент для отправки сообщений из данного сервиса

    private WindStatistics windStatistics;
    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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

        // создаем экземпляр интерфейса для генерации передачи
        windChangedHerald = new WindChangedHerald() {
            @Override
            public void onWindDirectionChanged(int windDirection) { // если обновляется инфа по направлению ветра
                intent = new Intent(BROADCAST_ACTION); // готовим передачу с новыми данными
                intent.putExtra("windDirection", windDirection);
                sendBroadcast(intent); // отправляем передачу
            }
        };
        // создаем экземпляр класса для расчета направлений ветра
        windStatistics = new WindStatistics(5, windChangedHerald);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + " service is get new location ");
                if (location != null) { // когда поступила ненулевая геолокация, отправляем сообщение
                    intent = new Intent(BROADCAST_ACTION);
                    intent.putExtra("location", location);
                    sendBroadcast(intent);

                    // передаем новые геоданные в расчетчик направления ветра
                    windStatistics.onLocationChanged(location);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { // на случай ошибки
            }
        };

        /** создаем менеджер, проверяем разрешение, и запускаем сервис приема геолокации */
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkPermission()) {// если есть все разрешения, запускаем прием геолокации с передачей в поток листенер
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
            Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + " request location updating ");
        }
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
}

class WindStatistics { // класс для сбора статистики скоростей и рассчета истинного напр ветра
    private final static String PROJECT_LOG_TAG = "racer_timer";

    private int sizeOfSectors; // размер каждого сектора диаграммы в градусах
    private int numberOfSectors; // количество секторов
    private int[] windDiagram; // массив, в котором храним диаграмму
    private int sensitivity; // чувствительность диаграммы к изменениям в %
    private int windDirection, lastWindDirection;
    private float[] sin, cos;

    WindChangedHerald windChangedHerald; // интерфейс для вывода нового значения ветра

    WindStatistics (int sizeOfSectors, WindChangedHerald windChangedHerald) {
        this.sizeOfSectors = sizeOfSectors;
        numberOfSectors = 360 / sizeOfSectors;
        windDiagram = new int[numberOfSectors];
        // заполняем таблицу синусов-косинусов для этой длины массива - в цикле
        sin = new float[numberOfSectors];
        cos = new float[numberOfSectors];
        for (int i = 0; i < numberOfSectors; i++) {
            double currentAngle = (i + 0.5) * sizeOfSectors; // берем середину сектора
            sin[i] = (float) Math.sin(Math.toRadians( currentAngle ));
            cos[i] = (float) Math.cos(Math.toRadians( currentAngle ));
//            Log.i(PROJECT_LOG_TAG, " sin = " + sin[i] + ", cos = " + cos[i]); // проверено
        }
        this.windChangedHerald = windChangedHerald;
        sensitivity = 50; // параметр чувствистельности для настройки
    }

    public void setSensitivity (int updSensitivity) { // сеттер для возможности настройки на ходу
        this.sensitivity = updSensitivity;
    }

    private int calculateNumberOfSector (int bearing) { // метод определения номера сектора
        return (int) bearing / sizeOfSectors;
    }

    public void onLocationChanged (Location location) { // главный метод. в этом методе заполняем диаграмму
        int bearing = (int) location.getBearing();
        int velocity = (int) location.getSpeed();
        int numTheSector = calculateNumberOfSector(bearing); // высчитываем номер сектора
        Log.i(PROJECT_LOG_TAG, " bearing =  " + bearing);

        if (windDiagram[numTheSector] < velocity) {  // определяем, является ли текущая скорость максимальной для сектора
            cutSymmetricalMaximum(bearing, (velocity - windDiagram[numTheSector]) ); // корректировка симметрии
            windDiagram[numTheSector] = velocity; // обновляем максимум

            windDirection = calculateWindDirection(); // определяем направление ветра
        }

        if (lastWindDirection != windDirection) { // если есть обновление направления ветра
            windChangedHerald.onWindDirectionChanged(windDirection);// отправляем broadcast с новым направлением
            lastWindDirection = windDirection; // обновляем данные по направлению
        }
    }

    void cutSymmetricalMaximum(int bearing, int velocityDifferent) { // симметрично прибавлению отнимаем
        int deltaBearing; // разница в градусах
        int symmetryDirection; // направление, симметричное исходному относительно ветра
        int sectorSymmetry; // искомый номер сектора, в котором находится symmetryDirection

        velocityDifferent = (int) velocityDifferent * sensitivity/100; // считаем разницу с учетом чувствительности
        // определяем номер симметричного ОТНОСИТЕЛЬНО ВЕТРА сектора для изменения
        // сначала определяем минимальную разницу между скоростью и ветром
        deltaBearing = windDirection - bearing; // разница между направлением ветра и скорости
        symmetryDirection = windDirection + deltaBearing; // если нет перехода через 360-0


        if (Math.abs (deltaBearing) > 180) { // обрабатываем переход расчетов через 360-0 если он есть
            // если переход есть, то верная разница между курсами = сколько осталось добрать до 360 + курс
            if (windDirection > 180) { // если ветер справа от 360-0, а курс слева
                deltaBearing = 360 - windDirection + bearing;
                symmetryDirection = windDirection - deltaBearing;
            }
            else { // если ветер слева от 360-0, а курс справа
                deltaBearing = windDirection + bearing;
                symmetryDirection = windDirection + deltaBearing;
            }
        }

        // приводим направление к диапазону от 0 до 360 для подсчета сектора в пределах массива
        if (symmetryDirection < 0) symmetryDirection += 360;
        if (symmetryDirection > 360) symmetryDirection -= 360;

        Log.i(PROJECT_LOG_TAG, " symmetry direction =  " + symmetryDirection);
        sectorSymmetry = calculateNumberOfSector(symmetryDirection); // высчитываем симметричный сектор

        // уменьшаем в этом секторе показания если они не нулевые
        if (windDiagram[sectorSymmetry] > 0) {
            windDiagram[sectorSymmetry] -= velocityDifferent;
        }
    }

    int calculateWindDirection() { // в этом методе высчитываем направление ветра
        int windDirection = this.windDirection;
        int vectorLength;
        int summVelocity = 0; // сумма длин всех векторов
        int summX = 0; // координата X суммирующего вектора
        int summY = 0; // координата Y суммирующего вектора

        for (int i = 0; i < numberOfSectors; i++) { // перебираем все сектора
            // находим координаты конца суммирующего вектора
            summX += (int) sin[i] * windDiagram[i]; // координата вектора X
            summY += (int) cos[i] * windDiagram[i]; // Координата вектора Y
            // находим сумму длин всех векторов для определеиня достаточности количества данных
            summVelocity += windDiagram[i];
        }

        // находим длину суммирующего вектора
        vectorLength = (int) Math.pow ((Math.pow(summX, 2) + Math.pow(summY, 2)), 0.5);

        // проверяем репрезентативность выборки (достаточное количество данных по секторам)
//        if (summVelocity / vectorLength > 4) {// если данных хватает, находим угол суммирующего вектора
        if (vectorLength != 0) windDirection = (int) Math.toDegrees( Math.acos(summY/vectorLength) );
        Log.i(PROJECT_LOG_TAG, " founded wind direction =  " + windDirection + ", last windDir = " + this.windDirection);

//        }

        return windDirection;
    }
}

interface WindChangedHerald { // глашатай изменения ветра для публикации новых данных
    void onWindDirectionChanged(int windDirection);
}