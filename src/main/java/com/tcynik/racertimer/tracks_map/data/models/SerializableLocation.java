package com.tcynik.racertimer.tracks_map.data.models;

import android.location.Location;

import java.io.Serializable;

public class SerializableLocation extends Location implements Serializable {
    public SerializableLocation(String provider) {
        super(provider);
    }
}
