package com.example.racertimer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

public class MenuFragment extends Fragment {

    private final static String PROJECT_LOG_TAG = "racer_timer";

    private ActivityRace activityRace;
    private Context context;
    private Button btnForecast, btnWindCalc, btnResetMax, btnDeveloper;
    private ImageButton btnClose;

    public MenuFragment() {
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
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        btnClose = view.findViewById(R.id.btn_close_menu);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityRace.closeMenu();
            }
        });
        return view;
    }
}