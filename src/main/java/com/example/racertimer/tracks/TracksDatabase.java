package com.example.racertimer.tracks;

import android.util.Log;

import java.io.Serializable;
import java.util.LinkedList;

public class TracksDatabase implements Serializable {
    private LinkedList<GeoTrack> savedTracks;

    public TracksDatabase () {
        savedTracks = new LinkedList<>();
    }

    public void addTrack (GeoTrack trackToBeAdded) {
        savedTracks.add(trackToBeAdded);
    }

    public LinkedList<GeoTrack> getSavedTracks () {
        return savedTracks;
    }

    public boolean isItAnyTracks () {
        Log.i("bugfix", "tracksDatabase: checking is it any tracks. DB size = " + savedTracks.size());
        if (savedTracks.isEmpty()) return false;

        else return true;
    }

    public int homMuchSavedTracks() {
        return savedTracks.size();
    }

    public LinkedList<GeoTrack> deleteTrackByName (String nameToBeingDeleted) {
        for (int i = 0; i < savedTracks.size(); i++) {
            String nextTrackName = savedTracks.get(i).getTrackName();

            if (nextTrackName.equals(nameToBeingDeleted)) {
                savedTracks.remove(i);
                break;
            }
        }
        return savedTracks;
    }
}
