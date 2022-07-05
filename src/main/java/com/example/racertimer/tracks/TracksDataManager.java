package com.example.racertimer.tracks;

import android.location.Location;
import android.util.Log;

import com.example.racertimer.MainActivity;

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

    private Long currentDate;

    public TracksDataManager(MainActivity mainActivity, String packageAddress) {
        this.mainActivity = mainActivity;
        this.packageAddress = packageAddress;
        trackPoints = new ArrayList<>();
        gpsTrackLoader = new GPSTrackLoader(mainActivity, packageAddress);
        tracksSaver = new TracksSaver(mainActivity);
    }

    public void onLocationChanged (Location location) {
        if (isTrackRecordingProgress) {
            trackPoints.add(location);
        }

        if (currentDate == null) {
            currentDate = location.getTime();
        }
    }

    public void beginRecordTrack () {
        isTrackRecordingProgress = true;
    }

    /**
     * Логика сохранения трека такова:
     * пользователь жмет остановку записи, ему приходит диалоговое окно:
     * Остановить запись? Варианты ответа: отмена, удалить трек, сохранить трек с именем таким-то.
     * отмена - убираем окно, ничего не меняется.
     * Удалить трек - переспрашиваем, вызываем обнуление трека
     * сохранить трек - записываем трек с указанным именем.
     */

    public void clearTheTrack () {
        trackPoints.clear();
        isTrackRecordingProgress = false;
    }

    public void initSavingRecordedTrack() {
        String trackNameToBeSaved = generateTrackName();
        trackNameToBeSaved = trackNameUniquer(trackNameToBeSaved);
        askUserToSave(trackNameToBeSaved);
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
        Log.i("bugfix", "Manager: saving track by name " + trackNameToBeSaved);
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
        // TODO: make request to user by dialogMenu to save the track with the current name.
        //  if user pressed Y save the file. If user cancelled - clear the arraylist
    }

    public TracksDatabase loadTracksDatabase () {
        Log.i("bugfix", "Manager: loading the tracks. number is: "+ gpsTrackLoader.getSavedTracks().homMuchSavedTracks());
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
