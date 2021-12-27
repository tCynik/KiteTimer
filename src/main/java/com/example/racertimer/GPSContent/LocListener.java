package com.example.racertimer.GPSContent;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class LocListener implements LocationListener { //LocationListener - библиотечный класс для работы с геопозиционииованием

    private LocationManager locationManager;
    private LocationListener locationListener;

    private LocListenerInterface locListenerInterface; // переменная с информацией о позиции (см. интерфейс)

    @Override
    public void onLocationChanged(@NonNull Location location) {
/*
        // каждый раз при изменении позиции
        ///// через интерфейс будет запускаться метод whenLocationChanged, переопределенный в классе MainLocal
        // для обьекта класса интерфейс кастомный метод whenLocationChanged - это действует на все классы, поддерживающие интрф?
 */
        locListenerInterface.whenLocationChanged(location); // при каждом изменении позиции мы запускаем
    }

    public void setLocListenerInterface(LocListenerInterface locListenerInterface) { // сеттер - чтобы из другого класса,
        // поддерживающего данный интерфейс, можно было присвоить текущему полю locListenerInterface значение,
        // передаваемое при запуске метода - сеттера.
        this.locListenerInterface = locListenerInterface;
    }

    public void runLocationListener(Context context, LocationManager locationManager) { // начало приема GPS после контролькой проверки наличия разрешения
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.locationManager = locationManager;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                2,
                5,
                locationListener);

    }


}
