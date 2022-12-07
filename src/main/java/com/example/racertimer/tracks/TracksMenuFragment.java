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

import com.example.racertimer.mainActivity.MainActivity;
import com.example.racertimer.R;
import com.example.racertimer.trackMap.MapManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;

public class TracksMenuFragment extends Fragment {
    private MainActivity mainActivity;
    private TracksDataManager tracksDataManager;
    private MapManager mapManager;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracks_menu, null);
        mainActivity = (MainActivity) getActivity();
        trackInList = new ArrayList<>();
        tracksDataManager = new TracksDataManager(mainActivity, "");
        // todo: убрать ненужные адреса пакетов из всех конструкторов
        mapManager = mainActivity.mapManager;
        gpsTrackLoader = new GPSTrackLoader(mainActivity, mainActivity.getTracksPackage());
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
                mainActivity.undeployTracksMenu();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView trackNameTV = selectedLine.findViewById(R.id.tracks_name);
                String nameToDelete = (String) trackNameTV.getText();
                tracksDataManager.deleteTrackByName(nameToDelete);
                updateTrackList();
                setButtonsVisibility(View.INVISIBLE);
            }
        });

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView trackNameTV = selectedLine.findViewById(R.id.tracks_name);
                String nameOfTheTrack = (String) trackNameTV.getText();

                boolean trackShownOnMap = checkIsTrackOnMap(selectedLine);
                if (trackShownOnMap) {
                    mapManager.hideTrackOnMap(nameOfTheTrack);
                }
                else {
                    GeoTrack geoTrackToBeShown = tracksDataManager.getGeoTrackByName(nameOfTheTrack);
                    if (geoTrackToBeShown != null) mapManager.showSavedGeoTrackOnMap(geoTrackToBeShown);
                    clearAnySelectedLines();
                }
                updateTrackList();
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
            fillNextLine("no saved tracks", "", "");
        } else {
            if (tracksDatabase.isItAnyTracks()) {
                LinkedList<GeoTrack> tracksArray = tracksDatabase.getSavedTracks();
                for (GeoTrack currentTrack: tracksArray) {
                    String trackName = currentTrack.getTrackName();

                    String trackDuration = durationToString(currentTrack.getDuration());

                    String trackDiplayedStatus = "no";
                    if (checkIsTrackDisplayed(trackName)) trackDiplayedStatus = "yes";
                    fillNextLine(trackName, trackDuration, trackDiplayedStatus);
                }
            } else {
                fillNextLine("no saved tracks", "", "");
            }
        }
    }

    private boolean checkIsTrackDisplayed(String trackName) {
        boolean alreadyDisplayed = false;
        ArrayList<String> listDisplayedTracks = mapManager.getAlreadyDisplayedLoadedTracks();
        for (String currentTrackName: listDisplayedTracks) {
            if (trackName.equals(currentTrackName)) {
                alreadyDisplayed = true;
                break;
            }
        }
        return alreadyDisplayed;
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
        fillNextLine("name", "duration", "on map");
    }

    private void fillNextLine(String name, String duration, String displayedStatus) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View currentLine = layoutInflater.inflate(R.layout.tracks_list_line, trackLineToBeFilled, false);
        TextView trackName = currentLine.findViewById(R.id.tracks_name);
        TextView trackDuration = currentLine.findViewById(R.id.track_duration);
        TextView isTrackOnMapTV = currentLine.findViewById(R.id.track_is_displayed);
        trackName.setText(name);
        trackDuration.setText(duration);
        isTrackOnMapTV.setText(displayedStatus);
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
            selectedLine.setBackgroundColor(Color.BLUE);
            setButtonsVisibility(View.VISIBLE);
            if (checkIsTrackOnMap(selectedLine)) btnShow.setText("Hide");
            else btnShow.setText("Show");
        }
    }

    private boolean checkIsTrackOnMap(View currentLine) {
        boolean flag = false;
        TextView displayedOnMap = currentLine.findViewById(R.id.track_is_displayed);
        String displayedMark = (String) displayedOnMap.getText();
        //Log.i("bugfix", "fragment: mark is = "+displayedMark);
        if (displayedMark.equals("yes")) flag = true;
        return flag;
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