package com.example.racertimer.tracks_map.movers;

import android.location.Location;
import android.util.Log;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.racertimer.tracks_map.MapManager;
import com.example.racertimer.tracks_map.TrackGridCalculator;
import com.example.racertimer.tracks_map.TracksWindowModel;

public class ScreenWindowShifter {
    private final static String PROJECT_LOG_TAG = "racer_timer_draw";

    private MapManager mapManager;
    private TrackGridCalculator trackGridCalculator;
    private TracksWindowModel tracksWindowModel;

    private double scale;
    private int layoutSizeX, layoutSizeY, windowSizeX, windowSizeY;

    private ScrollView verticalMapScroll;
    private HorizontalScrollView horizontalMapScroll;

    private int layoutShiftX, layoutShiftY;

    private Location lastLocation;

    public ScreenWindowShifter(MapManager mapManager,
                               TrackGridCalculator trackGridCalculator,
                               ConstraintLayout tracksLayout,
                               ScrollView verticalMapScroll,
                               HorizontalScrollView horizontalMapScroll,
                               double scale) {
        this.mapManager = mapManager;
        setLayout(tracksLayout, verticalMapScroll, horizontalMapScroll);
        this.trackGridCalculator = trackGridCalculator;

        this.scale = scale; // приходит из map manager
    }

    public void moveWindowCenterToPosition (Location location) {
        if (windowSizeX == 0) mapManager.setWindowSizesToShifter();

        lastLocation = location;
        calculateLayoutShifts(location);

        horizontalMapScroll.scrollTo(layoutShiftX, 0);
        verticalMapScroll.scrollTo(0, layoutShiftY);
    }

    private void calculateLayoutShifts(Location location) {
        if (trackGridCalculator == null) {
            trackGridCalculator = mapManager.trackGridCalculator;
        }
        int localX = mapManager.trackGridCalculator.calculateLocalX(location);
        int localY = mapManager.trackGridCalculator.calculateLocalY(location);

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