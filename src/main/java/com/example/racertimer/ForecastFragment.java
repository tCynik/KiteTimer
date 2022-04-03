package com.example.racertimer;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForecastFragment extends Fragment {
    private final static String PROJECT_LOG_TAG = "racer_timer_forecast_fragment";
    private Button btnNewRace;
    private Button btnForecastRunActivity;

    private TextView stopwachTV; // секундометр
    private double latitude, longitude; // координаты для получения прогноза

    public interface OpenerTimerInterface { // интерфейс для вызова другого фрагмента
        public void openTimerFragment();
    }

    OpenerTimerInterface openerTimer;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ForecastFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForecastFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ForecastFragment newInstance(String param1, String param2) {
        Log.i(PROJECT_LOG_TAG, " replacing fragment " );
        ForecastFragment fragment = new ForecastFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            openerTimer = (OpenerTimerInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " cast activity to interface is failed in TimerFragment");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_forecast, null);

        /** кнопка вызова таймера для начала новой гонки */
        btnNewRace = (Button) view.findViewById(R.id.btn_new_race);
        btnNewRace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openerTimer.openTimerFragment();
            }
        });

        /** кнопка запуска activity с прогнозом */
        btnForecastRunActivity = (Button) view.findViewById(R.id.forecast_run_activity);
        btnForecastRunActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (latitude == 0 & longitude ==0) { // если нет координат, запрашиваем их
                    ActivityRace activityRace = (ActivityRace) getActivity();
                    Location location = activityRace.getCurrentLocation();
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                }

                if (latitude == 0 & longitude ==0) { // проверяем, получили ли координаты
                    Toast.makeText(getActivity(), "No location data!", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(getActivity(), ActivityForecast.class);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    startActivity(intent);
                }
            }
        });

        stopwachTV = view.findViewById(R.id.timekeeper);
        stopwachTV.setVisibility(View.INVISIBLE);

        // TODO: мясо для реализации наполнение фрагмента погоды
/////////// добавляем в пакет Instruments класс thread с мясом для отправки и обработки погодных запросов
/////////// при наличии геоданных направляем запрос
/////////// по получении запроса исходя из актуального времени выбираем текущий ветер, выводим в отдельный TV
/////////// добавляем ImageLayout, вставляем туда шкалу ветра, стрелку скорости
/////////// исходя из текущего курса поворачиваем шкалу ветра, из скорости стрелку скорости
/////////// курить по conctrateLayout, порядок прорисовки, наложение, и т.д....


        return view;
    }

    public void setCoordinates (double latitude, double longitude) {
        Log.i(PROJECT_LOG_TAG, "Forecast fragment get new coordinates");
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

// TODO: сделать сериализованную позицию (Крск), и каждый раз подгружать ее?