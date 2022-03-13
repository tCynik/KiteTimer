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
import com.example.racertimer.multimedia.Voiceover;

/**
 * фрагмент для отображения данных с текущими лавировочными параметрами
 */

public class SailingToolsFragment extends Fragment {
    private final static String PROJECT_LOG_TAG = "racer_timer";
    private boolean viewIsCreated = false;
    Voiceover voiceover;
    ConstraintLayout arrowsLayoutCL, centralParametersCL, windLayoutCL;
    ImageView arrowVelocityIV;
    TextView velocityTV, bearingTV, windTV, velocityMadeGoodTV, bestDownwindTV, maxVelocityTV, bestUpwindTV, courseToWindTV;

    private int radiusArrowMax; // нулевой радиус положения стрелки, откуда ведем отсчет
    private int radiusArrowMin; // максимальный радиус, на котором может находиться стрелка
    private int velocity, bearing, windDirection, velocityMadeGood, lastVMG, maxVelocity, bestUpwind, bestDownwind;

    private boolean voiceoverIsMuted = false; // переменная отключен ли звук пищалки

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
        View view = inflater.inflate(R.layout.fragment_sailing_tools, null); // инфлейтим вьюшку фрагмента

        arrowsLayoutCL = view.findViewById(R.id.arrows_layout); // вьюшка для стрелок скорости
        centralParametersCL = view.findViewById(R.id.central_params_cl); // вьюшка для ограничения движения стрелок
        windLayoutCL = view.findViewById(R.id.wind_layout);
        arrowVelocityIV = view.findViewById(R.id.arrow); // стрелка скорости
        velocityTV = view.findViewById(R.id.velocity);
        bearingTV = view.findViewById(R.id.bearing);
        windTV = view.findViewById(R.id.wind);
        velocityMadeGoodTV = view.findViewById(R.id.vmg);
        bestDownwindTV = view.findViewById(R.id.best_downwind);
        maxVelocityTV = view.findViewById(R.id.max_velocity);
        bestUpwindTV = view.findViewById(R.id.best_upwind);
        courseToWindTV = view.findViewById(R.id.course_to_wind);

        //resetAllMaximums(); // выставляем в ноль все вьюшки
        renewWindDirection(202);
        //calculateHeometric(); // рассчитываем значения для перемещения стрелок
        Log.i("racer_timer_tools_fragment", " fragment view was created ");
        viewIsCreated = true; // разрешаем изменение вьюшек
        return view;
    }

    public void setVoiceover (Voiceover voiceover) {
        this.voiceover = voiceover;
    }

    /**
     * блок управляющийх воздействий по новым данным
     */

    public void onVelocityChanged(int valueVelocity) { // новые данные по скорости
        if (viewIsCreated) {
            Log.i("racer_timer_tools_fragment", " velocity in the tools fragment changed. New one = "+ valueVelocity);
            if (velocity != valueVelocity) { // если вновь поступившие цифры отличаются от старых
                renewVelocity(valueVelocity);
                if (velocity > maxVelocity) { // обновляем максимум
                    maxVelocity = velocity;
                    renewMaxVelocity(maxVelocity);
                }
                updateArrowPosition(velocity);// перемещаем стрелку
                updateVmgByNewWindOrVelocity();// считаем ВМГ -> пищим
            }
        }
    }
    public void onBearingChanged(int valueBearing) { // новые данные по курсу
        if (viewIsCreated) {
            Log.i("racer_timer_tools_fragment", " bearing in the tools fragment changed. New one = "+ valueBearing);
            if (valueBearing != bearing) {// если вновь поступившие цифры отличаются от старых
                renewBearing(valueBearing);
                updateVmgByNewWindOrVelocity();// считаем ВМГ -> пищим
            }
        }
    }
    public void onWindDirectionChanged(int valueWindDirection) { // новые данные по направлению ветра
        if (viewIsCreated) {
            Log.i("racer_timer_tools_fragment", " wind dir in the tools fragment changed. New one = "+ valueWindDirection);
            if (valueWindDirection != windDirection) {
                windTV.setTextColor(Color.WHITE);
                renewWindDirection(valueWindDirection);
                updateVmgByNewWindOrVelocity();
                // TODO: корректировка ВМГ при обновлении ветра
            }
        }
        // обновляем цифры
        // поворачиваем компас
        // поворачиваем стрелки
        // считаем ВМГ - > пищим
    }

    /**
     * блок управляющийх воздействий по органам управления
     */

    public void resetPressed () { // нажата кнопка сброса максимумов
        if (viewIsCreated) resetAllMaximums();
    }

    public void muteChangedStatus (boolean valueMute) { // изменен статус переключателя звука пищалки
        voiceoverIsMuted = valueMute;
        if (voiceoverIsMuted) voiceover.voiceoverIsBeingMuted();
        else {
            voiceover.vmgIsMuted = true;
            // пикаем
        }
        // обновляем переменную
        // пищим/не пищим
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
        double courseToWindRadians; // обьявляем переменную для расчета курсов в радианах
        int courseToWind = CoursesCalculator.calcWindCourseAngle(windDirection, bearing); // находим курс к ветру
        int vmg = velocityMadeGood;
        if (Math.abs(courseToWind) < 90) { // если курс острый, значит считаем апвинд
            courseToWindRadians = Math.toRadians( Math.abs(courseToWind) ); // считаем курс в радианах
            vmg = (int)( Math.cos(courseToWindRadians)*velocity); // считаем ВМГ upwind -> положительный
        } else { // если курс тупой, считаем даунвинд
            courseToWindRadians = Math.toRadians( (Math.abs(courseToWind) - 90) ); // считаем курс в радианах
            vmg = (int)( -1 * Math.cos(courseToWindRadians)*velocity); // считаем ВМГ downwind -> отрицательный
        }
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
        int fullSpeed = radiusArrowMax - radiusArrowMin;
        double percentVelocity = 0;
        if (maxVelocity != 0) percentVelocity = maxVelocity / maxVelocity;
        float position = (float) ( radiusArrowMin + (percentVelocity * fullSpeed) );
        arrowVelocityIV.setY(position);
    }

    private void calculateHeometric() { // при создании вьюшки определяемся с размерами окон для последующих расчетов
        radiusArrowMax = centralParametersCL.getHeight()/2; // максимальный радиус
        radiusArrowMin = arrowsLayoutCL.getHeight()/2; // минимальный радиус
    }

    private void makeBeeping() {

    }


}