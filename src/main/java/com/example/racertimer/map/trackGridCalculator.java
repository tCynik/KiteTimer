package com.example.racertimer.map;

import android.location.Location;
import android.util.Log;

class TrackGridCalculator {
    private final String PROJECT_LOG_TAG = "racer_timer_grid";
    private double trackStartLongitude; // точка начала трека принимается как начало отсчета карты...
    private double trackStartLatitude;  //      т.е. Х=0 У=0 в локальной системе отсчета
    private final int trackAccuracy = 5; // точность прорисовки трека = 5й знак после запятой в координатах


    public TrackGridCalculator (Location location) {
        trackStartLatitude = location.getLatitude();
        trackStartLongitude = location.getLongitude();
    }

    public int calculateLocalX(Location location) {
        double coordinateDifferent = location.getLongitude() - trackStartLongitude;
        int coordinatePixels = calculatePixelFromCoordinate(coordinateDifferent);
        Log.i(PROJECT_LOG_TAG, "calculating different coord. X = " + coordinateDifferent + ", pixels = " + coordinatePixels);
        return coordinatePixels; // TODO: вот эти координаты почему-то получаются нулевые, хотя в логах все ок
    }

    public int calculateLocalY(Location location) {
        double coordinateDifferent = location.getLatitude() - trackStartLatitude;
        int coordinatePixels = calculatePixelFromCoordinate(coordinateDifferent) * -1; // -1 для инвертирования оси У
        Log.i(PROJECT_LOG_TAG, "calculating different coord. Y = " + coordinateDifferent + ", pixels = " + coordinatePixels);
        return coordinatePixels;
    }

    private int calculatePixelFromCoordinate(double coordinateDifferent) {
        int scaledCoordinates = 0;
        if (Math.abs((int) coordinateDifferent) < 1) { // костыль на случай косяков с GPS и внезапных перескоков на большие расстояния
            int scaleMultiplier = 10;
            for (int i = 1; i < trackAccuracy; i++) { // 5 - trackAccuracy
                scaleMultiplier = scaleMultiplier * 10;
            }
            scaledCoordinates = (int) (coordinateDifferent * scaleMultiplier);
            Log.i(PROJECT_LOG_TAG, "scale = " + scaleMultiplier + ", different = " + coordinateDifferent + ", " +
                    "scaled coordinate = " + scaledCoordinates);
        }
        return scaledCoordinates;

        //TODO: протестировать корректность подсчета
    }
}