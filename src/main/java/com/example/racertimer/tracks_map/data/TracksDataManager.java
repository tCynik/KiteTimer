package com.example.racertimer.tracks_map.data;

import android.location.Location;

import com.example.racertimer.main_activity.presentation.interfaces.LocationHeraldInterface;
import com.example.racertimer.main_activity.data.wind_direction.WindProvider;
import com.example.racertimer.main_activity.MainActivity;
import com.example.racertimer.tracks_map.data.models.GeoTrack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;

public class TracksDataManager {
    private final static String PROJECT_LOG_TAG = "tracks_data_manager";

    private boolean isTrackRecordingProgress = false;
    private ArrayList<Location> trackPoints;
    private String packageAddress;

    private GPSTrackLoader gpsTrackLoader;
    private TracksSaver tracksSaver;

    private MainActivity mainActivity;
    private LocationHeraldInterface locationHerald;

    private Long currentDate;

    public TracksDataManager(MainActivity mainActivity, String packageAddress) {
        this.mainActivity = mainActivity;
        this.packageAddress = packageAddress;
        trackPoints = new ArrayList<>();
        gpsTrackLoader = new GPSTrackLoader(mainActivity, packageAddress);
        tracksSaver = new TracksSaver(mainActivity);
        locationHerald = new LocationHeraldInterface() {
            @Override
            public void onLocationChanged(Location location) {
                TracksDataManager.this.onLocationChanged(location);
            }

            @Override
            public void onWindDirectionChanged(int windDirection, WindProvider provider) {

            }
        };
    }

    public LocationHeraldInterface getContentUpdater(){
        return locationHerald;
    }

    public void beginRecordTrack () {
        isTrackRecordingProgress = true;
    }

    public void hasMissedLocations(ArrayList<Location> missedLocations){
        trackPoints.addAll(missedLocations);
    }

    public void clearTheTrack () {
        trackPoints.clear();
        isTrackRecordingProgress = false;
    }

    public void initSavingRecordedTrack() {
        if (trackPoints.size() != 0) {
            String trackNameToBeSaved = generateTrackName();
            trackNameToBeSaved = trackNameUniquer(trackNameToBeSaved);
            askUserToSave(trackNameToBeSaved);
        } else mainActivity.clearCurrentTrack();
    }

    private void onLocationChanged (Location location) {
        if (isTrackRecordingProgress) {
            trackPoints.add(location);
        }

        if (currentDate == null) {
            currentDate = location.getTime();
        }
    }

    private String generateTrackName () {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy");
        String year = timeFormat.format(currentDate);

        timeFormat = new SimpleDateFormat("MM");
        String month = timeFormat.format(currentDate);

        timeFormat = new SimpleDateFormat("dd");
        String day = timeFormat.format(currentDate);

        String trackNameToBeSaved = year + "-" +month + "-" + day;

        return trackNameToBeSaved;
    }

    private String trackNameUniquer (String trackNameToBeChecked) {
        int modifier = 1;
        String uniqueName = trackNameToBeChecked + "_" + modifier;

        LinkedList<GeoTrack> existedTracks = loadTracksDatabase().getSavedTracks();
        for (GeoTrack nextTrack: existedTracks) {
            String nextTrackName = nextTrack.getTrackName();
            if (uniqueName.equals(nextTrackName)) {
                modifier++;
                uniqueName  = trackNameToBeChecked + "_" + modifier;
            }
        }
        return uniqueName;
    }

    public GeoTrack saveCurrentTrackByName(String trackNameToBeSaved) {
        //Log.i("bugfix", "Manager: saving track by name " + trackNameToBeSaved);
        GeoTrack trackToBeSaved = new GeoTrack();
        trackToBeSaved.setTrackName(trackNameToBeSaved);
        trackToBeSaved.setPointsListToSave(trackPoints);

        TracksDatabase writedTracks = loadTracksDatabase();
        writedTracks.addTrack(trackToBeSaved);
        tracksSaver.saveTracksDatabase(writedTracks);
        return trackToBeSaved;
    }

    public void deleteTrackByName (String nameTractToBeDeleted) {
        TracksDatabase writtenTracks = gpsTrackLoader.getSavedTracks();
        writtenTracks.deleteTrackByName(nameTractToBeDeleted);
        tracksSaver.saveTracksDatabase(writtenTracks);
    }

    private void askUserToSave (String trackNameToBeChecked) {
        mainActivity.askToSaveTrack(trackNameToBeChecked);
    }

    public TracksDatabase loadTracksDatabase () {
        //Log.i("bugfix", "Manager: loading the tracks. number is: "+ gpsTrackLoader.getSavedTracks().homMuchSavedTracks());
        return gpsTrackLoader.getSavedTracks();
    }

    public GeoTrack getGeoTrackByName (String trackName) {
        GeoTrack trackToBeReturned = null;
        LinkedList<GeoTrack> existedTracks = loadTracksDatabase().getSavedTracks();
        for (GeoTrack currentTrack: existedTracks) {
            String currentTrackName = currentTrack.getTrackName();
            if (currentTrackName.equals(trackName)) trackToBeReturned = currentTrack;
        }
        return trackToBeReturned;
    }
}
