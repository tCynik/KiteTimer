package com.tcynik.racertimer.tracks_map.presentation;

import android.location.Location;

import com.tcynik.racertimer.tracks_map.data.models.GeoTrack;

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
