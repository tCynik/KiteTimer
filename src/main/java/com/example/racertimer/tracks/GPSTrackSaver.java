package com.example.racertimer.tracks;

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

public class GPSTrackSaver {
    private boolean isTrackRecordingProgress = false;
    private ArrayList<Location> trackPoints;
    private String packageAddress;

    private ActivityRace activityRace;

    private Long currentDate;

    public GPSTrackSaver (ActivityRace activityRace, String packageAddress) {
        this.activityRace = activityRace;
        this.packageAddress = packageAddress;
        trackPoints = new ArrayList<>();
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
        saveTrackFile(trackNameToBeSaved);
        saveTracksList(trackNameToBeSaved);
    }

    private void saveTrackFile (String trackNameToBeSaved) {
        GeoTrack trackObject = new GeoTrack();
        trackObject.setTrackName(trackNameToBeSaved);
        trackObject.setPointsList(trackPoints);
        // TODO: определить папку, в которую сохранять трек /tracks/saved
        FileOutputStream file = null;
        try {
            file = new FileOutputStream(packageAddress+trackNameToBeSaved+".bin");
            ObjectOutputStream obj = new ObjectOutputStream(file);
            obj.writeObject(trackObject);
            obj.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveTracksList(String trackNameToBeSaved) {
        ArrayList<String> alreadyExistingTracksNames = new ArrayList<>();

        GPSTrackLoader gpsTrackLoader = new GPSTrackLoader(packageAddress);
        alreadyExistingTracksNames = gpsTrackLoader.uploadTracksList();

        FileOutputStream file = null;
        try {
            file = new FileOutputStream(packageAddress + "trackList.bin");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(file);
            for (String trackFileName: alreadyExistingTracksNames) {
                objectOutputStream.writeChars(trackFileName);
            }
            objectOutputStream.writeChars(trackNameToBeSaved);
            objectOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //+ "trackList.bin"
    }

    private void askUserToSave (String trackNameToBeChecked) {
        // TODO: make request to user by dialogMenu to save the track with the current name.
        //  if user pressed Y save the file. If user cancelled - clear the arraylist
    }
}
