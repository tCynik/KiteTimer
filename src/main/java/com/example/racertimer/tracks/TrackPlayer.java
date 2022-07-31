package com.example.racertimer.tracks;

import android.location.Location;

import java.util.ArrayList;

public class TrackPlayer {

    public void playTheTrack (ArrayList<Location> trackToPlay, TrackPlayerDisplay playerDisplay) {
        for (Location location: trackToPlay) {
            playerDisplay.onSwitchNextLocation(location);
        }
    }

    public void playTheTrack (GeoTrack geoTrack, TrackPlayerDisplay playerDisplay) {
        ArrayList<Location> trackToPlay = geoTrack.getPointsList();
        playTheTrack(trackToPlay, playerDisplay);
    }
}
