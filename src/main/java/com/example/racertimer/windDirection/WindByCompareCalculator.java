package com.example.racertimer.windDirection;

import android.location.Location;

public abstract class WindByCompareCalculator {
    private CalculatedWindUpdater calculatedWindUpdater;

    public WindByCompareCalculator(CalculatedWindUpdater calculatedWindUpdater) {
        this.calculatedWindUpdater = calculatedWindUpdater;
    }

    public void onTackChanged(int numberOfTack) {}

    public void onLocationChanged(Location location) {};

    private void calculateWindDirection() {}

    protected void onWindCalculated(int windDirection) {
        calculatedWindUpdater.windIsCalculated(windDirection);
    }
}
