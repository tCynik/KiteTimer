package com.example.racertimer.windDirection;

import android.location.Location;
import android.util.Log;

import com.example.racertimer.Instruments.CoursesCalculator;

public class CompareDynamicCalculator extends WindByCompareCalculator{
    CalculatedWindUpdater calculatedWindUpdater;
    private static final int TACK_RIGHT_DOWNWIND = 1;
    private static final int TACK_RIGHT_UPWIND = 2;
    private static final int TACK_LEFT_UPWIND = 3;
    private static final int TACK_LEFT_DOWNWIND = 4;

    private int[] maxTackVMG, tackVMGsBearings;
    private int windDirection; // направление ветра


    public CompareDynamicCalculator(CalculatedWindUpdater calculatedWindUpdater, int windDirection) {
        super(calculatedWindUpdater);
        this.windDirection = windDirection;
    }

    public void onLocationChanged(Location location, int numberOfTack) {
        // расчет направления ветра берем ТОЛЬКО ПО БЕЙДЕВИНДУ! Даунвинд не в счет, т.к.
        // волатилен и сильно зависит от внешних факторов
        if (numberOfTack == TACK_LEFT_UPWIND || numberOfTack == TACK_RIGHT_UPWIND) { // если речь о бейдевинде, анализируем ВМГ на предмет максимума
            int velocity = (int) (location.getSpeed() * 3.6); // в км/ч
            int bearing = (int) location.getBearing();
            int velocityMadeGood = CoursesCalculator.VMGByWindBearingVelocity(windDirection, bearing, velocity);
            // определяем максималку для данного галса при скорости выше 12 кмч
            if (velocityMadeGood > maxTackVMG[(numberOfTack - 2)] & velocity > 12) {
                Log.i("racer_timer_wind_compare", " max VMG on course " + numberOfTack + " changed. Old = " + maxTackVMG[numberOfTack - 2] + ", new = " + velocityMadeGood);
                maxTackVMG[numberOfTack - 2] = velocityMadeGood; // обновляем максимум. номера галсов 2 и 3, переводим в 0 и 1
                tackVMGsBearings[numberOfTack - 2] = bearing;
            }
        }
    }

    public void newForceWindDirection (int windDirection) {
        this.windDirection = windDirection;
    }

    public void onTackChanged() {}
}
