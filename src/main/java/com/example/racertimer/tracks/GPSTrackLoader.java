package com.example.racertimer.tracks;

import com.example.racertimer.ActivityRace;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class GPSTrackLoader {
    private ActivityRace activityRace;
    private String listPackageAddress;

    public GPSTrackLoader (String listPackageAddress) {
        this.listPackageAddress = listPackageAddress;
    }

    public void fillTrackList () {
        ArrayList<String> tracksNames = new ArrayList<>();
        tracksNames = uploadTracksList();

        for (String trackFileName:
             tracksNames) {
            addNextFileNameToList(trackFileName);
        }
    }

    public LinkedList<GeoTrack> getSavedTracks() {
        LinkedList<GeoTrack> loadedTracks = new ArrayList<>();
        File trackListFile = new File (listPackageAddress + "savedTracks.bin");
        Scanner scanner = null;
        try {
            scanner = new Scanner(trackListFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace(); // убрать при не сузествующем файле?
        }
        while (scanner.hasNext()) { loadedTracks.add(scanner.next());}
        scanner.close();

        return loadedTracks;
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