package com.example.racertimer.tracks;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.racertimer.ActivityRace;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;

public class TracksDataManager {
    private final static String PROJECT_LOG_TAG = "tracks_data_manager";

    private boolean isTrackRecordingProgress = false;
    private ArrayList<Location> trackPoints;
    private String packageAddress;

    private GPSTrackLoader gpsTrackLoader;

    private ActivityRace activityRace;

    private Long currentDate;

    public TracksDataManager(ActivityRace activityRace, String packageAddress) {
        this.activityRace = activityRace;
        this.packageAddress = packageAddress;
        trackPoints = new ArrayList<>();
        gpsTrackLoader = new GPSTrackLoader(activityRace, packageAddress);
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
     * сохранить трек - записываем трекс указанным именем.
     */

    public void stopRecordTrack () {
        initSavingRecordedTrack();
    }

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

        Log.i("bugfix", "generated trackName is: "+ trackNameToBeSaved );
        return trackNameToBeSaved;
    }

    private String trackNameUniquer (String trackNameToBeChecked) {
        String uniqueName;
        //вот тут перебираем имена файлов, каждый раз добавляя цифровой модификатор, пока не получим
        // уникальное имя (для начала пробуем без модификатора). Когда находим уникальное имя, пишем на него.
        int counter = 0;
        if (!checkNameUniquelity(trackNameToBeChecked)) { // если имя без модификатора не найдено, запускаем перебор
            boolean nameMached = false;
            while (!nameMached) {
                counter++;
                nameMached = checkNameUniquelity(trackNameToBeChecked+"-"+counter);
            }
            uniqueName = trackNameToBeChecked + "-" + counter;
        } else uniqueName = trackNameToBeChecked; // если первоначального имени не было, сохраняем его как есть

        return uniqueName;
    }

    private boolean checkNameUniquelity(String nextNameToComplain) {
        boolean nameMached = true;
        try {
            FileInputStream file = new FileInputStream (nextNameToComplain);
        } catch (FileNotFoundException e) {
            nameMached = false;
        }
        return nameMached;
    }

    public void saveTheTrack (String trackNameToBeSaved) {
        GeoTrack trackToBeSaved = new GeoTrack();
        trackToBeSaved.setTrackName(trackNameToBeSaved);
        trackToBeSaved.setPointsList(trackPoints);

        TracksDatabase writedTracks = gpsTrackLoader.getSavedTracks();
        writedTracks.addTrack(trackToBeSaved);

        saveTrackDatabase(writedTracks);
    }

    public void saveTrackDatabase (TracksDatabase tracksDatabase) {
        try {
            FileOutputStream fileOutputStream = activityRace.openFileOutput("saved.savedTracks.bin", Context.MODE_PRIVATE);//packageAddress + "savedTracks.bin");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(tracksDatabase);
            objectOutputStream.close();
            fileOutputStream.close();
            Log.i(PROJECT_LOG_TAG, "tracks database was saved");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteTrackByName (String nameTractToBeDeleted) {
        TracksDatabase writedTracks = gpsTrackLoader.getSavedTracks();
        writedTracks.deleteTrackByName(nameTractToBeDeleted);
        saveTrackDatabase(writedTracks);
    }

    private void askUserToSave (String trackNameToBeChecked) {

        // TODO: make request to user by dialogMenu to save the track with the current name.
        //  if user pressed Y save the file. If user cancelled - clear the arraylist
    }

    public LinkedList<GeoTrack> loadTracksDatabase () {
        TracksDatabase tracksDatabase = gpsTrackLoader.getSavedTracks();
        return tracksDatabase.getSavedTracks();
    }
}
