package com.example.racertimer.tracks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.racertimer.ActivityRace;
import com.example.racertimer.R;

public class TracksMenuFragment extends Fragment {
    GPSTrackLoader gpsTrackLoader;

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
        gpsTrackLoader = new GPSTrackLoader(activityRace.getTracksPackage());

        return view;
    }

    private void updateTracksList() {
        gpsTrackLoader.uploadTracksList();
    }

}