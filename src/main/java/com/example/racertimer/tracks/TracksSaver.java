package com.example.racertimer.tracks;

import android.content.Context;
import android.util.Log;

import com.example.racertimer.ActivityRace;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class TracksSaver {
    private final static String PROJECT_LOG_TAG = "racer_timer_saver";

    private ActivityRace activityRace;

    public TracksSaver(ActivityRace activityRace) {
        this.activityRace = activityRace;
    }

    public void saveTracksDatabase(TracksDatabase tracksDatabase) {
        try {
            Log.i("bugfix", "Manager: saving database " );
            FileOutputStream fileOutputStream = activityRace.openFileOutput("saved.saved_tracks.bin", Context.MODE_PRIVATE);//packageAddress + "savedTracks.bin");
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
