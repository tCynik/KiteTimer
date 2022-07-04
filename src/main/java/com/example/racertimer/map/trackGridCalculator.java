package com.example.racertimer.map;

import android.location.Location;
import android.util.Log;

import androidx.constraintlayout.widget.ConstraintLayout;

class TrackGridCalculator {
    private final String PROJECT_LOG_TAG = "racer_timer_grid";
    private MapManager mapManager;

    private double trackStartLongitude; // точка начала трека принимается как начало отсчета карты...
    private double trackStartLatitude;  //      т.е. Х=0 У=0 в локальной системе отсчета
    private final int trackAccuracy = 5; // точность прорисовки трека = 5й знак после запятой в координатах

    private ConstraintLayout tracksLayout;
    private int centerOfViewX, centerOfViewY;

    public TrackGridCalculator (MapManager mapManager, Location location){
        this.mapManager = mapManager;
        trackStartLatitude = location.getLatitude();
        trackStartLongitude = location.getLongitude();
    }

    public int calculateCoordXInView(Location location) {
        if (centerOfViewX == 0) calculateCenterOfView();

        int coordinateInView = centerOfViewX + calculateLocalX(location);
        // TODO: если будет ошибка с не нейденными параметрами, нужно запилить коллбек для получения лайаута

        return coordinateInView;
    }

    public int calculateCoordYInView(Location location) {
        int coordinateInView = centerOfViewY + calculateLocalY(location);
        // TODO: если будет ошибка с не нейденными параметрами, нужно запилить коллбек для получения лайаута
        return coordinateInView;
    }

    public int calculateLocalX(Location location) {
        double coordinateDifferent = location.getLongitude() - trackStartLongitude;
        int coordinatePixels = calculatePixelFromCoordinate(coordinateDifferent);
        Log.i(PROJECT_LOG_TAG, "calculating different coord. X = " + coordinateDifferent + ", pixels = " + coordinatePixels);
        return coordinatePixels;
    }

    public int calculateLocalY(Location location) {
        double coordinateDifferent = location.getLatitude() - trackStartLatitude;
        int coordinatePixels = calculatePixelFromCoordinate(coordinateDifferent) * -1; // -1 для инвертирования оси У
        Log.i(PROJECT_LOG_TAG, "calculating different coord. Y = " + coordinateDifferent + ", pixels = " + coordinatePixels);
        return coordinatePixels;
    }

    public void setTracksLayout (ConstraintLayout tracksLayout) {
        this.tracksLayout = tracksLayout;
        calculateCenterOfView();
    }

    private void calculateCenterOfView () {
        centerOfViewX = tracksLayout.getWidth() / 2;
        centerOfViewY = tracksLayout.getHeight() / 2;
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