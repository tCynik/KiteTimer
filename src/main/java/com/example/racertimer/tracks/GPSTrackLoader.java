package com.example.racertimer.tracks;

import android.util.Log;

import com.example.racertimer.ActivityRace;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

public class GPSTrackLoader {
    private final static String PROJECT_LOG_TAG = "tracks_loader";

    private ActivityRace activityRace;
    private String listPackageAddress;
    private TracksDatabase tracksDatabase;

    public GPSTrackLoader (ActivityRace activityRace, String listPackageAddress) {
        this.activityRace = activityRace;
        this.listPackageAddress = listPackageAddress;
        this.tracksDatabase = new TracksDatabase();
    }

    public TracksDatabase getSavedTracks() {
        try {
            FileInputStream trackListFile = activityRace.openFileInput("saved.saved_tracks.bin");
            ObjectInputStream inputObject = new ObjectInputStream(trackListFile);
            tracksDatabase = (TracksDatabase) inputObject.readObject();
            inputObject.close();
            trackListFile.close();
            Log.i(PROJECT_LOG_TAG, "tracks database was loaded");
            Log.i("bugfix", "Loader: number of saved tracks " + tracksDatabase.homMuchSavedTracks());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return tracksDatabase;
    }

    private void addNextFileNameToList (String filename) {
        // TODO: здесь инфлейтим в новую строчку списка новую запись из файла
    }
}

/**
 * логика загрузки:
 * вызываем диалоговое меню/фрагмент (обдумать), в который выгружается список всех ранее сохраненных треков.
 * выбрав нужный, можем его либо удалить, либо отобразить на карте
 */