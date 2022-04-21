package com.example.racertimer.tracks;

import android.location.Location;

import java.util.ArrayList;

public class GeoTrack {
    private String trackName;
    private String datetime;
    private ArrayList<Location> pointsList;

    public String getTrackName() {
        return trackName;
    }

    public String getDatetime() {
        return datetime;
    }

    public ArrayList<Location> getPointsList() {
        return pointsList;
    }

    public int getLength() {
        return pointsList.size();
    }

    public Location getTrackPoint(int position) {
        return pointsList.get(position);
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public void addPoint (Location location) {
        pointsList.add(location);

    }
}
