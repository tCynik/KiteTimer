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
        clearListAndFillTop();
        makeTracksFile();
        loadTracksData();
        fillListInView();
    }

    private void makeTracksFile (){
        tracksDataManager.saveTrackDatabase(new TracksDatabase());
    }

    private void loadTracksData() {
        if (gpsTrackLoader == null)
        Log.i("bugfix", "GPSTrackLoader is null!!! ");
        tracksDatabase = gpsTrackLoader.getSavedTracks();
    }

    private void fillListInView() {
        if (tracksDatabase == null) {
            fillNextLine("no saved tracks", "");
        } else
        if (tracksDatabase.isItAnyTracks()) {
            LinkedList<GeoTrack> tracksArray = tracksDatabase.getSavedTracks();
            for (GeoTrack currentTrack: tracksArray) {
                String trackName = currentTrack.getTrackName();
                String trackDate = currentTrack.getDatetime();
                fillNextLine(trackDate, trackName);
            }
        } else {
            fillNextLine("no saved tracks", "");
        }
    }

    private void clearListAndFillTop () {
        trackLineToBeFilled.removeAllViewsInLayout();
        fillNextLine("name", "date");
    }

    private void fillNextLine(String date, String name) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View item = layoutInflater.inflate(R.layout.tracks_list_line, trackLineToBeFilled, false);
        TextView trackName = item.findViewById(R.id.tracks_name);
        TextView trackDate = item.findViewById(R.id.tracks_date);

        trackName.setText(name);
        trackDate.setText(date);

        trackLineToBeFilled.addView(item);
    }
}