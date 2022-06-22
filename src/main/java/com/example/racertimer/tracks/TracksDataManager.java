package com.example.racertimer.tracks;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.racertimer.ActivityRace;

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

//    public void stopRecordTrack () {
//        initSavingRecordedTrack();
//    }

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

        //вот тут перебираем имена файлов, каждый раз добавляя цифровой модификатор, пока не получим
        // уникальное имя (для начала пробуем без модификатора). Когда находим уникальное имя, пишем на него.
//        int counter = 0;
//        if (!checkNameUniqueness(trackNameToBeChecked)) { // если имя без модификатора не найдено, запускаем перебор
//            boolean nameMatched = false;
//            while (!nameMatched) {
//                counter++;
//                nameMatched = checkNameUniqueness(trackNameToBeChecked+"-"+counter);
//            }
//            uniqueName = trackNameToBeChecked + "-" + counter;
//        } else uniqueName = trackNameToBeChecked; // если первоначального имени не было, сохраняем его как есть
//
//        return uniqueName;
    }

//    private int parseDailyNumber(String trackName) {
//        String radix = "_";
//        char[] parsedName = trackName.toCharArray();
//        for (char nextChar: parsedName) {
//
//        }
//        return
//    }

    public void saveCurrentTrackByName(String trackNameToBeSaved) {
        Log.i("bugfix", "Manager: saving track by name " + trackNameToBeSaved);
        GeoTrack trackToBeSaved = new GeoTrack();
        trackToBeSaved.setTrackName(trackNameToBeSaved);
        trackToBeSaved.setPointsList(trackPoints);

        TracksDatabase writedTracks = loadTracksDatabase();
        writedTracks.addTrack(trackToBeSaved);

        saveTracksDatabase(writedTracks);
    }

    public void saveTracksDatabase(TracksDatabase tracksDatabase) {
        try {
            Log.i("bugfix", "Manager: saving database " );
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
        saveTracksDatabase(writedTracks);
    }

    private void askUserToSave (String trackNameToBeChecked) {
        Log.i("bugfix", "DataManager: asking user to save " + trackNameToBeChecked);

        activityRace.askToSaveTrack(trackNameToBeChecked);
        // TODO: make request to user by dialogMenu to save the track with the current name.
        //  if user pressed Y save the file. If user cancelled - clear the arraylist
    }

    public TracksDatabase loadTracksDatabase () {
        return gpsTrackLoader.getSavedTracks();
    }
}
