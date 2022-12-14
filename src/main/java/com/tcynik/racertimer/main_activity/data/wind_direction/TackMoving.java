package com.tcynik.racertimer.main_activity.data.wind_direction;

import android.location.Location;
import android.util.Log;

import com.tcynik.racertimer.main_activity.domain.CoursesCalculator;
import com.tcynik.racertimer.main_activity.domain.CoursesCalculator;

public abstract class TackMoving {
    private final static String PROJECT_LOG_TAG = "racer_timer_windCompare";
    private final Location firstLocation;
    private String tackName;
    private Location secondLocation = null;
    private int bestVmg = 0;
    private int bearingWithBestVmg = 10000;

    public TackMoving(Location location, String tackName) {
        firstLocation = location;
        this.tackName = tackName;
    }

    protected int getBestBearing() {
        return bearingWithBestVmg;

//        int bearing = 10000;
//        if (bearingWithBestVmg != 10000) bearing = bearingWithBestVmg;
//        else if (secondLocation != null) firstLocation.bearingTo(secondLocation);
//        return bearing;
    }

    protected void initSecondPoint(Location location) {
        secondLocation = location;
        bearingWithBestVmg = (int) location.getBearing();
        Log.i(PROJECT_LOG_TAG, tackName+" has it secondPoint first time. Now best bearing = " +bearingWithBestVmg);
    }

    protected boolean checkAndUpdateMaxVmg(Location location, int windDirection) {
        int currentBearing = (int) location.getBearing();
        int currentVelocity = (int) location.getSpeed();
        int currentVmg = CoursesCalculator.VMGByWindBearingVelocity(windDirection, currentBearing, currentVelocity);
        if (currentVmg > bestVmg) {
            bestVmg = currentVmg;
            Log.i(PROJECT_LOG_TAG, " current "+tackName+" got new best VMG = " +bestVmg);
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

    public boolean isSecondPointExist() {
        return secondLocation != null;
    }

    public static class UpwindRight extends TackMoving {
        public UpwindRight(Location location) {
            super(location, "rightTack");
            Log.i(PROJECT_LOG_TAG, " new upwind right tack instance was created ");
        }
    }

    public static class UpwindLeft extends TackMoving {
        public UpwindLeft(Location location) {
            super(location, "leftTack");
            Log.i(PROJECT_LOG_TAG, " new upwind left tack instance was created ");
        }
    }
}

