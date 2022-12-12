package com.example.racertimer;

import android.location.Location;
import android.util.Log;

import com.example.racertimer.windDirection.WindProvider;
import com.example.racertimer.windDirection.WindChangedHeraldInterface;

public class StatusUIModulesDispatcher {
    private final static String PROJECT_LOG_TAG = "StatusUI";

    private LocationChanger locationChanger;
    private WindChangedHeraldInterface windChangedHeraldInterface;
    private StatusUiUpdater statusUiUpdater;

    private String[] moduleNames;
    private boolean[] moduleStatus;
    private LocationHeraldInterface[] locationHeralds;

    private Location lastLocation = null;
    private int lastWindDirection = 10000;
    private WindProvider lastProvider = null;

    public StatusUIModulesDispatcher (String[] moduleNames, LocationHeraldInterface[] locationHeralds) {
        this.moduleNames = moduleNames;
        moduleStatus = new boolean[moduleNames.length];
        this.locationHeralds = locationHeralds;
        initStatusUpdater();
        initInterfaces();
    }

    public void sendWindToContentUpdater(LocationHeraldInterface locationHerald) {
        locationHerald.onWindDirectionChanged(lastWindDirection, lastProvider);
    }

    public LocationChanger getLocationChanger () {
        return locationChanger;
    }

    public WindChangedHeraldInterface getWindChangedHerald() {
        return windChangedHeraldInterface;
    }

    public StatusUiUpdater getStatusUiUpdater() {
        if (statusUiUpdater == null)
            Log.i(PROJECT_LOG_TAG, " debug. status updater is null! ");
        return statusUiUpdater;
    }

    private void initInterfaces () {
        locationChanger = new LocationChanger() {
            @Override
            public void onLocationChanged(Location location) {
                lastLocation = location;
                sendLocationToAllModules(location);
            }
        };

        windChangedHeraldInterface = new WindChangedHeraldInterface() {
            @Override
            public void onWindDirectionChanged(int windDirection, WindProvider provider) {
                lastWindDirection = windDirection;
                lastProvider = provider;
                sendWindToAllModules(windDirection, provider);
            }
        };
    }

    private void initStatusUpdater() {
        statusUiUpdater = new StatusUiUpdater() {
            @Override
            public void onStatusChecked(boolean status) {
            }

            @Override
            public void updateUIModuleStatus(String moduleName, boolean isItReady) {
                Log.i(PROJECT_LOG_TAG, " module "+moduleName+" changing ready status to " + isItReady);
                int index = indexModuleByName(moduleName);
                moduleStatus[index] = isItReady;
                if (isItReady) {
                    index = indexModuleByName(moduleName);
                    if (lastLocation != null)
                        sendLocationByIndex(lastLocation, index);
                    if (lastWindDirection != 10000){
                        sendWindByIndex(lastWindDirection, lastProvider, index);
                        Log.i(PROJECT_LOG_TAG, " first time sending wind direction into module "+moduleName);
                    }
                }
            }
        };
    }

    private void sendLocationToAllModules(Location location) {
        for (int i =0; i < moduleNames.length; i++) {
            if (moduleStatus[i]) {
                sendLocationByIndex(location, i);
                Log.i(PROJECT_LOG_TAG, " sending new location into module "+moduleNames[i]);
            }
        }
    }

    private void sendLocationByIndex(Location location, int index) {
        locationHeralds[index].onLocationChanged(location);
    }

    private void sendWindToAllModules(int windDirection, WindProvider provider) {
        for (int i =0; i < moduleNames.length; i++) {
            if (moduleStatus[i])
                sendWindByIndex(windDirection, provider, i);
        }
    }

    private void sendWindByIndex(int windDirection, WindProvider provider, int index) {
        LocationHeraldInterface currentUpdater = locationHeralds[index];
        currentUpdater.onWindDirectionChanged(windDirection, provider);
    }

    private int indexModuleByName (String moduleName) {
        int index = 100;
        for (int i = 0; i < moduleNames.length; i++) {
            if (moduleName.equals(moduleNames[i])) {
                index = i;
                break;
            }
        }
        return  index;
    }
}
