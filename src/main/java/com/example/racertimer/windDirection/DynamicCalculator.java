package com.example.racertimer.windDirection;

import android.location.Location;
import android.util.Log;

import com.example.racertimer.Instruments.CoursesCalculator;

public class DynamicCalculator {
    CalculatedWindUpdater calculatedWindUpdater;

    public void onLocationChanged(Location location, int tack) {
        // расчет направления ветра берем ТОЛЬКО ПО БЕЙДЕВИНДУ! Даунвинд не в счет, т.к.
        // волатилен и сильно зависит от внешних факторов
        if (tack == TACK_LEFT_UPWIND || tack == TACK_RIGHT_UPWIND) { // если речь о бейдевинде, анализируем ВМГ на предмет максимума
            int velocity = (int) (location.getSpeed() * 3.6); // в км/ч
            int velocityMadeGood = CoursesCalculator.VMGByWindBearingVelocity(windDirection, bearing, velocity);
            // определяем максималку для данного галса при скорости выше 12 кмч
            if (velocityMadeGood > maxTackVMG[(tack - 2)] & velocity > 12) {
                Log.i("racer_timer_wind_compare", " max VMG on course "+numberOfTack+" changed. Old = "+maxTackVMG[numberOfTack - 2]+", new = "+ velocityMadeGood);
                maxTackVMG[tack - 2] = velocityMadeGood; // обновляем максимум. номера галсов 2 и 3, переводим в 0 и 1
                tackVMGsBearings[numberOfTack - 2] = bearing;
            }
        }

    }

    public void onTackChanged() {}
}
