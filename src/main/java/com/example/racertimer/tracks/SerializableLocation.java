package com.example.racertimer.tracks;

import android.location.Location;

import java.io.Serializable;

public class SerializableLocation extends Location implements Serializable {
    public SerializableLocation(String provider) {
        super(provider);
    }
}
