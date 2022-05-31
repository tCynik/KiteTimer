package com.example.racertimer.map;

import android.location.Location;
import android.util.Log;

import androidx.constraintlayout.widget.ConstraintLayout;

public class ScreenWindowShifter {
    private final static String PROJECT_LOG_TAG = "racer_timer_draw";

    private MapManager mapManager;
    private TrackGridCalculator trackGridCalculator;
    private double startPositionX, startPositionY;

    private double scale;
    private ConstraintLayout tracksLayout;
    private int layoutSizeX, layoutSizeY, windowSizeX, windowSizeY;

    private float layoutShiftX, layoutShiftY;

    private Location lastLocation;

    public ScreenWindowShifter(MapManager mapManager, Location location, ConstraintLayout tracksLayout, double scale) {
        this.mapManager = mapManager;
        setLayout(tracksLayout);
        trackGridCalculator = new TrackGridCalculator(location);
        lastLocation = location;
        startPositionX = location.getLatitude();
        startPositionY = location.getLongitude();

        this.scale = scale;
    }

    public void moveWindowCenterToPosition (Location location) {
        if (windowSizeX == 0) mapManager.setWindowSizesToShifter();

        lastLocation = location;
        calculateLayoutShifts(location);
        //shiftLayoutByScale();
        tracksLayout.setX(layoutShiftX);
        tracksLayout.setY(layoutShiftY);
    }

    private void calculateLayoutShifts(Location location) {
        int localX = trackGridCalculator.calculateLocalX(location);
        int localY = trackGridCalculator.calculateLocalY(location);

        layoutShiftX = (float) (windowSizeX / 2 - (layoutSizeX / 2 + localX * scale));
        layoutShiftY = (float) (windowSizeY / 2 - (layoutSizeY / 2 + localY * scale));
    }

    private void shiftLayoutByScale () {

    }

    public void onScaleChanged (double currentScale) {
        scale = currentScale;
        moveWindowCenterToPosition(lastLocation);
    }

    public void setLayout(ConstraintLayout tracksLayout) {
        this.tracksLayout = tracksLayout;
        layoutSizeX = tracksLayout.getWidth();
        layoutSizeY = tracksLayout.getHeight();
    }

    public void setWindowSizes(int windowSizeX, int windowSizeY) {
        this.windowSizeX = windowSizeX;
        this.windowSizeY = windowSizeY;
        Log.i(PROJECT_LOG_TAG, "setting sizes in shifter - LayoutX: " + layoutSizeX+", windowX:"+ windowSizeX );
    }
}

////////Log.i("bugfix", "calculateLayoutShifts(): layoutShiftX = "+ layoutShiftX );
