package com.example.racertimer.tracks;

import android.location.Location;

import java.util.ArrayList;

public class TestTrack {
    private ArrayList<TestPoint> track;
    int counter = 0;

    private void addPoint(TestPoint currPoint) {
        TestPoint point = currPoint;
        if (counter > 0) {
            TestPoint prevPoint = track.get(counter - 1);
            int bearing = (int) prevPoint.bearingTo(point);
            point.setBearing(bearing);
            int speed = prevPoint.distTo(point);
            point.setSpeed(speed);
        }
        track.add(point);
    }
}

class TestPoint {
    private int latitude, longitude, bearing, speed;

    /** в этой системе координат:
     * @param latitude - координата Y, увеличение вверх
     * @param longitude - координата X, увеличене вправо
     * bearing - азимут от 0 до 360; 0 вверх; отсчет по часовой стрелке
     */

    public TestPoint(int latitude, int longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getLatitude() {
        return latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public double bearingTo(TestPoint secondPoint) {
        double bearing;
        int dist = distTo(secondPoint);
        int deltaLatitude =  secondPoint.getLatitude() - latitude;
        int deltaLongitude = secondPoint.getLongitude() - longitude;
        double cos = Math.cos(longitude / dist);
        if (deltaLatitude > 0) { // верхний полукруг 180 град.
            if (deltaLongitude > 0) // верхний правый сектор, 90 - косинус longitude
                bearing = 90 - cos;

            else // верхний левый сектор, 360 - cos longitude
                bearing = 360 - cos;
        }
        else { // нижний полукруг 180 град.
            if (deltaLongitude > 0) // нижний правый сектор, 90+cos longitude
                bearing = 90 + cos;

            else // нижний левый сектор, 270 - cos longitude
                bearing = 270 - cos;
        }
        return bearing;
    }

    public int distTo (TestPoint secondPoint) {
        int deltaLatitude =  secondPoint.getLatitude() - latitude;
        int deltaLongitude = secondPoint.getLongitude() - longitude;
        int dist = (int) Math.pow((Math.pow(deltaLatitude, 2) + Math.pow(deltaLongitude, 2)), 0.5);
        return dist;
    }

    public Location castToLocation () { // casting to Location for test proposes
        Location location = new Location("gps");
        location.setLatitude(50 + latitude / 1000);
        location.setLongitude(90 + longitude/ 1000);
        location.setBearing(bearing);
        location.setSpeed(speed);
        return location;
    }

    public void setBearing(int bearing) {
        this.bearing = bearing;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}