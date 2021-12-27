package com.example.racertimer.GPSContent;

import android.location.Location;

// интерфейс для передачи новой информации при обновлении геоданных
public interface LocListenerInterface {
    public void whenLocationChanged(Location location); // у интерфейса есть переменная, в которой хранится позиция
}
