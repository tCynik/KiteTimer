package com.example.racertimer.tracks;

import android.location.Location;

import java.io.Serializable;
import java.util.ArrayList;

public class GeoTrack implements Serializable {
    private String trackName;
    private String datetime;
    private ArrayList<Location> pointsList;

    public GeoTrack () {
        pointsList = new ArrayList<>();
    }

    public String getTrackName() {
        return trackName;
    }

    public String getDatetime() {
        return datetime;
    }

    public ArrayList<Location> getPointsList() {
        return pointsList;
    }

    public void setPointsList(ArrayList<Location> pointList) {
        this.pointsList = pointList;
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

    public long getDuration () {
        long duration = 0;
        if (! pointsList.isEmpty()) {
            Location location = pointsList.get(0);
            long timeStart = location.getTime();
            location = pointsList.get(pointsList.size());
            long timeEnd = location.getTime();
            duration = timeEnd - timeStart;
        }
        return duration;
    }
}
