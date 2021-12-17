package com.example.racertimer.GPSContent;

import android.location.Location;
import android.location.LocationListener;

import androidx.annotation.NonNull;

import java.util.List;

public class LocListener implements LocationListener {
    private LocListenerInterface locListenerInterface;



    @Override
    public void onLocationChanged(@NonNull Location location) {
        locListenerInterface.whenLocationChanged(location);
    }

    public void setLocListenerInterface(LocListenerInterface locListenerInterface) {
        this.locListenerInterface = locListenerInterface;
    }
}
