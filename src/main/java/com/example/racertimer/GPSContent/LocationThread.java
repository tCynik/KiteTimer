package com.example.racertimer.GPSContent;

///// отдельная нить для слушателя местоположения

import android.location.Location;
import android.location.LocationManager;

public class LocationThread extends Thread {
    private Location location = null; // актуальные (последние) данные геолокации
    private LocationManager locationManager; // поле класса LocationManager - для управления GPS
    private LocListener locListener; // объект класса Loclistener


    public Location getLocation() {
        return location;
    }

//    private void initLocationManager () {
//        locationManager = (LocationManager)getSystemService(this.LOCATION_SERVICE); // доступ к Location сервису
//        locListener = new LocListener(); // создаем новый обьект класса loclistener
//        locListener.setLocListenerInterface(this); // вызываем метод передачи данных через интерфейс
//        checkPermissionLoc(); // обращаемся за разрешением на использование GPS
//    }

    public void run(){
        LocationListenerClass listener = new LocationListenerClass();
        listener.initLocationManager();
        while (true) {
            location = listener.getLocation();
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
