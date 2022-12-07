package com.example.racertimer;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.racertimer.Instruments.StartingProcedureTimer;
import com.example.racertimer.Instruments.InfoBarStatusUpdater;
import com.example.racertimer.mainActivity.MainActivity;

public class TimerFragment extends Fragment {
    private final static String PROJECT_LOG_TAG = "racer_timer";

    private StartingProcedureTimer startingProcedureTimer;
    private InfoBarStatusUpdater startingTimerStatusUpdater;
    private Button btnInstantStartRace, btn5Minutes, btn3Minutes, btn2Minutes, btn1Minutes;
    private TextView timerResult;

    private boolean timerPaused = true; // флаг: поставлен ли таймер на паузу

    public TimerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, null);

        findTheViews(view);
        setClickListeners();
        /** обработка нажатий и отображения циферблата */

        Toast.makeText(getActivity(), "tap the timer to START", Toast.LENGTH_LONG).show();

        startingTimerStatusUpdater = new InfoBarStatusUpdater() {
            @Override
            public void onTimerStatusUpdated(String timerStatus) {
                timerResult.setText(timerStatus);
                if (timerStatus.equals("00:00")) startRaceCloseTomer();
            }
        };
        startingProcedureTimer = new StartingProcedureTimer((MainActivity) getActivity(), startingTimerStatusUpdater);
        startingProcedureTimer.setTimerPeriod(10 * 60 * 1000);

        return view;
    }

    private void findTheViews(View view) {
        timerResult = view.findViewById(R.id.timer_fragment_tv);
        btnInstantStartRace = view.findViewById(R.id.instant_start_race);
        btn5Minutes = view.findViewById(R.id.set_5min);
        btn3Minutes = view.findViewById(R.id.set_3min);
        btn2Minutes = view.findViewById(R.id.set_2min);
        btn1Minutes = view.findViewById(R.id.set_1min);
    }

    private void setClickListeners() {
        timerResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerPaused) { // если счетчик остановлен,
                    timerPaused = false; // снимамем счетчик с паузы
                    startingProcedureTimer.start();
                    timerResult.setTextColor(Color.WHITE);
                } else { // если таймер идет, ставим на паузу
                    timerPaused = true;
                    startingProcedureTimer.pause();
                    timerResult.setTextColor(Color.RED);
                }
            }
        });

        btnInstantStartRace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRaceCloseTomer();
            }
        });

        /** кнопки корректировки таймера */
        btn5Minutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startingProcedureTimer.setTimerPeriod(5 * 60 * 1000);
            }
        });

        btn3Minutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startingProcedureTimer.setTimerPeriod(3 * 60 * 1000);
            }
        });

        btn2Minutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startingProcedureTimer.setTimerPeriod(2 * 60 * 1000);
            }
        });

        btn1Minutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startingProcedureTimer.setTimerPeriod(1 * 60 * 1000);
            }
        });
    }

    private void startRaceCloseTomer() {
        startingProcedureTimer.stop();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.startTheRace();
    }

    public boolean isTimerRan() {return startingProcedureTimer.isTimerRan();}

    public void stopTheTimer() {
        startingProcedureTimer.stop();
    }
}