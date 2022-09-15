package com.example.racertimer;

import android.location.Location;

import com.example.racertimer.windDirection.WindChangedHerald;

public interface LocationHerald extends WindChangedHerald {
    void onLocationChanged(Location location);
}
