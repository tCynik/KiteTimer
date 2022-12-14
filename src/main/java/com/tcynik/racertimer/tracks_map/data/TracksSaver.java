package com.tcynik.racertimer.tracks_map.data;

import android.content.Context;
import android.util.Log;

import com.tcynik.racertimer.main_activity.MainActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class TracksSaver {
    private final static String PROJECT_LOG_TAG = "racer_timer_saver";

    private MainActivity mainActivity;

    public TracksSaver(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void saveTracksDatabase(TracksDatabase tracksDatabase) {
        try {
            Log.i("bugfix", "Manager: saving database " );
            FileOutputStream fileOutputStream = mainActivity.openFileOutput("saved.saved_tracks.bin", Context.MODE_PRIVATE);//packageAddress + "savedTracks.bin");
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
}
