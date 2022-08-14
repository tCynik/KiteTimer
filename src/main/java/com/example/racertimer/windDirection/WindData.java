package com.example.racertimer.windDirection;

import android.content.Context;
import android.util.Log;

import com.example.racertimer.Instruments.WindProvider;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class WindData {
    private final static String PROJECT_LOG_TAG = "Wind_Data";

    private Context context;

    private final int TIME_WIND_INFORMATION_ACTUALITY = 6;
    private final int DEFAULT_WIND_DIRECTION = 202; // usual wind in developer's home spot

    public WindData (Context context) {
        this.context = context;
    }

    public void saveWindData (int windDirection, WindProvider windProvider) {
        if (windProvider == WindProvider.CALCULATED || windProvider == WindProvider.MANUAL) {
            SavedWindState savedWindState = new SavedWindState(windDirection, windProvider);
            outputWindStatus(savedWindState);
        }
    }

    private void outputWindStatus (SavedWindState savedWindState) {
        Log.i(PROJECT_LOG_TAG, " Try to save windState: ");
        try {
            FileOutputStream fileOutputStream = context.openFileOutput("saved.last_wind_dir.bin", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(savedWindState);
            objectOutputStream.close();
            fileOutputStream.close();
            Log.i(PROJECT_LOG_TAG, "    ...Data saved successful ");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(PROJECT_LOG_TAG, "    ...ERROR: file not found ");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(PROJECT_LOG_TAG, "    ...ERROR: IO exception ");
        }
    }

    public void returnWindData (WindChangedHerald windChangedHerald) {
        int windDirection = DEFAULT_WIND_DIRECTION;
        WindProvider windProvider = WindProvider.DEFAULT;
        SavedWindState savedWindState = loadWindData();
        if (savedWindState != null) {
            savedWindState.getWindDirection();
            windProvider = analyzeProviderActuality(savedWindState);
        }
        windChangedHerald.onWindDirectionChanged(windDirection, windProvider);
    }

    private WindProvider analyzeProviderActuality(SavedWindState savedWindState) {
        /** аналаиз актуальности данных по ветру
         * тип данных \ сегодня  \  раньше
         * -------------------------------------
         * CALCULATED \ как есть \ HISTORY
         * MANUAL     \ как есть \ HISTORY
         * HISTORY    \ неприменимо, не подлежит сохранению
         * DEFAULT    \ если нет никаких данных, не подлежит сохранению
         * FORECAST   \ актуально только сейчас, не подлежит сохранению
         * */
        WindProvider windProvider;

        Date currentTime = Calendar.getInstance().getTime();
        long savedInstanceTime = savedWindState.getDate().getTime();
        int timeDifferenceMin = (int) (currentTime.getTime() - savedInstanceTime) / 1000 / 60;
        if (timeDifferenceMin < TIME_WIND_INFORMATION_ACTUALITY * 60) {
            windProvider = savedWindState.getWindProvider();
        }
        else {
            windProvider = WindProvider.HISTORY;
        }
        return windProvider;
    }

    private SavedWindState loadWindData () {
        SavedWindState state = null;
        Log.i(PROJECT_LOG_TAG, " Try to load the wind data from memory...");
        try{
            FileInputStream fileInputStream = context.openFileInput("saved.last_wind_dir.bin");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            SavedWindState savedWindState = (SavedWindState) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            state = savedWindState;
            Log.i(PROJECT_LOG_TAG, "    ...Wind data read successful");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(PROJECT_LOG_TAG, "    ...ERROR: file not found");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(PROJECT_LOG_TAG, "    ...ERROR: IO exception");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.i(PROJECT_LOG_TAG, "    ...ERROR: ClassNotFoundException");
        }
        return state;
    }
}

class SavedWindState implements Serializable {
    private int windDirection;
    private WindProvider windProvider;
    private Date date;

    public SavedWindState(int windDirection, WindProvider windProvider) {
        this.windDirection = windDirection;
        this.windProvider = windProvider;
        date = Calendar.getInstance().getTime();
    }

    public Date getDate() {
        return date;
    }

    public int getWindDirection() {
        return windDirection;
    }

    public WindProvider getWindProvider() {
        return windProvider;
    }
}
