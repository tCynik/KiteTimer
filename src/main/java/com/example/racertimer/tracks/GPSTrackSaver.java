package com.example.racertimer.tracks;

import android.location.Location;

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

    private ActivityRace activityRace;

    private Long currentDate;

    public GPSTrackSaver (ActivityRace activityRace) {
        this.activityRace = activityRace;
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

        return trackNameToBeSaved;
    }

    private String trackNameUniquer (String trackNameToBeChecked) {
        String uniqueName;
        //вот тут перебираем имена файлов, каждый раз добавляя цифровой модификатор, пока не получим
        // уникальное имя (для начала пробуем без модификатора). Когда находим уникальное имя, пишем на него.
        int counter = 0;
        if (!checkNextName(trackNameToBeChecked)) { // если имя без модификатора не найдено, запускаем перебор
            boolean nameMached = false;
            while (!nameMached) {
                counter++;
                nameMached = checkNextName(trackNameToBeChecked+"-"+counter);
            }
            uniqueName = trackNameToBeChecked + "-" + counter;
        } else uniqueName = trackNameToBeChecked; // если первоначального имени не было, сохраняем его как есть

        return uniqueName;
    }

    private boolean checkNextName (String nextNameToComplain) {
        boolean nameMached = true;
        try {
            FileInputStream file = new FileInputStream (nextNameToComplain);
        } catch (FileNotFoundException e) {
            nameMached = false;
        }
        return nameMached;
    }

    public void saveTrackFile (String trackNameToBeSaved) {
        FileOutputStream file = null;
        try {
            file = new FileOutputStream(trackNameToBeSaved);
            ObjectOutputStream obj = new ObjectOutputStream(file);
            obj.writeObject(trackPoints);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void askUserToSave (String trackNameToBeChecked) {
        // TODO: make request to user by dialogMenu to save the track with the current name.
        //  if user pressed Y save the file. If user cancelled - clear the arraylist
    }
}
