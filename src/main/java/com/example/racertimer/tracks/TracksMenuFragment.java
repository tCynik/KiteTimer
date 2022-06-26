package com.example.racertimer.tracks;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.racertimer.ActivityRace;
import com.example.racertimer.R;

import java.text.SimpleDateFormat;
import java.util.LinkedList;

public class TracksMenuFragment extends Fragment {
    private TracksDataManager tracksDataManager;
    private GPSTrackLoader gpsTrackLoader;
    private TracksDatabase tracksDatabase;

    private LinearLayout trackLineToBeFilled;

    public TracksMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracks_menu, null);
        ActivityRace activityRace = (ActivityRace) getActivity();
        tracksDataManager = new TracksDataManager((ActivityRace) getActivity(), "");
        gpsTrackLoader = new GPSTrackLoader(activityRace, activityRace.getTracksPackage());
        trackLineToBeFilled = view.findViewById(R.id.tracks_line_to_fill);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTrackList();
    }

    public void updateTrackList() {
        Log.i("bugfix", "fragment: updating database ");
        clearListAndFillTop();
        loadTracksData();
        fillListInView();
    }

    private void loadTracksData() {
        Log.i("bugfix", "fragment: loading database ");
        tracksDatabase = tracksDataManager.loadTracksDatabase();
    }

    private void fillListInView() {
        Log.i("bugfix", "fragment: starting fill the track list ");
        if (tracksDatabase == null) {
            fillNextLine("no saved tracks", "");
        } else {
            Log.i("bugfix", "fragment: database size = " + tracksDatabase.getSavedTracks().size());
            if (tracksDatabase.isItAnyTracks()) {
                Log.i("bugfix", "fragment: the tracks are founded in the database ");
                LinkedList<GeoTrack> tracksArray = tracksDatabase.getSavedTracks();
                for (GeoTrack currentTrack: tracksArray) {
                    Log.i("bugfix", "fragment: filling next line with name = " + currentTrack.getTrackName() );
                    String trackName = currentTrack.getTrackName();

                    String trackDuration = durationToString(currentTrack.getDuration());
                    fillNextLine(trackName, trackDuration);
                }
            } else {
                fillNextLine("no saved tracks", "");
            }
        }
    }

    private String durationToString (long durationTime) {
        String durationString = "0";
        if (durationTime != 0) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm:ss");
            durationString = timeFormat.format(durationTime);
        }
        return durationString;
    }

    private void clearListAndFillTop () {
        trackLineToBeFilled.removeAllViewsInLayout();
        fillNextLine("name", "duration");
    }

    private void fillNextLine(String name, String duration) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View item = layoutInflater.inflate(R.layout.tracks_list_line, trackLineToBeFilled, false);
        TextView trackName = item.findViewById(R.id.tracks_name);
        TextView trackDuration = item.findViewById(R.id.track_duration);
        trackName.setText(name);
        trackDuration.setText(duration);

        trackLineToBeFilled.addView(item);
    }
}