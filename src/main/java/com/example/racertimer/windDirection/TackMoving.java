package com.example.racertimer.windDirection;

import android.location.Location;
import android.util.Log;

import com.example.racertimer.Instruments.CoursesCalculator;

public abstract class TackMoving {
    private final static String PROJECT_LOG_TAG = "racer_timer_windCompare";
    private final Location firstLocation;
    private Location endLocation;
    private Location bestVmgLocation;
    private int bestVmg = 0;
    private int bearingWithBestVmg = 10000;

    public TackMoving(Location location) {
        firstLocation = location;
    }

    protected int getBearing () {
        int bearing = 10000;
        if (bearingWithBestVmg != 10000) bearing = bearingWithBestVmg;
        else if (endLocation != null) firstLocation.bearingTo(endLocation);
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
            Log.i(PROJECT_LOG_TAG, " current tack got new best VMG = " +bestVmg);
            bearingWithBestVmg = currentBearing;
            return true;
        }
        else return false;
    }

    public boolean isInActiveZone(Location location, int deadRadius) {
        boolean deadZoneEnded = false;
        int currentRadius = (int) firstLocation.distanceTo(location);
        if (currentRadius > deadRadius) {
            deadZoneEnded = true;
            Log.i(PROJECT_LOG_TAG, " tack dead zone = "+deadRadius+"m is ended! " );
        }

        return deadZoneEnded;
    }

    public static class UpwindRight extends TackMoving {
        public UpwindRight(Location location) {
            super(location);
            Log.i(PROJECT_LOG_TAG, " new upwind right tack instance was created ");
        }
    }

    public static class UpwindLeft extends TackMoving {
        public UpwindLeft(Location location) {
            super(location);
            Log.i(PROJECT_LOG_TAG, " new upwind right tack instance was created ");
        }
    }
}

