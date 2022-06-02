package com.example.racertimer.map;

import android.location.Location;
import android.util.Log;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class ScreenWindowShifter {
    private final static String PROJECT_LOG_TAG = "racer_timer_draw";

    private MapManager mapManager;
    private TrackGridCalculator trackGridCalculator;

    private double scale;
    private int layoutSizeX, layoutSizeY, windowSizeX, windowSizeY;

    private ScrollView verticalMapScroll;
    private HorizontalScrollView horizontalMapScroll;

    private int layoutShiftX, layoutShiftY;

    private Location lastLocation;

    public ScreenWindowShifter(MapManager mapManager, Location location, ConstraintLayout tracksLayout,
                               ScrollView verticalMapScroll, HorizontalScrollView horizontalMapScroll, double scale) {
        this.mapManager = mapManager;
        setLayout(tracksLayout, verticalMapScroll, horizontalMapScroll);
        trackGridCalculator = new TrackGridCalculator(location);
        lastLocation = location;

        this.scale = scale;
    }

    public void moveWindowCenterToPosition (Location location) {
        if (windowSizeX == 0) mapManager.setWindowSizesToShifter();

        lastLocation = location;
        calculateLayoutShifts(location);

        Log.i("bugfix", "layoutShiftX = "+ layoutShiftX );
        horizontalMapScroll.scrollTo(layoutShiftX, 0);
        verticalMapScroll.scrollTo(0, layoutShiftY);
    }

    private void calculateLayoutShifts(Location location) {
        int localX = trackGridCalculator.calculateLocalX(location);
        int localY = trackGridCalculator.calculateLocalY(location);

        layoutShiftX = (int) ((layoutSizeX / 2) + (localX * scale) - (windowSizeX / 2));
        layoutShiftY = (int) ((layoutSizeY / 2) + (localY * scale) - (windowSizeY / 2));
    }

    public void onScaleChanged (double currentScale) {
        scale = currentScale;
        moveWindowCenterToPosition(lastLocation);
    }

    public void setLayout(ConstraintLayout tracksLayout, ScrollView verticalScroll, HorizontalScrollView horizontalMapScroll) {
        layoutSizeX = tracksLayout.getWidth();
        layoutSizeY = tracksLayout.getHeight();

        this.verticalMapScroll = verticalScroll;
        this.horizontalMapScroll = horizontalMapScroll;
    }

    public void setWindowSizes(int windowSizeX, int windowSizeY) {
        this.windowSizeX = windowSizeX;
        this.windowSizeY = windowSizeY;
        Log.i(PROJECT_LOG_TAG, "setting sizes in shifter - LayoutX: " + layoutSizeX+", windowX:"+ windowSizeX );
    }
}

////////Log.i("bugfix", "calculateLayoutShifts(): layoutShiftX = "+ layoutShiftX );
