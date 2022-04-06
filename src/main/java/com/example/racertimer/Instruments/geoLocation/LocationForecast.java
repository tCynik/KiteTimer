package com.example.racertimer.Instruments.geoLocation;

/** Класс геоточки для хранения сохраненных локаций прогноза */

public class LocationForecast {
    private String name;
    private double latitude, longitude;

    public LocationForecast(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
