package com.example.racertimer.GPSContent;

import android.location.Location;
import android.location.LocationListener;

import androidx.annotation.NonNull;

public class LocListener implements LocationListener { //LocationListener - библиотечный класс для работы с геопозиционииованием
    private LocListenerInterface locListenerInterface; // переменная с информацией о позиции (см. интерфейс)

    @Override
    public void onLocationChanged(@NonNull Location location) { // каждый раз при изменении позиции
        ///// через интерфейс будет запускаться метод whenLocationChanged, переопределенный в классе MainLocal
        locListenerInterface.whenLocationChanged(location); // при каждом изменении позиции мы запускаем
        // для обьекта класса интерфейс кастомный метод whenLocationChanged - это действует на все классы, поддерживающие интрф?
    }

    public void setLocListenerInterface(LocListenerInterface locListenerInterface) { // сеттер - чтобы из другого класса,
        // поддерживающего данный интерфейс, можно было присвоить текущему полю locListenerInterface значение,
        // передаваемое при запуске метода - сеттера.
        this.locListenerInterface = locListenerInterface;
    }


}
