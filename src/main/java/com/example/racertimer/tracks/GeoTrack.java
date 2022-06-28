package com.example.racertimer.tracks;

import android.location.Location;

import java.io.Serializable;
import java.util.ArrayList;

public class GeoTrack implements Serializable {
    private String trackName;
    private String datetime;

    private ArrayList<GeoPoint> pointsList;

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
        ArrayList<Location> locationList = new ArrayList<>();
        for (GeoPoint geoPoint: pointsList) {
            Location location = castGeoPointToLocation(geoPoint);
            locationList.add(location);
        }
        return locationList;
    }

    public void setPointsListToSave(ArrayList<Location> locationList) {
        pointsList = new ArrayList<>();
        for (Location location: locationList) {
            GeoPoint geoPoint = castLocationToGeoPoint(location);
            pointsList.add(geoPoint);
        }
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public void addPoint (Location location) {
        GeoPoint geoPoint = castLocationToGeoPoint(location);
        pointsList.add(geoPoint);
    }

    public long getDuration () {
        long duration = 0;
        if (! pointsList.isEmpty()) {
            GeoPoint geoPoint = pointsList.get(0);
            long timeStart = geoPoint.getTime();
            geoPoint = pointsList.get(pointsList.size() - 1);
            long timeEnd = geoPoint.getTime();
            duration = timeEnd - timeStart;
        }
        return duration;
    }

    private Location castGeoPointToLocation(GeoPoint geoPoint) {
        Location location = new Location("gps");
        location.setLongitude(geoPoint.getLongitude());
        location.setLatitude(geoPoint.getLatitude());
        location.setBearing(geoPoint.getBearing());
        location.setSpeed(geoPoint.getSpeed());
        location.setTime(geoPoint.getTime());
        return location;
    }

    private GeoPoint castLocationToGeoPoint(Location location) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        float bearing = location.getBearing();
        float speed = location.getSpeed();
        long time = location.getTime();
        GeoPoint geoPoint = new GeoPoint(longitude, latitude, bearing, speed, time);
        return geoPoint;
    }
}

class GeoPoint implements Serializable {
    double longitude, latitude;
    float bearing, speed;
    long time;

    public GeoPoint(double longitude, double latitude, float bearing, float speed, long time) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.bearing = bearing;
        this.speed = speed;
        this.time = time;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public float getBearing() {
        return bearing;
    }

    public float getSpeed() {
        return speed;
    }

    public long getTime() {
        return time;
    }
}
