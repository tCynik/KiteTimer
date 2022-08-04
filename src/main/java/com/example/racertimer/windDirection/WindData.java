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

public class WindData {
    private Context context;

    public WindData (Context context) {
        this.context = context;
    }

    public void saveWindData (int windDirection, WindProvider windProvider) {
        SavedWindState savedWindState = new SavedWindState(windDirection, windProvider);
        outputWindStatus(savedWindState);
        Log.i("bugfix", " windData: saving. wind = " +windDirection+", provider = " +windProvider);
    }

    private void outputWindStatus (SavedWindState savedWindState) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput("saved.lst_wind_dir.bin", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(savedWindState);
            objectOutputStream.close();
            fileOutputStream.close();
            Log.i("bugfix", " windData: Data saved successful ");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void returnWindData (WindChangedHerald windChangedHerald) {
        int windDirection = 10000;
        WindProvider windProvider = WindProvider.DEFAULT;
        SavedWindState savedWindState = loadWindData();
        if (savedWindState != null) {
            windDirection = savedWindState.getWindDirection();
            windProvider = savedWindState.getWindProvider();
        }
        Log.i("bugfix", " windData: Data loaded. wind = " + windDirection+ ", provider = " +windProvider);
        windChangedHerald.onWindDirectionChanged(windDirection, windProvider);
    }

    private SavedWindState loadWindData () {
        SavedWindState state = null;
        try{
            FileInputStream fileInputStream = context.openFileInput("saved.lst_wind_dir.bin");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            SavedWindState savedWindState = (SavedWindState) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            state = savedWindState;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return state;
    }
}

class SavedWindState {
    private int windDirection;
    private WindProvider windProvider;

    public SavedWindState(int windDirection, WindProvider windProvider) {
        this.windDirection = windDirection;
        this.windProvider = windProvider;
        Log.i("bugfix", " windState: creating new instance ");
    }

    public int getWindDirection() {
        return windDirection;
    }

    public WindProvider getWindProvider() {
        return windProvider;
    }
}
