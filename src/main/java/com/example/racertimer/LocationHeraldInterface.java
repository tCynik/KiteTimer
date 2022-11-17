package com.example.racertimer;

import android.location.Location;

import com.example.racertimer.windDirection.WindChangedHeraldInterface;

public interface LocationHeraldInterface extends WindChangedHeraldInterface {
    void onLocationChanged(Location location);
}
