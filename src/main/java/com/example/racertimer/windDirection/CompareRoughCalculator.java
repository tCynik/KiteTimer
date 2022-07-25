package com.example.racertimer.windDirection;

import android.location.Location;

public class CompareRoughCalculator {
    private Location lastLocation;
    private Location locationA, locationB; // расчеты ведутся для двух отрезков:
    // отрезок А-В, и отрезок В-"текущая точка"
    private int bearingFirstSegment, bearingSecondSegment = 1000;

    boolean firstSegmentFinished = false;

    public void onLocationChanged(Location location) {
        if (locationA == null) locationA = location;
        else
            if (locationB == null & firstSegmentFinished == true) {
                locationB = location;
                bearingFirstSegment = (int) locationA.bearingTo(locationB);
            }
            else {
                bearingSecondSegment = (int) locationB.bearingTo(location);
            }
        if (bearingSecondSegment != 1000) calculateWindDirection();
    }

    private void calculateWindDirection() {

    }

    public void onTackChanged(Location location) {
        firstSegmentFinished = true;
    }

}
