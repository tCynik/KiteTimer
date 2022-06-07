package com.example.racertimer.map;

import android.location.Location;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class ArrowMover {
    private ImageView arrowPosition;
    private MapManager mapManager;
    private TrackGridCalculator trackGridCalculator;

    private int arrowCenterX, arrowCenterY;

    /**
     * ниже идут параметры (лайауты) В будущем имеет смысл сделать единый калькулятор, который будет
     * высчитывать все нужные координаты для всех классов, чтобы не делать это индивидуально в каждом классе
     */
    private int windowSizeX, windowSizeY, layoutSizeX, layoutSizeY;

    public ArrowMover(MapManager mapManager, ImageView arrowPosition, TrackGridCalculator trackGridCalculator) {
        this.mapManager = mapManager;
        this.arrowPosition = arrowPosition;
        this.trackGridCalculator = trackGridCalculator;
        calculateArrowCenter();
    }

    private void calculateArrowCenter () {
        arrowCenterX = arrowPosition.getWidth() / 2;
        arrowCenterY = arrowPosition.getHeight() / 2;
    }

    public void moveArrowToPosition (Location location) {
        int currentCoordinateX = trackGridCalculator.calculateCoordXInView(location); // нынешние координаты в системе координат лайаута
        int currentCoordinateY = trackGridCalculator.calculateCoordYInView(location);

        if (arrowCenterX == 0) calculateArrowCenter();

        arrowPosition.setX(currentCoordinateX - arrowCenterX);
        arrowPosition.setY(currentCoordinateY - arrowCenterY);
    }

    public void setLayoutSizes (ConstraintLayout tracksLayout, ScrollView verticalScroll, HorizontalScrollView horizontalMapScroll) {
        windowSizeX = horizontalMapScroll.getWidth();
        windowSizeY = verticalScroll.getHeight();
        layoutSizeX = tracksLayout.getWidth();
        layoutSizeY = tracksLayout.getHeight();
    }

}

// TODO: make the instance in mapManager, make moveArrowPosition callback from manager
