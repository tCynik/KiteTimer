package com.example.racertimer;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class MapFragment extends Fragment {
    private final static String PROJECT_LOG_TAG = "racer_timer_map_fragment";

    //private Context context;
    private Context context;
    private View view;

    private ConstraintLayout tracksLayout;
    private LinearLayout trackLinear;
    public ImageView arrowDirection, arrowWind;

    private Button btnIncScale, btnDecScale;
    Button btnStartRecordTrack;

    private double latitude, longitude; // координаты для получения прогноза
    private Location currentLocation;


    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, null);
        this.view = view;
        context = getActivity();

        tracksLayout = view.findViewById(R.id.tracks_layout);

        arrowDirection = view.findViewById(R.id.arrow_position_on_map); // стрелка метка курса
        arrowWind = view.findViewById(R.id.wind_direction_arrow_on_map); // стрелка ветра

        btnIncScale = view.findViewById(R.id.btn_inc_scale);
        btnDecScale = view.findViewById(R.id.btn_dec_scale);

//        btnStartRecordTrack = view.findViewById(R.id.button_start);
//        btnStartRecordTrack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (trackPainterOnMap != null) trackPainterOnMap.beginNewTrackDrawing();
//            }
//        });

        //initTrackPainter();
        //trackPainterOnMap = new TrackPainterOnMap(trackDrawerTranzister, context);
//        trackDrawerTranzister = new TrackDrawerTranzister() {
//            @Override
//            public void setDrawView(DrawView drawView) {
//                trackDrawerView = drawView;
//                Log.i("racer_timer_painter", "!!!!!!transiting the drawView by callback");
//            }
//        };
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        exportViewsIntoTools();
        //initTrackPainter();

        //trackViewInflating();
    }

    private void exportViewsIntoTools () {
        ActivityRace activityRace = (ActivityRace) getActivity(); // экземпляр главной активити
        assert activityRace != null;
        activityRace.uploadMapUIIntoTools(tracksLayout, arrowDirection, arrowWind, btnIncScale, btnDecScale);
    }

    /** Публичные методы для связи с внешним миром */
//    public void locationIsChanged (Location location) {
//        this.currentLocation = location;
//        Log.i("racer_timer_painter", "renew location for the track painter");
//
//        if (trackPainterOnMap != null) {
//            trackPainterOnMap.onLocatoinChanged(location);
//            Log.i("racer_timer_painter", "sending the location into track pointer");
//        } else Log.i("racer_timer_painter", "track painter is null");
//    }

    public void setCoordinates (double latitude, double longitude) {
        Log.i(PROJECT_LOG_TAG, "Forecast fragment get new coordinates");
        this.latitude = latitude;
        this.longitude = longitude;
    }

//    private void initTrackPainter() {
//        Log.i("racer_timer_painter", "initing trackPainter");
//        trackPainterOnMap = new TrackPainterOnMap(trackDrawerTranzister, context);
//        //trackPainterOnMap.setTrackDrawerTranzister(trackDrawerTranzister);
//
//        //trackDrawerView = trackPainterOnMap.getDrawView();
//        //if (trackDrawerView == null) Log.i(PROJECT_LOG_TAG, "track painter view is null!");
//        // TODO: получается наловый экземпляр trackDrawer'а. надо разобраться
//    }

//    public void setTrackPainterOnMap(TrackPainterOnMap trackPainterOnMap) {
//        this.trackPainterOnMap = trackPainterOnMap;
//    }

//    private void trackViewInflating() {
//        Log.i("racer_timer_painter", "!!!!!!!!!!!!!!!!!!!!!!!!!");
//        if (trackDrawerView == null) Log.i(PROJECT_LOG_TAG, "!!!!trackDrawerView is NULL");
//        if (trackDrawerView != null) Log.i(PROJECT_LOG_TAG, "!!!!drawView was maked and not null!");
//        trackDrawerView = trackPainterOnMap.getDrawView();
//        if (trackDrawerView == null) Log.i(PROJECT_LOG_TAG, "!!!!trackDrawerView is NULL");
//        if (trackDrawerView != null) Log.i(PROJECT_LOG_TAG, "!!!!drawView was maked and not null!");
//
//        //tracksLayout.addView(trackDrawerView);
//    }
}


