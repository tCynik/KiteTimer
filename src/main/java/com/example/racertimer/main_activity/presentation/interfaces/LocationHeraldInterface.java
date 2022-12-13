package com.example.racertimer.main_activity.presentation.interfaces;

import android.location.Location;

import com.example.racertimer.main_activity.data.wind_direction.WindChangedHeraldInterface;

public interface LocationHeraldInterface extends WindChangedHeraldInterface {
    void onLocationChanged(Location location);
}
