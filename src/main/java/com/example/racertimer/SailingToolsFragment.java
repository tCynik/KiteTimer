package com.example.racertimer;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.racertimer.Instruments.CoursesCalculator;
import com.example.racertimer.multimedia.BeepSounds;

/**
 * фрагмент для отображения данных с текущими лавировочными параметрами
 */

public class SailingToolsFragment extends Fragment {
    private final static String PROJECT_LOG_TAG = "racer_timer_sailing_tools";
    BeepSounds voiceover;
    ConstraintLayout arrowsLayoutCL, centralParametersCL, windLayoutCL;
    LayoutInflater windDialogLayoutInflater;
    ImageView arrowVelocityIV, arrowDirectionIV;
    TextView velocityTV, bearingTV, windTV, velocityMadeGoodTV, bestDownwindTV, maxVelocityTV, bestUpwindTV, courseToWindTV;

    private int fullSpeedSize = 0; // максимальный ход стрелки
    private int velocity, bearing, windDirection, velocityMadeGood, lastVMG, maxVelocity, bestUpwind, bestDownwind;
    private double vmgBeeperSensitivity = 0.5; // чувствительность бипера - с какого % от максимального ВМГ начинаем пикать

    private boolean voiceoverIsMuted = false; // переменная отключен ли звук пищалки

    private boolean isRaceStarted  = false;

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

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setSailingToolsFragment(this);

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

        windDialogLayoutInflater = LayoutInflater.from(getActivity());
        View windDialogView = windDialogLayoutInflater.inflate(R.layout.manually_input_wind, null);

        resetAllMaximums(); // выставляем в ноль все вьюшки
        renewWindDirection(202);
        Log.i(PROJECT_LOG_TAG, " fragment tools view was created by onCreateView");

        /** установка направления ветра вручную нажатием на поле "ветер" */

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
        return view;
    }

    public void setVoiceover (BeepSounds voiceover) {
        this.voiceover = voiceover;
    }

    /**
     * блок управляющийх воздействий по новым данным
     */

    public void onVelocityChanged(int valueVelocity) { // новые данные по скорости
        Log.i(PROJECT_LOG_TAG, " velocity in the tools fragment changed. New one = "+ valueVelocity);
        if (velocity != valueVelocity) { // если вновь поступившие цифры отличаются от старых
            renewVelocity(valueVelocity);
            Log.i(PROJECT_LOG_TAG, " got new velocity = "+ valueVelocity+ ", old one = "+velocity);
            if (velocity > maxVelocity) { // обновляем максимум
                renewMaxVelocity(velocity);
            }
            updateArrowPosition(velocity);// перемещаем стрелку
//                updateMaxVelocity(velocity);
            updateVmgByNewWindOrVelocity();// считаем ВМГ -> пищим
        }
    }
    public void onBearingChanged(int valueBearing) { // новые данные по курсу
        Log.i(PROJECT_LOG_TAG, " bearing in the tools fragment changed. New one = "+ valueBearing);
        if (valueBearing != bearing) {// если вновь поступившие цифры отличаются от старых
            renewBearing(valueBearing);
            updateVmgByNewWindOrVelocity();// считаем ВМГ -> пищим
        }
    }
    public void onWindDirectionChanged(int valueWindDirection) { // новые данные по направлению ветра
        Log.i(PROJECT_LOG_TAG, " wind dir in the tools fragment changed. New one = "+ valueWindDirection);
        if (valueWindDirection != windDirection) {
            windTV.setTextColor(Color.WHITE);
            renewWindDirection(valueWindDirection);
            updateVmgByNewWindOrVelocity();
        }
    }

    /**
     * блок управляющийх воздействий по органам управления
     */

    public void resetPressed () { // нажата кнопка сброса максимумов
        resetAllMaximums();
    }

    public void muteChangedStatus (boolean valueMute) { // изменен статус переключателя звука пищалки
        voiceoverIsMuted = valueMute;
        Log.i(PROJECT_LOG_TAG, " voiceover mute setted = " + voiceoverIsMuted);
        if (voiceoverIsMuted) {
            voiceover.voiceoverIsBeingMuted();
            Log.i(PROJECT_LOG_TAG, " voiceover was muted");
        }
        else {
            voiceover.vmgIsMuted = valueMute;
            lastVMG = velocityMadeGood - 1;
            Log.i(PROJECT_LOG_TAG, " voiceover was unmuted, VMG = "+velocityMadeGood+", last VMG = "+lastVMG);
            makeBeeping();
        }
    }

    /**
     * блок методов изменения вьюшек
     */
    private void renewBestUpwind(int value) {
        bestUpwind = value;
        bestUpwindTV.setText(String.valueOf(bestUpwind));
    }
    private void renewBestDownwind(int value) {
        bestDownwind = value;
        bestDownwindTV.setText(String.valueOf(bestDownwind));
    }
    private void renewMaxVelocity(int value) {
        maxVelocity = value;
        maxVelocityTV.setText(String.valueOf(maxVelocity));
        Log.i(PROJECT_LOG_TAG, " new max velocity = "+value);
    }
    private void renewVelocity(int value) {
        velocity = value;
        velocityTV.setText(String.valueOf(velocity));
    }
    private void renewVmg (int value) {
        velocityMadeGood = value;
        velocityMadeGoodTV.setText(String.valueOf(velocityMadeGood));
    }
    private void renewBearing (int value) {
        bearing = value;
        bearingTV.setText(String.valueOf(bearing));
        arrowsLayoutCL.setRotation(bearing);// поворачиваем вьюшку
    }
    private void renewWindDirection (int value) {
        windDirection = value;
        windTV.setText(String.valueOf(windDirection));
        windLayoutCL.setRotation(-1*CoursesCalculator.invertCourse(windDirection));
    }

    /**
     * блок технических методов
     */

    private void resetAllMaximums () { // обнуление всех максимумов
        renewBestUpwind(0);
        renewBestDownwind(0);
        renewMaxVelocity(0);
    }

    private void updateVmgByNewWindOrVelocity() { // обновление максимумов ВМГ
        int courseToWind = CoursesCalculator.calcWindCourseAngle(windDirection, bearing); // находим курс к ветру
        int vmg = CoursesCalculator.VMGByWindBearingVelocity(windDirection, bearing, velocity);
        courseToWindTV.setText(String.valueOf(courseToWind));
        if (velocityMadeGood != vmg) { // если ВМГ изменилось, обновляем поле ВМГ и вьюшку
            velocityMadeGood = vmg;
            renewVmg(velocityMadeGood);
            onVmgUpdated(); // а так же запускаем обработку измененного ВМГ
            makeBeeping();
        }
    }

    private void onVmgUpdated () { // обработка измененного ВМГ
        if (velocityMadeGood > bestUpwind) { // если ВМГ болше максимальной
            renewBestUpwind(velocityMadeGood);
        } else
        if (velocityMadeGood < bestDownwind) { // если ВМГ меньше минимальной
            renewBestDownwind(velocityMadeGood);
        }
    }

    /**
     * блок работы с графикой и звуком
     */
    private void updateArrowPosition (int velocity) { // обновление позиции стрелки скорости
        if (fullSpeedSize == 0) calculateHeometric(); // получаем начальную позицию из размеров вьюшек
        double percentVelocity = 0;
        if (maxVelocity != 0) percentVelocity = 100 - (velocity * 100 / maxVelocity); // находим процент скорости от максимальной
        float position = (float) ( ( percentVelocity * fullSpeedSize ) / 100 + fullSpeedSize/10);
        arrowVelocityIV.setY(position);
    }

    private void calculateHeometric() { // готовимся к отображению динамических вьюшек
        int radiusArrowMin = centralParametersCL.getHeight()/2; // максимальный радиус
        int radiusArrowMax = arrowsLayoutCL.getHeight()/2; // минимальный радиус
        fullSpeedSize = radiusArrowMax - radiusArrowMin; // диапазон, в котором ходит стрелка

        int heightOfArrow = arrowDirectionIV.getHeight(); // высота стрелки направления
        float scaleOfArrow = (float) (fullSpeedSize * 100 / heightOfArrow) / 100; // определем масштаб отображения стрелки
        arrowDirectionIV.setScaleY(scaleOfArrow); // выставляем масштаб
        int shift = (int) ((heightOfArrow * scaleOfArrow) - heightOfArrow); // смещение для компенсации изменения масштаба
        arrowDirectionIV.setY((shift / 2) + 6); // устанавливаем на позицию смещения + 6 для устранения разрыва между вьюшками
        Log.i(PROJECT_LOG_TAG, " fullspeed size = "+fullSpeedSize+", scale = "+ scaleOfArrow);

        arrowDirectionIV.setVisibility(View.VISIBLE);
    }

    private void makeBeeping() {
        int threshold;
        int percent;

        if (velocityMadeGood != 0 & velocityMadeGood != lastVMG & isRaceStarted) { // если изменилась VMG, перезапускаем прищалку
            Log.i(PROJECT_LOG_TAG, " called makeBeeping, voiceoverMute = "+voiceoverIsMuted);
            lastVMG = velocityMadeGood;

            if (velocityMadeGood > 0) { // обрабатываем апвинд
                threshold = (int) (bestUpwind * vmgBeeperSensitivity); // высчитываем порог чувствительности ВМГ
                if (velocityMadeGood > threshold) { // если ВМГ выше порога,
                    percent = calculateBeepingPercent(bestUpwind, threshold); // считаем % от максимульной частоты пиков
                    if (velocity > 5) voiceover.playRepeatSound(percent); // перезапуск пищалки (с автоматической остановкой)
                } else voiceover.stopRepeatSound();

            } else { // обрабатываем даунвинд
                threshold = (int) (bestDownwind * vmgBeeperSensitivity); // высчитываем порог чувствительности ВМГ
                if (velocityMadeGood < threshold) { // если ВМГ меньше порога (больше по модулю, т.к. и то и то минус)
                    percent = calculateBeepingPercent(bestDownwind, threshold); // запускаем/меняем пищалку
                    if (velocity > 5) voiceover.playRepeatSound(percent);
                } else voiceover.stopRepeatSound();
            }
        }
    }

    public void startTheRace() {
        isRaceStarted = true;
    }

    public void stopTheRace () {
        isRaceStarted = false;
        voiceover.stopRepeatSound();
    }

    private int calculateBeepingPercent(int VMGmax, int threshold) {
        int activeVMG = velocityMadeGood - threshold;
        int activeVMGmax = VMGmax - threshold;
        return Math.abs(activeVMG * 100 / activeVMGmax);
    }
}