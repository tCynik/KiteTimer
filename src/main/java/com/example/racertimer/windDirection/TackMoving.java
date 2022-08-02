package com.example.racertimer.windDirection;

import android.location.Location;

import com.example.racertimer.Instruments.CoursesCalculator;

public abstract class TackMoving {
    private final Location firstLocation;
    private Location endLocation;
    private Location bestVmgLocation;
    private int bestVmg = 0;

    public TackMoving(Location location) {
        firstLocation = null;
    }

    protected int getBearing () {
        int bearing = 1000;
        if (endLocation != null) firstLocation.bearingTo(endLocation);
        return bearing;
    }

    protected void changeSecondPoint(Location location) {
        endLocation = location;
    }

    protected boolean checkAndUpdateMaxVmg(Location location, int windDirection) {
        endLocation = location;
        int currentBearing = (int) location.getBearing();
        int currentVelocity = (int) location.getSpeed();
        int currentVmg = CoursesCalculator.VMGByWindBearingVelocity(windDirection, currentBearing, currentVelocity);
        if (currentVmg > bestVmg) {
            bestVmg = currentVmg;
            return true;
        }
        else return false;
    }

    public boolean isInActiveZone(Location location, int deadRadius) {
        boolean deadZoneEnded = false;
        int currentRadius = (int) firstLocation.distanceTo(location);
        if (currentRadius > deadRadius) deadZoneEnded = true;

        return deadZoneEnded;
    }

    public static class UpwindRight extends TackMoving {
        public UpwindRight(Location location) {
            super(location);
        }
    }

    public static class UpwindLeft extends TackMoving {
        public UpwindLeft(Location location) {
            super(location);
        }
    }
}

