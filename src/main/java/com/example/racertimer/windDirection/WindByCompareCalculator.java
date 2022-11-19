package com.example.racertimer.windDirection;

import android.location.Location;
import android.util.Log;

import com.example.racertimer.Instruments.CoursesCalculator;
import com.example.racertimer.Instruments.WindProvider;

/** в этом классе рассчитываем истинное направление ветра исходя из расчета разности ВМГ
 * для корректной работы требуется исходное значение ветра
 */
public class WindByCompareCalculator {
    private final static String PROJECT_LOG_TAG = "racer_timer_windCompare";

    private final int DEAD_RADIUS = 50; // radius to start processing the segment

    private boolean forceCalculating = false;
    private boolean isCalculatorOn = false;

    private TackMoving currentTack, upwindRightTack, upwindLeftTack;

    private int windDirection = 10000; // направление ветра, 10000 = нет данных
    private TackDirection lastTackDirection; // последний номер галса: 1 - правый бакштаг, 2 - правый бейдевинд, 3 - левый бейдевинд 4 - левый бакштаг
    private WindChangedHeraldInterface windChangedHerald; // экземпляр интерфейса для отправки измененного направления

    public WindByCompareCalculator(WindChangedHeraldInterface windChangedHerald, int windDirection) {
        this.windChangedHerald = windChangedHerald;
        this.windDirection = windDirection;
    }

    public void setWindDirection (int windDirection) {
        this.windDirection = windDirection;
    }

    public void setCalculatorStatus(boolean isItOn) {
        isCalculatorOn = isItOn;
        Log.i(PROJECT_LOG_TAG, " Wind calculator changing work status to " +isCalculatorOn);
        // TODO: определиться с логикой запуска/останова обработки
    }

    /** логика обработки новых послупающих геоданных:
     * определяем, является ли галс первым из серии бейдевинда. Если является - расчет ведется заново.
     * каждый галс проверяется на отход от минимального радиуса для исключения ошибки при развороте не оверштаг.
     * Пока отход не зафиксирован, данный галс в расчет не принимается.
     * При выходе очередного галса из радиуса и если зафиксирован противоположный бейдевинд,
     * пересчитывается направление ветра. От текущего галса берутся две точки (первая, и точка выхода из радиуса),
     * для предыдущего галса - лучшее значение, на их основе получаем ветер.
     * При дальнейшем движении этим галсом улучшаем значение угла к ветру по лучшему ВМГ.
     * Каждый пересчет угла для любог бейдевинда - пересчет и обновление данных по ветру.
     * Серия апвинда (и расчет) заканчивается переходом в бакшатг (после выхода из радиуса)
     * При анализе серии галсов апвинд смотрим, меняется ли галс.
     */
    public void onLocationChanged (Location location) { // прием новых данных по геолокации
        if (isCalculatorOn) {
            TackDirection tackDirection = CoursesCalculator.numberOfTack(windDirection, (int) location.getBearing());
            Log.i(PROJECT_LOG_TAG, " got new location to calculate wind. Tack is " +tackDirection);
            if (isUpwind(tackDirection)) {
                if (currentTack == null) { // первый галс из серии бейдевиндов
                    currentTack = firstTackCreateInstances(tackDirection, location);
                }
            }
            if (isDeadZoneEnded(location)){
                if (isUpwind(tackDirection))
                    analyzeFirstTimeSecondPoint(location);
                if (isTackChanged(tackDirection))
                    registerTackChanged(tackDirection, location);
                else if (isUpwind(tackDirection)) {
                    keepGoingUpwindAnalyse(location);
                }
            }
        }
    }

    private void analyzeFirstTimeSecondPoint (Location location) {
        if (!currentTack.isSecondPointExist()) { // первая точка текущего галса
            currentTack.initSecondPoint(location);
            if (upwindLeftTack != null && upwindRightTack != null)
                recalculateDirection(upwindRightTack, upwindLeftTack);
        }
    }

    private TackMoving firstTackCreateInstances(TackDirection tackDirection, Location location) {
        TackMoving tackMoving = null;
        if (tackDirection == TackDirection.UPWIND_LEFT) {
            if (upwindLeftTack == null)
                upwindLeftTack = new TackMoving.UpwindLeft(location);
            // TODO: what's about saving counter-tack?
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
            Log.i(PROJECT_LOG_TAG, " tack was changed from "+ lastTackDirection +" to "+tackDirection);
            return true;
        }
        else return false;
    }

    private boolean isDeadZoneEnded(Location location) {
        if (currentTack == null) return false;
        else
            return currentTack.isInActiveZone(location, DEAD_RADIUS);
    }

    private void keepGoingUpwindAnalyse(Location location) {
        /**
         * варианты действий:
         *                          первый галс \ второй галс
         * второй точки пока нет \ ставим точку \ ставим точку, пересчитываем
         * вторая точка уже есть \ ничего       \ проверяем ВМГ, если обновили максимум, пересчитываем
         */
        if (upwindLeftTack != null && upwindRightTack != null) {
            if (currentTack.checkAndUpdateMaxVmg(location, windDirection))
                recalculateDirection(upwindRightTack, upwindLeftTack);
        }
    }

    private void registerTackChanged(TackDirection tackDirection, Location location) { // если у нас изменился номер курса
        if (!isUpwind(tackDirection)) { // если идем в бакштаг, все сбрасываем, ждем бейдевинда
            upwindLeftTack = null;
            upwindRightTack = null;
            currentTack = null;
        }
        else { // если идем в бейдевинд, считаем
            if (tackDirection == TackDirection.UPWIND_LEFT) {
                upwindLeftTack = new TackMoving.UpwindLeft(location);
                currentTack = upwindLeftTack;
            }
            if (tackDirection == TackDirection.UPWIND_RIGHT) {
                upwindRightTack = new TackMoving.UpwindRight(location);
                currentTack = upwindRightTack;
            }
        }
        lastTackDirection = tackDirection;
    }

    private void recalculateDirection (TackMoving tackRight, TackMoving tactLeft) {
        int bearingLeft = tactLeft.getBestBearing();
        int bearingRight = tackRight.getBestBearing();
        Log.i(PROJECT_LOG_TAG, " recalculating. LeftBearing = "+ bearingLeft +", right one = "+bearingRight);

        windDirection = CoursesCalculator.windBetweenTwoUpwinds(bearingLeft, bearingRight);
        Log.i(PROJECT_LOG_TAG, " calculated wind direction is = "+ windDirection +", sending the broadcast ");
        windChangedHerald.onWindDirectionChanged(windDirection, WindProvider.CALCULATED); // отправляем сообщение с новым значением

        forceCalculating = false;
    }
}

// TODO: - Как будет считаться ветер в случае если он не находится между двумя галсами? (запускаем force)
//       - меню с вариантами выбора методов расчета ветра
//       - запуск расчета ветра вручную либо во время гонки (после окончания таймера)