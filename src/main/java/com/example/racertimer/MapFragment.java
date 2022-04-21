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

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class MapFragment extends Fragment {
    private final static String PROJECT_LOG_TAG = "racer_timer_forecast_fragment";

    private MapFragment context;
    private View view;

    private ConstraintLayout tracksLayout;
    public ImageView arrowDirection, arrowWind;

    private Button btnIncScale, btnDecScale;

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
        context = this;

        tracksLayout = view.findViewById(R.id.tracks_layout);

        arrowDirection = view.findViewById(R.id.arrow_position_on_map); // стрелка метка курса
        arrowWind = view.findViewById(R.id.wind_direction_arrow_on_map); // стрелка ветра

        btnIncScale = view.findViewById(R.id.btn_inc_scale);
        btnDecScale = view.findViewById(R.id.btn_dec_scale);

        exportViewsIntoTools();

        return view;
    }

    private void exportViewsIntoTools () {
        ActivityRace activityRace = (ActivityRace) getActivity(); // экземпляр главной активити
        assert activityRace != null;
        activityRace.uploadMapUIIntoTools(tracksLayout, arrowDirection, arrowWind, btnIncScale, btnDecScale);
    }

    /** Публичные методы для связи с внешним миром */
    public void locationIsChanged (Location location) {
        this.currentLocation = location;

    }

    public void setCoordinates (double latitude, double longitude) {
        Log.i(PROJECT_LOG_TAG, "Forecast fragment get new coordinates");
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

