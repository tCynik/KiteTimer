package com.example.racertimer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

/**
 * фрагмент для отображения данных с текущими лавировочными параметрами
 */

public class SailingToolsFragment extends Fragment {
    private final static String PROJECT_LOG_TAG = "racer_timer";

    ConstraintLayout arrowsLayoutCL;
    ImageView arrowVelocityIV;
    TextView velocityTV, bearingTV, windTV, velocityMadeGoodTV, bestDownwindTV, maxVelocityTV, bestUpwindTV, courseToWindTV;

    private int velocity, bearing, windDirection, velocityMadeGood, lastVMG, velocityMax, VMGmax, VMGmin;

    public SailingToolsFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: определяем обьявленные элементы
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sailing_tools, container, false);

    }
}