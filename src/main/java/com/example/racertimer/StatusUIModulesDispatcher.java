package com.example.racertimer;

import android.location.Location;
import android.util.Log;

import com.example.racertimer.Instruments.WindProvider;
import com.example.racertimer.windDirection.WindChangedHerald;

public class StatusUIModulesDispatcher {
    private final static String PROJECT_LOG_TAG = "StatusUI";

    private LocationChanger locationChanger;
    private WindChangedHerald windChangedHerald;
    private StatusUiUpdater statusUiUpdater;

    private String[] moduleNames;
    private boolean[] moduleStatus;
    private ContentUpdater[] contentUpdaters;

    private Location lastLocation = null;
    private int lastWindDirection = 10000;
    private WindProvider lastProvider = null;

    public StatusUIModulesDispatcher (String[] moduleNames, ContentUpdater[] contentUpdaters) {
        this.moduleNames = moduleNames;
        moduleStatus = new boolean[moduleNames.length];
        this.contentUpdaters = contentUpdaters;
        initInterfaces();
    }

    public LocationChanger getLocationChanger () {
        return locationChanger;
    }

    public WindChangedHerald getWindChangedHerald() {
        return windChangedHerald;
    }

    public StatusUiUpdater getStatusUiUpdater() {
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

        windChangedHerald = new WindChangedHerald() {
            @Override
            public void onWindDirectionChanged(int windDirection, WindProvider provider) {
                lastWindDirection = windDirection;
                lastProvider = provider;
                sendWindToAllModules(windDirection, provider);
            }
        };

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
                    if (lastWindDirection != 1000)
                        sendWindByIndex(lastWindDirection, lastProvider, index);
                }
            }
        };
    }

    private void sendLocationToAllModules(Location location) {
        for (int i =0; i < moduleNames.length; i++) {
            if (moduleStatus[i])
                sendLocationByIndex(location, i);
        }
    }

    private void sendLocationByIndex(Location location, int index) {
        contentUpdaters[index].onLocationChanged(location);
    }

    private void sendWindToAllModules(int windDirection, WindProvider provider) {
        for (int i =0; i < moduleNames.length; i++) {
            if (moduleStatus[i])
                sendWindByIndex(windDirection, provider, i);
        }
    }

    private void sendWindByIndex(int windDirection, WindProvider provider, int index) {
        contentUpdaters[index].onWindDirectionChanged(windDirection, provider);
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
