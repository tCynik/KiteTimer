package com.example.racertimer.map;

import android.location.Location;

public class TrackPainterOnMap {
    private final static String PROJECT_LOG_TAG = "racer_timer_painter";

    private int trackAccuracy = 5; // точность прорисовки трека = 5й знак после запятой в координатах

    private double startPointLongitude; // точка начала трека принимается как начало отсчета карты...
    private double startPointLatitude;  // т.е. Х=0 У=0 в локальной системе отсчета

    public void beginNewTrack (Location location) {
        startPointLatitude = location.getLatitude();
        startPointLongitude = location.getLongitude();
    }

    public void addLocationIntoTrack (Location location) {
        // TODO: обработка появления новой локации для отрисовки трека
    }

    private int calculateLocalX (Location location) {
        double coordinateDifferent = location.getLongitude() - startPointLongitude;
        return calculatePixelFromCoordinate(coordinateDifferent);
    }

    private int calculateLocalY (Location location) {
        double coordinateDifferent = location.getLatitude() - startPointLatitude;
        return (calculatePixelFromCoordinate(coordinateDifferent))*-1; // -1 для инвертирования оси У
    }

    private int calculatePixelFromCoordinate (double coordinate) {
        return (int) coordinate * 10 * trackAccuracy;
    }


}
