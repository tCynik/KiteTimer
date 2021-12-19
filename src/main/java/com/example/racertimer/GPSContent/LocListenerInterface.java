package com.example.racertimer.GPSContent;

import android.location.Location;

// интерфейс для передачи методов, связанных с работой GPS
public interface LocListenerInterface {
    public void whenLocationChanged(Location location); // у интерфейса есть переменная, в которой хранится позиция
}
