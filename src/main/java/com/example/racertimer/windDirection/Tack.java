package com.example.racertimer.windDirection;

import android.location.Location;

import com.example.racertimer.Instruments.CoursesCalculator;

public abstract class Tack {
    private final int tackDirection;
    private final Location firstLocation;
    private Location endLocation;
    private Location bestVmgLocation;
    private int bestVmg;

    public Tack(int tackDirection, Location location) {
        this.tackDirection = tackDirection;
        firstLocation = null;
    }

    protected int getBearing () {
        int bearing = 1000;
        if (endLocation != null) firstLocation.bearingTo(endLocation);
        return bearing;
    }

    protected int getMaxVmg() {
        return bestVmg;
    }

    protected void changeSecondPoint(Location location) {
        endLocation = location;
    }

    protected boolean updateMaxVmg(Location location, int windDirection) {
        changeSecondPoint(location);
        int currentBearing = (int) location.getBearing();
        int currentVelocity = (int) location.getSpeed();
        int currentVmg = CoursesCalculator.VMGByWindBearingVelocity(windDirection, currentBearing, currentVelocity);
        if (currentVmg > bestVmg) {
            bestVmgLocation = location;
            return true;
        }
        else return false;
    }

    public boolean isInActiveZone(int deadRadius) {
        boolean zoneBegan = false;
        if (endLocation != null) {
            int currentRadius = (int) firstLocation.distanceTo(endLocation);
            if (currentRadius > deadRadius) zoneBegan = true;
        }
        return zoneBegan;
    }

    public static class UpwindRight extends Tack {
        public UpwindRight(int tackDirection, Location location) {
            super(tackDirection, location);
        }
    }

    public static class UpwindLeft extends Tack {
        public UpwindLeft(int tackDirection, Location location) {
            super(tackDirection, location);
        }
    }


}

