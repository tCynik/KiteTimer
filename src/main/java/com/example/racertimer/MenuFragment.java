package com.example.racertimer;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.racertimer.forecast.presentation.ActivityForecast;

public class MenuFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private final static String PROJECT_LOG_TAG = "racer_timer";

    private MainActivity mainActivity;
    private Button btnForecast, btnWindCalc, btnResetMax, btnDeveloper;
    private ImageButton btnClose;
    private SwitchCompat switchMuteVMG;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mainActivity = (MainActivity) this.getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        btnClose = view.findViewById(R.id.btn_close_menu);
        btnForecast = view.findViewById(R.id.menu_forecast);
        btnDeveloper = view.findViewById(R.id.developer);
        btnResetMax = view.findViewById(R.id.reset);
        switchMuteVMG = view.findViewById(R.id.switch_mute);

        switchMuteVMG.setOnCheckedChangeListener(this);

        /** обработчики кнопок */
        btnClose.setOnClickListener(new View.OnClickListener() { // кнопка закрытия меню
            @Override
            public void onClick(View view) {
                mainActivity.closeMenu();
            }
        });

        btnForecast.setOnClickListener(new View.OnClickListener() { // кнопка запуска прогноза
            @Override
            public void onClick(View view) {
                runForecast();
            }
        });

        btnDeveloper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.deployDeveloperTools();
                //deployDeveloperTools();
            }
        });

        btnResetMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.resetAllMaximums();
            }
        });

        return view;

    }

    private void runForecast () { // обработка запуска активити прогноза
        mainActivity.closeMenu();

        Location location = mainActivity.getCurrentLocation();
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        Intent intent = new Intent(getActivity(), ActivityForecast.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        startActivity(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        mainActivity.muteChangedStatus(b);
    }

//    public void deployDeveloperTools () { // выгрузка фрагмена меню
//        FragmentManager fragmentManager = activityRace.getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.fr_menu_place, activityRace.menuFragment);
//        fragmentTransaction.commit();
//    }

}