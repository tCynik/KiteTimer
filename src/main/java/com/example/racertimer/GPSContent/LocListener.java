package com.example.racertimer.GPSContent;

import android.location.Location;
import android.location.LocationListener;
import android.util.Log;

import androidx.annotation.NonNull;

public class LocListener extends Thread { //LocationListener - библиотечный класс для работы с геопозиционииованием
    public LocationListener locationListener; // листенер для приема геоданных
    private LocListenerInterface locationListenerInterface;

    private int locationUpdatePeriodMs;


    public LocListener () {
        locationUpdatePeriodMs = 1000;
        Log.i("Main", " Thread: "+Thread.currentThread().getName() + " constructor is working...");

        /** создаем в потоке листенер геопозиции */
        locationListener = new LocationListener() { // потом на этот листенер вешаем передачу данных в менеджере
            @Override
            public void onLocationChanged(@NonNull Location location) {
                locationListenerInterface.whenLocationChanged(location);
                Log.i("Main", " Thread: "+Thread.currentThread().getName() + ". locationListener fixing new position... ");
            }
        };
    }

    public void run() {
        Log.i("Main", " Thread: "+Thread.currentThread().getName() + " Thread runned");
    }

    /** сеттер, через которого из мэйна привязывается экземпляр интерфейса для передачи данных*/
    public void setLocationListenerInterface(LocListenerInterface locationListenerInterface) {
        this.locationListenerInterface = locationListenerInterface;
    }


}
