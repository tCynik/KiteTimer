package com.example.racertimer.windDirection;

import android.location.Location;

import com.example.racertimer.Instruments.CoursesCalculator;

public class CompareRoughCalculator extends WindByCompareCalculator{
    private Location lastLocation;
    private Location locationA, locationB; // расчеты ведутся для двух отрезков:
    // отрезок А-В, и отрезок В-"текущая точка"
    private int bearingSegmentAB, bearingSegmentBC = 1000;

    boolean segmentAB_IsFinished = false;

    public CompareRoughCalculator(CalculatedWindUpdater calculatedWindUpdater) {
        super(calculatedWindUpdater);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (locationA == null) locationA = location; // first got location
        else {
            if (segmentAB_IsFinished) {
                bearingSegmentBC = (int) locationB.bearingTo(location);
                int windDirection = CoursesCalculator.windBetweenTwoUpwinds(bearingSegmentAB, bearingSegmentBC);
                onWindCalculated(windDirection);
            }
        }
    }

    private void calculateWindDirection() {

    }

    public void onTackChanged(Location location) {
        if (! segmentAB_IsFinished) {
            segmentAB_IsFinished = true;
            locationB = location;
            bearingSegmentAB = (int) locationA.bearingTo(locationB);
        }
    }

}
