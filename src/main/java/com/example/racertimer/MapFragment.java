package com.example.racertimer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.racertimer.map.MapHorizontalScrollView;
import com.example.racertimer.map.MapScrollView;

public class MapFragment extends Fragment {
    private final static String PROJECT_LOG_TAG = "racer_timer_map_fragment";

    private MainActivity mainActivity;

    private ConstraintLayout tracksLayout;
    private MapScrollView windowMap;
    private MapHorizontalScrollView horizontalScroll;

    public ImageView arrowPosition, arrowWind;

    private Button btnIncScale, btnDecScale, btnMenuTracks;
    private ImageButton btnFixPosition;

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

        arrowPosition = view.findViewById(R.id.arrow_position_on_map); // стрелка метка курса
        arrowWind = view.findViewById(R.id.wind_direction_arrow_on_map); // стрелка ветра

        btnIncScale = view.findViewById(R.id.btn_inc_scale);
        btnDecScale = view.findViewById(R.id.btn_dec_scale);
        btnMenuTracks = view.findViewById(R.id.btn_tracks_menu);

        btnFixPosition = view.findViewById(R.id.btn_fix_position);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity = (MainActivity) getActivity();
        exportViewsIntoSailingTools();
        exportTracksLayoutIntoTrackPainter();
    }

    private void exportViewsIntoSailingTools() {
        assert mainActivity != null;
        mainActivity.uploadMapUIIntoTools(arrowPosition, arrowWind, btnIncScale, btnDecScale, btnFixPosition, btnMenuTracks);
    }

    private void exportTracksLayoutIntoTrackPainter() {
        mainActivity.uploadTrackLayout(windowMap, horizontalScroll, tracksLayout, btnFixPosition, arrowPosition);
    }
}

// TODO: настроить масштаб так чтобы не выставалять слишком большой размер лайаута вьюшек треков


