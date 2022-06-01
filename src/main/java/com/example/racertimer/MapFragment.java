package com.example.racertimer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class MapFragment extends Fragment {
    private final static String PROJECT_LOG_TAG = "racer_timer_map_fragment";

    private ConstraintLayout tracksLayout;
    private ScrollView windowMap;
    private HorizontalScrollView horizontalScroll;

    public ImageView arrowDirection, arrowWind;

    private Button btnIncScale, btnDecScale;

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

        tracksLayout = view.findViewById(R.id.tracks_layout);
        windowMap = view.findViewById(R.id.window_map);
        horizontalScroll = view.findViewById(R.id.horizontal_map_scroll);

        arrowDirection = view.findViewById(R.id.arrow_position_on_map); // стрелка метка курса
        arrowWind = view.findViewById(R.id.wind_direction_arrow_on_map); // стрелка ветра

        btnIncScale = view.findViewById(R.id.btn_inc_scale);
        btnDecScale = view.findViewById(R.id.btn_dec_scale);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        exportViewsIntoSailingTools();
        exportTracksLayoutIntoTrackPainter();
    }

    private void exportViewsIntoSailingTools() {
        ActivityRace activityRace = (ActivityRace) getActivity(); // экземпляр главной активити
        assert activityRace != null;
        activityRace.uploadMapUIIntoTools(tracksLayout, arrowDirection, arrowWind, btnIncScale, btnDecScale);
    }

    private void exportTracksLayoutIntoTrackPainter() {
        ActivityRace activityRace = (ActivityRace) getActivity(); // экземпляр главной активити
        activityRace.uploadTrackLayout(windowMap, horizontalScroll, tracksLayout);
    }

    public ConstraintLayout getTracksLayout() {
        return tracksLayout;
    }
}

// TODO: настроить масштаб так чтобы не выставалять слишком большой размер лайаута вьюшек треков


