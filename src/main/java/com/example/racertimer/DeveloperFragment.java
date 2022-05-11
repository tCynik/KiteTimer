package com.example.racertimer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;

import androidx.fragment.app.Fragment;

public class DeveloperFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private final static String PROJECT_LOG_TAG = "racer_timer";

    private ActivityRace activityRace;

    private SeekBar bearingSB, velocitySB;

    private ImageButton btnBack;
    private Button btnUpdateWind;

    public DeveloperFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        activityRace = (ActivityRace) this.getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_developer, null);

        btnBack = view.findViewById(R.id.btn_close_menu);
        btnUpdateWind = view.findViewById(R.id.btn_update_wind);
        bearingSB = view.findViewById(R.id.seekBar_bearing);
        velocitySB = view.findViewById(R.id.seekBar_velosity);

        bearingSB.setOnSeekBarChangeListener(this);
        velocitySB.setOnSeekBarChangeListener(this);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityRace.deployMenuFragment();
            }
        });

        btnUpdateWind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityRace.updateWindDirection();
            }
        });

        return view;
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar == bearingSB) {
            int bearing = i;
            activityRace.sailingToolsFragment.onBearingChanged(bearing);
            activityRace.mapUIToolsAndTrackLines.onBearingChanged(bearing);
        }
        if (seekBar == velocitySB) {
            int velocity = i;
            activityRace.sailingToolsFragment.onVelocityChanged(velocity);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}

// TODO: доработать чтобы бегунки работали, реализовать кнопку выхода в главное меню