package com.example.racertimer.tracks;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.racertimer.ActivityRace;
import com.example.racertimer.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;

public class TracksMenuFragment extends Fragment {
    private TracksDataManager tracksDataManager;
    private GPSTrackLoader gpsTrackLoader;
    private TracksDatabase tracksDatabase;

    private LinearLayout trackLineToBeFilled;

    private Button btnDelete, btnShow;
    private ImageButton btnClose;

    private View selectedLine;

    private ArrayList<View> trackInList;

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
        trackInList = new ArrayList<>();
        tracksDataManager = new TracksDataManager((ActivityRace) getActivity(), "");
        gpsTrackLoader = new GPSTrackLoader(activityRace, activityRace.getTracksPackage());
        trackLineToBeFilled = view.findViewById(R.id.tracks_line_to_fill);
        btnDelete = view.findViewById(R.id.button_delete_track);
        btnShow = view.findViewById(R.id.button_show_track);
        btnClose = view.findViewById(R.id.btn_close_trackList);
        setOnClickListeners();
        return view;
    }

    private void setOnClickListeners() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityRace activityRace = (ActivityRace) getActivity();
                activityRace.undeployTracksMenu();
            }
        });
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
        if (tracksDatabase == null) {
            fillNextLine("no saved tracks", "");
        } else {
            if (tracksDatabase.isItAnyTracks()) {
                LinkedList<GeoTrack> tracksArray = tracksDatabase.getSavedTracks();
                for (GeoTrack currentTrack: tracksArray) {
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
            SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
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
        View currentLine = layoutInflater.inflate(R.layout.tracks_list_line, trackLineToBeFilled, false);
        TextView trackName = currentLine.findViewById(R.id.tracks_name);
        TextView trackDuration = currentLine.findViewById(R.id.track_duration);
        trackName.setText(name);
        trackDuration.setText(duration);
        trackInList.add(currentLine);
        currentLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listItemClicked(v);
            }
        });
        trackLineToBeFilled.addView(currentLine);
    }

    private void listItemClicked(View clickedItem) {
        if (clickedItem == selectedLine) {
            clickedItem.setBackgroundColor(Color.WHITE);
            selectedLine = null;
            setButtonsVisibility(View.INVISIBLE);
        }
        else {
            clearAnySelectedLines();
            selectedLine = clickedItem;
            listItemWasSelected(clickedItem);
            setButtonsVisibility(View.VISIBLE);
        }
    }

    private void listItemWasSelected(View currentLine) {
        TextView trackName = currentLine.findViewById(R.id.tracks_name);
        String name = (String) trackName.getText();
        currentLine.setBackgroundColor(Color.BLUE);
    }

    private void clearAnySelectedLines() {
        setButtonsVisibility(View.INVISIBLE);
        for (View currentLine: trackInList) {
            currentLine.setBackgroundColor(Color.WHITE);
        }
    }

    private void setButtonsVisibility(int visibility) {
        btnShow.setVisibility(visibility);
        btnDelete.setVisibility(visibility);
    }
}