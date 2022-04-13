package com.example.racertimer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.fragment.app.Fragment;

public class DeveloperFragment extends Fragment {

    private final static String PROJECT_LOG_TAG = "racer_timer";

    private ActivityRace activityRace;

    private SeekBar bearingSB, velocitySB;

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

        bearingSB = view.findViewById(R.id.seekBar_bearing);
        velocitySB = view.findViewById(R.id.seekBar_velosity);

        bearingSB.setOnSeekBarChangeListener(activityRace);
        velocitySB.setOnSeekBarChangeListener(activityRace);

        return view;
    }

//    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//        if (seekBar == bearingSB) {
//            int bearing = i;
//            activityRace.sailingToolsFragment.onBearingChanged(bearing);
//        }
//        if (seekBar == velocitySB) {
//            int velocity = i;
//            activityRace.sailingToolsFragment.onVelocityChanged(velocity);
//        }
//    }

}

// TODO: доработать чтобы бегунки работали, реализовать кнопку выхода в главное меню