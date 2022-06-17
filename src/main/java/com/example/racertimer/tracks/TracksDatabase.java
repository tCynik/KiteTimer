package com.example.racertimer.tracks;

import java.util.LinkedList;

public class TracksDatabase {
    private LinkedList<GeoTrack> savedTracks;

    public void addTrack (GeoTrack trackToBeAdded) {
        savedTracks.add(trackToBeAdded);
    }

    public LinkedList<GeoTrack> getSavedTracks () {
        return savedTracks;
    }

    public boolean isItAnyTracks () {
        if (savedTracks.isEmpty()) return false;
        else return true;
    }

    public LinkedList<GeoTrack> deleteTrackByName (String nameToBeingDeleted) {
        for (int i = 0; i < savedTracks.size(); i++) {
            if (savedTracks.get(i).getTrackName() == nameToBeingDeleted) {
                savedTracks.remove(i);
                break;
            }
        }
        return savedTracks;
    }
}
