package com.example.racertimer.trackMap.movers;

import android.location.Location;
import android.widget.ImageView;

import com.example.racertimer.trackMap.MapManager;
import com.example.racertimer.trackMap.TrackGridCalculator;

public class ArrowMover {
    private ImageView arrowPosition;
    private MapManager mapManager;
    private TrackGridCalculator trackGridCalculator;

    private int arrowCenterX, arrowCenterY;

    public ArrowMover(MapManager mapManager, ImageView arrowPosition) {
        this.mapManager = mapManager;
        this.arrowPosition = arrowPosition;
        calculateArrowCenter();
    }

    private void calculateArrowCenter () {
        arrowCenterX = arrowPosition.getWidth() / 2;
        arrowCenterY = arrowPosition.getHeight() / 2;
    }

    public void moveArrowToPosition (Location location) {
        if (trackGridCalculator == null) {
            trackGridCalculator = mapManager.trackGridCalculator;
        }

        int currentCoordinateX = trackGridCalculator.calculateCoordXInView(location); // нынешние координаты в системе координат лайаута
        int currentCoordinateY = trackGridCalculator.calculateCoordYInView(location);

        if (arrowCenterX == 0) calculateArrowCenter();

        arrowPosition.setX(currentCoordinateX - arrowCenterX);
        arrowPosition.setY(currentCoordinateY - arrowCenterY);
    }
}