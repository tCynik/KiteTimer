package com.example.racertimer.sailingToolsFragment;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.racertimer.Instruments.CoursesCalculator;
import com.example.racertimer.Instruments.WindProvider;
import com.example.racertimer.LocationHeraldInterface;
import com.example.racertimer.mainActivity.MainActivity;
import com.example.racertimer.R;
import com.example.racertimer.StatusUiUpdater;

/**
 * фрагмент для отображения данных с текущими лавировочными параметрами
 */

public class SailingToolsFragment extends Fragment {
    private final static String PROJECT_LOG_TAG = "racer_timer_sailing_tools";
    private final String MODULE_NAME = "sailing_tools";
    private ViewModel viewModel;

    VmgBeeper beeperVMG;
    //BeepSounds voiceover;
    ConstraintLayout arrowsLayoutCL, centralParametersCL, windLayoutCL;
    ImageView arrowVelocityIV, arrowDirectionIV;
    TextView velocityTV, bearingTV, windTV, velocityMadeGoodTV, bestDownwindTV, maxVelocityTV, bestUpwindTV, courseToWindTV;

    private int fullSpeedSize = 0; // максимальный ход стрелки
    private int velocity, windDirection;
    private double vmgBeeperSensitivity = 0.5; // чувствительность бипера - с какого % от максимального ВМГ начинаем пикать

    private boolean voiceoverIsMuted = false; // переменная отключен ли звук пищалки

    private boolean isRaceStarted  = false;

    private MainActivity mainActivity;

    private StatusUiUpdater statusUiUpdater;
    private LocationHeraldInterface locationHerald;

    public SailingToolsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sailing_tools, null); // инфлейтим вьюшку фрагмента

        findViews(view);

        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        viewModel.onWindChanged(windDirection);

        initManualWindSetting();
        passInstanceToMain();
        arrowsLayoutCL.setRotation(135);// поворачиваем вьюшку

        initLocationHerald();


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        calculateArrowHeometricFromViewses(); // добавил еще один вызов при создании

    }

    private void findViews(View view) {
        arrowsLayoutCL = view.findViewById(R.id.arrows_layout); // вьюшка для стрелок скорости
        centralParametersCL = view.findViewById(R.id.central_params_cl); // вьюшка для ограничения движения стрелок
        windLayoutCL = view.findViewById(R.id.wind_layout);
        arrowVelocityIV = view.findViewById(R.id.arrow); // стрелка скорости
        arrowDirectionIV = view.findViewById(R.id.arrow_direction); // стрелка направления
        velocityTV = view.findViewById(R.id.velocity);
        bearingTV = view.findViewById(R.id.bearing);
        windTV = view.findViewById(R.id.wind);
        velocityMadeGoodTV = view.findViewById(R.id.vmg);
        bestDownwindTV = view.findViewById(R.id.best_downwind);
        maxVelocityTV = view.findViewById(R.id.max_velocity);
        bestUpwindTV = view.findViewById(R.id.best_upwind);
        courseToWindTV = view.findViewById(R.id.course_to_wind);
    }

    private void initObservers() {
        viewModel.getSpeedLive().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer value) {
                velocity = value;
                velocityTV.setText(String.valueOf(velocity));
                beeperVMG.updateVelocity(value);
            }
        });

        viewModel.getMaxSpeedLive().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer value) {
                maxVelocityTV.setText(value.toString());
            }
        });

        viewModel.getBearingLive().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer value) {
                bearingTV.setText(String.valueOf(value));
                arrowsLayoutCL.setRotation(value);
            }
        });

        viewModel.getVMGLive().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer value) {
                velocityMadeGoodTV.setText(value.toString());
                beeperVMG.updateVMG(value);
            }
        });

        viewModel.getMaxUpwindLive().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer value) {
                bestUpwindTV.setText(value.toString());
                beeperVMG.updateBestUpwind(value);
            }
        });

        viewModel.getMaxDownwindLive().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer value) {
                bestDownwindTV.setText(value.toString());
                beeperVMG.updateBestDownwind(value);
            }
        });

        viewModel.getCourseToWindLive().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer value) {
                courseToWindTV.setText(value.toString());
            }
        });

        viewModel.getWindDirectionLive().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer value) {
                windTV.setText(value.toString());
                windLayoutCL.setRotation(-1*CoursesCalculator.invertCourse(value));
            }
        });

        viewModel.getPercentVelocityLive().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer value) {
                Log.i("bugfix: sailingToolsFragment", " percent velocity observer has new value");
                updateArrowPositionByPercent(value);
            }
        });
    }

    private void initManualWindSetting() {
        windTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.manuallyWindManager();
                    Log.i(PROJECT_LOG_TAG, " windManager in fragment pressed ");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void passInstanceToMain(){
        if (mainActivity == null) {
            mainActivity = (MainActivity) getActivity();
            mainActivity.setSailingToolsFragment(this);
        }
    }

    public void setStatusUiUpdater(StatusUiUpdater statusUiUpdater) {
        this.statusUiUpdater = statusUiUpdater;
    }

    public LocationHeraldInterface getContentUpdater() {
        return locationHerald;
    }

    private void initLocationHerald() {
        locationHerald = new LocationHeraldInterface() {
            @Override
            public void onLocationChanged(Location location) {
                int bearing = (int) location.getBearing();
                int velocity = 0;
                if (location.hasSpeed()) {
                    velocity = (int) location.getSpeed();
                }
                viewModel.onLocationChanged(velocity, bearing);
            }

            @Override
            public void onWindDirectionChanged(int windDirection, WindProvider provider) {
                SailingToolsFragment.this.onWindDirectionChanged(windDirection, provider);
                viewModel.onWindChanged(windDirection);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        beeperVMG = new VmgBeeper(mainActivity);
        initObservers();

        statusUiUpdater.updateUIModuleStatus(MODULE_NAME, true);
    }

    @Override
    public void onPause() {
        super.onPause();
        statusUiUpdater.updateUIModuleStatus(MODULE_NAME, false);
    }

    /**
     * блок управляющийх воздействий по новым данным
     */

    public void onWindDirectionChanged(int valueWindDirection, WindProvider provider) { // новые данные по направлению ветра
        Log.i(PROJECT_LOG_TAG, " wind dir in the tools fragment changed. New one = "+ valueWindDirection+
                " provider = " + provider);
        if (valueWindDirection != windDirection) {
            windDirection = valueWindDirection;
            viewModel.onWindChanged(valueWindDirection);

            switch (provider) {
                case DEFAULT:
                    windTV.setTextColor(Color.RED);
                    break;
                case HISTORY:
                    windTV.setTextColor(Color.RED);
                    break;
                case FORECAST:
                    windTV.setTextColor(Color.GREEN);
                    break;
                case CALCULATED:
                    windTV.setTextColor(Color.WHITE);
                    break;
                case MANUAL:
                    windTV.setTextColor(Color.BLUE);
                    break;
            }
        }
    }

    /**
     * блок управляющийх воздействий по органам управления
     */

    public void resetPressed () { // нажата кнопка сброса максимумов
        viewModel.resetMaximums();
    }

    public void muteChangedStatus (boolean valueMute) { // изменен статус переключателя звука пищалки
        beeperVMG.setMuteStatus(valueMute);
    }

    /**
     * блок работы с графикой и звуком
     */

    private void updateArrowPositionByPercent (int percent) { // обновление позиции стрелки скорости
        if (fullSpeedSize == 0) {
            Log.i("bugfix: sailingToolsFragment", " calculating arrow sizes ");
            calculateArrowHeometricFromViewses(); // получаем начальную позицию из размеров вьюшек
        }
        float position = (float) ( ( percent * fullSpeedSize )/100); //+ centralParametersCL.getHeight());
        arrowVelocityIV.setY(fullSpeedSize - position);
    }

    private void calculateArrowHeometricFromViewses() { // готовимся к отображению динамических вьюшек
        Log.i("bugfix: sailingToolsFragment", " starting calculate, high = "+ centralParametersCL.getHeight()+", arrow visibility = "+arrowDirectionIV.getVisibility());
        Log.i("bugfix: sailingToolsFragment", " height of arrow = "+arrowDirectionIV.getHeight()+", width = "+ arrowDirectionIV.getWidth());
        Log.i("bugfix: sailingToolsFragment", " position of arrow: X = "+arrowDirectionIV.getX()+", Y = "+ arrowDirectionIV.getY());
        arrowDirectionIV.setVisibility(View.VISIBLE);
        RetryTimer retryTimer = new RetryTimer(new RetryTimer.EndingTimerInterface() {
            @Override
            public void onTimerEnded() {
                calculateArrowHeometricFromViewses();
                Log.i("bugfix: sailingToolsFragment", " timeIsEnded, re-trying to calculate ");
            }
        });

        if (centralParametersCL.getHeight() != 0 && arrowDirectionIV.getHeight()!=0) {
            Log.i("bugfix: sailingToolsFragment", " going to perform calculate ");
            performCalculate();
        } else {
            retryTimer.execute();
            Log.i("bugfix: sailingToolsFragment", " view sizes = 0, calculation dropped ");
        }
    }

    private void performCalculate() {
        Log.i("bugfix: sailingToolsFragment", " performing calculate parameter = "+centralParametersCL.getHeight());
        int radiusArrowMin = centralParametersCL.getHeight()/2; // максимальный радиус
        int radiusArrowMax = arrowsLayoutCL.getHeight()/2; // минимальный радиус
        fullSpeedSize = radiusArrowMax - radiusArrowMin; // диапазон, в котором ходит стрелка

        int heightOfArrow = arrowDirectionIV.getHeight(); // высота стрелки направления
        Log.i("bugfix: sailingToolsFragment", " height of arrow = "+arrowDirectionIV.getHeight()+", width = "+ arrowDirectionIV.getWidth());
        float scaleOfArrow = 1; // todo: to prevent DevideByZeroException. value 1 can be in correct!
        if (heightOfArrow!=0) scaleOfArrow = (float) (fullSpeedSize * 100 / heightOfArrow) / 100; // определем масштаб отображения стрелки
        arrowDirectionIV.setScaleY(scaleOfArrow); // выставляем масштаб
        int shift = (int) ((heightOfArrow * scaleOfArrow) - heightOfArrow); // смещение для компенсации изменения масштаба
        arrowDirectionIV.setY((shift / 2) + 6); // устанавливаем на позицию смещения + 6 для устранения разрыва между вьюшками
        Log.i(PROJECT_LOG_TAG, " fullspeed size = "+fullSpeedSize+", scale = "+ scaleOfArrow+", y="+(shift / 2) + 6);
        Log.i("bugfix: sailingToolsFragment", " fullspeed size = "+fullSpeedSize+", scale = "+ scaleOfArrow+", y="+(shift / 2) + 6);

        Log.i("bugfix: sailingToolsFragment", " arrow visibility1 = "+arrowDirectionIV.getVisibility());
        arrowDirectionIV.setVisibility(View.VISIBLE);
        Log.i("bugfix: sailingToolsFragment", " arrow visibility2 = "+arrowDirectionIV.getVisibility());
    }

    public void startTheRace() {
        isRaceStarted = true;
        beeperVMG.setRaceStatus(true);
    }

    public void stopTheRace () {
        isRaceStarted = false;
        beeperVMG.setRaceStatus(false);
    }
}