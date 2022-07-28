package com.example.racertimer.windDirection;

import android.location.Location;
import android.util.Log;

import com.example.racertimer.Instruments.CoursesCalculator;

/** в этом классе рассчитываем истинное направление ветра исходя из расчета разности ВМГ
 * для корректной работы требуется исходное значение ветра
 */
public class WindCalcByCompareManager {
    private final static String PROJECT_LOG_TAG = "racer_timer_windCompare";

    private final int DEAD_RADIUS = 50; // radius to start processing the segment

    private boolean forceCalculating = false;

    private TackMoving currentTack, upwindRightTack, upwindLeftTack;

    private int windDirection = 1000; // направление ветра, 1000 = нет данных
    private TackDirection lastTackDirection; // последний номер галса: 1 - правый бакштаг, 2 - правый бейдевинд, 3 - левый бейдевинд 4 - левый бакштаг
    private WindChangedHerald windChangedHerald; // экземпляр интерфейса для отправки измененного направления
    private CalculatedWindUpdater roughWindUpdater, dynamicWindUpdater;

    public WindCalcByCompareManager(WindChangedHerald windChangedHerald, int windDirection) {
        this.windChangedHerald = windChangedHerald;
        this.windDirection = windDirection;
    }

    public void setWindDirection (int windDirection) {
        this.windDirection = windDirection;
    }

    public void forceCalculatingWithNoInformation () {
        forceCalculating = true;
        startCalculating();
    }

    public void startCalculating() {
        // TODO: определиться с логикой запуска/останова обработки
    }

    /** логика обработки новых послупающих геоданных:
     * определяем, является ли галс первым из серии бейдевинда. Если является - расчет ведется заново.
     * каждый галс проверяется на отход от минимального радиуса для исключения ошибки при развороте не оверштаг.
     * Пока отход не зафиксирован, данный галс в расчет не принимается.
     * Серия апвинда заканчивается переходом в бакшатг (с выходом из радиуса)
     * При анализе серии галсов апвинд смотрим, меняется ли галс.
     * При выходе очередного галса из радиуса и если зафиксирован противоположный бейдевинд,
     * пересчитывается направление ветра. От текущего галса берутся две точки (первая, и точка выхода из радиуса),
     * для предыдущего галса - лучшее значение, на их основе получаем ветер.
     * При дальнейшем движении этим галсом улучшаем значение угла к ветру по лучшему ВМГ.
     * Каждый пересчет угла для любог бейдевинда - пересчет и обновление данных по ветру.
     */
    public void onLocationChanged (Location location) { // прием новых данных по геолокации
        TackDirection tackDirection = CoursesCalculator.numberOfTack(windDirection, (int) location.getBearing());
        if (isUpwind(tackDirection))
            if (currentTack == null) // первый галс из серии бейдевиндов
                currentTack = firstTackCreateInstances(tackDirection, location);
        if (isDeadZoneEnded(location)){
            if (isTackChanged(tackDirection))
                registerTackChanged(tackDirection, location);
            else if (isUpwind(tackDirection))
                keepGoingUpwindAnalyse(location);
        }
    }

    private TackMoving firstTackCreateInstances(TackDirection tackDirection, Location location) {
        TackMoving tackMoving = null;
        if (tackDirection == TackDirection.UPWIND_LEFT) {
            if (upwindLeftTack == null)
                upwindLeftTack = new TackMoving.UpwindLeft(location);
            tackMoving = upwindLeftTack;
        }
        if (tackDirection == TackDirection.UPWIND_RIGHT) {
            if (upwindRightTack == null)
                upwindRightTack = new TackMoving.UpwindLeft(location);
            tackMoving = upwindRightTack;
        }
        return tackMoving;
    }

    private boolean isUpwind (TackDirection tackDirection) {
        if (forceCalculating) return true;
        else {
            if (tackDirection == TackDirection.UPWIND_LEFT || tackDirection == TackDirection.UPWIND_RIGHT)
                return true;
            else return false;
        }
    }

    private boolean isTackChanged(TackDirection tackDirection){
        if (tackDirection != lastTackDirection) { // если новый галс не равен прежнему
            Log.i("racer_timer_wind_compare", " tack was changed from "+ lastTackDirection +" to "+tackDirection);
            return true;
        }
        else return false;
    }

    private boolean isDeadZoneEnded(Location location) {
        return currentTack.isInActiveZone(location, DEAD_RADIUS);
    }

    private void keepGoingUpwindAnalyse(Location location) {
        if (upwindLeftTack != null && upwindRightTack != null)
            if (currentTack.checkAndUpdateMaxVmg(location, windDirection))
                recalculateDirection(upwindRightTack, upwindLeftTack);
    }

    private void registerTackChanged(TackDirection tackDirection, Location location) { // если у нас изменился номер курса
        if (!isUpwind(tackDirection)) {
            upwindLeftTack = null;
            upwindRightTack = null;
            currentTack = null;
        }
        else {
            if (tackDirection == TackDirection.UPWIND_LEFT) {
                upwindLeftTack = new TackMoving.UpwindLeft(location);
                currentTack = upwindLeftTack;
                if (upwindRightTack != null) {
                    recalculateDirection(upwindRightTack, upwindLeftTack);
                }
            }
            if (tackDirection == TackDirection.UPWIND_RIGHT) {
                upwindRightTack = new TackMoving.UpwindRight(location);
                currentTack = upwindRightTack;
                if (upwindLeftTack != null) {
                    recalculateDirection(upwindRightTack, upwindLeftTack);
                }
            }
        }
        lastTackDirection = tackDirection;
    }

    private void recalculateDirection (TackMoving tackRight, TackMoving tactLeft) {
        int bearingLeft = tactLeft.getBearing();
        int bearingRight = tackRight.getBearing();
        Log.i("racer_timer_wind_compare", " recalculating. LeftBearing = "+ bearingLeft +", right one = "+bearingRight);

        windDirection = CoursesCalculator.windBetweenTwoUpwinds(bearingLeft, bearingRight);
        windChangedHerald.onWindDirectionChanged(windDirection); // отправляем сообщение с новым значением

        forceCalculating = false;
    }
}

// TODO: - Как будет считаться ветер в случае если он не находится между двумя галсами? (запускаем force)
//       - меню с вариантами выбора методов расчета ветра
//       - запуск расчета ветра вручную либо во время гонки (после окончания таймера)

interface CalculatedWindUpdater {
    void windIsCalculated(int calculatedWind);
}
