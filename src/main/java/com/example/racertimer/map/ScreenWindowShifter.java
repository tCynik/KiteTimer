package com.example.racertimer.map;

import android.location.Location;
import android.util.Log;

import androidx.constraintlayout.widget.ConstraintLayout;

public class ScreenWindowShifter {
    private final static String PROJECT_LOG_TAG = "racer_timer_draw";

    private TrackPainterOnMap trackPainterOnMap;
    private TrackGridCalculator trackGridCalculator;
    private double startPositionX, startPositionY;

    private double scale;
    private ConstraintLayout tracksLayout;
    private int layoutSizeX, layoutSizeY, windowSizeX, windowSizeY;

    private float layoutShiftX, layoutShiftY;

    private Location lastLocation;

    public ScreenWindowShifter(TrackPainterOnMap trackPainterOnMap, Location location, ConstraintLayout tracksLayout, double scale) {
        trackGridCalculator = new TrackGridCalculator(location);
        lastLocation = location;
        startPositionX = location.getLatitude();
        startPositionY = location.getLongitude();

        this.scale = scale;
    }

    public void moveWindowCenterToPosition (Location location) {
        if (windowSizeX == 0) trackPainterOnMap.setWindowSizesToShifter();
        lastLocation = location;
        calculateLayoutShifts(location);
        tracksLayout.setX(layoutShiftX);
        tracksLayout.setY(layoutShiftY);
    }

    private void calculateLayoutShifts(Location location) {
        int localX = trackGridCalculator.calculateLocalX(location);
        int localY = trackGridCalculator.calculateLocalY(location);

        layoutShiftX = windowSizeX / 2 - (layoutSizeX / 2 + localX);
        layoutShiftY = windowSizeY / 2 - (layoutSizeY / 2 + localY);
    }

    public void onScaleChanged (double currentScale) {}

    public void setLayout(ConstraintLayout tracksLayout) {
        this.tracksLayout = tracksLayout;
        layoutSizeX = tracksLayout.getWidth();
        layoutSizeY = tracksLayout.getHeight();
        moveWindowCenterToPosition(lastLocation);
    }

    public void setSizes (int windowSizeX, int windowSizeY) {
        this.windowSizeX = windowSizeX;
        this.windowSizeY = windowSizeY;
        Log.i(PROJECT_LOG_TAG, "setting sizes in shifter - LayoutX: " + layoutSizeX+", windowX:"+ windowSizeX );
    }
}

