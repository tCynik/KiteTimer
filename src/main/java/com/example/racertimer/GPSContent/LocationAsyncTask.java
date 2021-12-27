package com.example.racertimer.GPSContent;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;

public class LocationAsyncTask extends AsyncTask<Void, Location, Void> {
    private LocationManager locationManager;
    private LocationListener locationListener;

    public LocationAsyncTask (LocationManager locationManager, LocationListener locationListener) {
        this.locationManager = locationManager;
        this.locationListener = locationListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

    @Override
    protected void onProgressUpdate(Location... values) {
        super.onProgressUpdate(values);
    }
}
