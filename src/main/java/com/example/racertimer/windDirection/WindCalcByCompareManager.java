package com.example.racertimer.windDirection;

import android.location.Location;
import android.util.Log;

import com.example.racertimer.Instruments.CoursesCalculator;

import java.util.Arrays;

/** в этом классе рассчитываем истинное направление ветра исходя из расчета разности ВМГ
 * такой калькулятор требует управления и принудительного включения, зато дает более точные результаты
 * ПРИНЦИП ДЕЙСТВИЯ: работает если идем апвинд. Программа запоминает максимальный ВМГ для каждого галса апвинд
 * если уже третий галс подряд получается апвинд, сравниваем максимальные ВМГ обоих галсов и разворачиваем ветер в сторону большего значения
 * пересчет направления ветра работает все время пока идем галсами апвинд. При переходе в даунвинд расчет прекращается
 * для корректной работы требуется исходное значение ветра
 */
public class WindCalcByCompareManager {
    private final static String PROJECT_LOG_TAG = "racer_timer_windCompare";

    private final int EMPTY = 0;
    private final int WIND_DEFAULT = 1; // дефолтное или выставлено вручную или продгруженное вчерашнее
    private final int WIND_ROUGH = 2;   // рассчитанно грубо или загруженное сегодняшнее
    private final int WIND_DYNAMIC = 3;  // текущее, высчитано динамически

    private final int DEAD_RADIUS = 50; // radius to start processing the segment

    private int windStatus = EMPTY;
    private int managerWorkStatus = EMPTY;

    private Tack currentTack, upwindRight, upwindLeft;

    private CompareRoughCalculator compareRoughCalculator;
    private CompareDynamicCalculator compareDynamicCalculator;
    public WindChangeCalculator windChangeCalculator;
    private int windDirection; // направление ветра
    private int lastNumberOfTack = 0; // последний номер галса: 1 - правый бакштаг, 2 - правый бейдевинд, 3 - левый бейдевинд 4 - левый бакштаг
    private WindChangedHerald windChangedHerald; // экземпляр интерфейса для отправки измененного направления
    private CalculatedWindUpdater roughWindUpdater, dynamicWindUpdater;

    private int[] maxTackVMG, tackVMGsBearings;
    private int bearingLastMaxVMG; // курс, при котором зафиксирован максимальный ВМГ
    private int upwindTackChangedCounter = 0; // счетчик неприрывных галсов против ветра

    private static final int TACK_RIGHT_DOWNWIND = 1;
    private static final int TACK_RIGHT_UPWIND = 2;
    private static final int TACK_LEFT_UPWIND = 3;
    private static final int TACK_LEFT_DOWNWIND = 4;


    public WindCalcByCompareManager(int windDirection, WindChangedHerald windChangedHerald) {
        this.windChangedHerald = windChangedHerald;
        // TODO: убрать выставление ветра в конструкторе
        this.windDirection = windDirection;
        maxTackVMG = new int[2]; // обьявляем массив для хранения максимальных данных
        tackVMGsBearings = new int[2];
        windChangeCalculator = new WindChangeCalculator();
        //TODO: вынести в отдельные методы создание интефейса и создание калькуляторов
        roughWindUpdater = new CalculatedWindUpdater() {
            @Override
            public void windIsCalculated(int calculatedWind) {
                updateWindDirectionCalculatedRough(calculatedWind);
            }
        };
        dynamicWindUpdater = new CalculatedWindUpdater() {
            @Override
            public void windIsCalculated(int calculatedWind) {
                updateWindDirectionCalculatedDynamic(calculatedWind);
            }
        };
    }

    public void setWindDirection (int windDirection) {
        this.windDirection = windDirection;
        upwindTackChangedCounter = 0;
        Arrays.fill(maxTackVMG, 0);
        windStatus = WIND_DEFAULT;
    }

    private void updateWindDirectionCalculatedRough(int windDirection) {
        this.windDirection = windDirection;
        windChangedHerald.onWindDirectionChanged(windDirection); // отправляем сообщение с новым значением
        windStatus = WIND_ROUGH;
    }

    private void updateWindDirectionCalculatedDynamic(int windDirection) {
        this.windDirection = windDirection;
        windChangedHerald.onWindDirectionChanged(windDirection); // отправляем сообщение с новым значением
        windStatus = WIND_DYNAMIC;
    }

    public void startCalculating() {
        if (windStatus == WIND_DEFAULT) makeRoughCalculating();
        else makeDynamicCalculating(windDirection);
    }

    public void endCalculating() {
        managerWorkStatus = EMPTY;
    }

    private void makeRoughCalculating() {
        CalculatedWindUpdater roughWindUpdater = new CalculatedWindUpdater() {
            @Override
            public void windIsCalculated(int calculatedWind) {
                updateWindDirectionCalculatedRough(calculatedWind);
            }
        };
        compareRoughCalculator = new CompareRoughCalculator(roughWindUpdater);
        managerWorkStatus = WIND_ROUGH;
    }

    private void makeDynamicCalculating(int windDirection) {
        CalculatedWindUpdater dynamicWindUpdater = new CalculatedWindUpdater() {
            @Override
            public void windIsCalculated(int calculatedWind) {
                updateWindDirectionCalculatedDynamic(calculatedWind);
            }
        };
        compareDynamicCalculator = new CompareDynamicCalculator(dynamicWindUpdater, windDirection);
        managerWorkStatus = WIND_DYNAMIC;
    }

    // TODO: добавить уменьшение максималок через время для

    /** обработка новых послупающих геоданных
     * смотрим, меняется ли галс. Если галс в бейдевинд, для каждого направления находим максимум ВМГ и курс при этом максимуме
     */
    public void onLocationChanged (Location location) { // прием новых данных по геолокации
        int numberOfTack = CoursesCalculator.numberOfTack(windDirection, (int) location.getBearing());
        if (checkTackChangingAndZoneExit(numberOfTack)) onTackChanged(numberOfTack, location);
        else if (isUpwind(numberOfTack))
            windAnalise(location);
    }

    private void windAnalise(Location location) {
        switch (managerWorkStatus) {
            case WIND_DEFAULT: // в недавней смене впервые вышли за радиус
                if (currentTack.isInActiveZone(DEAD_RADIUS)) {
                    currentTack.changeSecondPoint(location);
                    recalculateDirection(upwindLeft, upwindRight);
                    managerWorkStatus = WIND_ROUGH;
                }
                //compareRoughCalculator.onLocationChanged(location);
                break;
            case WIND_ROUGH:
                if (currentTack.updateMaxVmg(location, windDirection)) {
                    recalculateDirection(upwindRight, upwindLeft);
                }
                break;
            default: break;
        }
    }

    private boolean checkTackChangingAndZoneExit(int numberOfTack) {
        boolean tackIsChanged = false;
        if (numberOfTack != lastNumberOfTack && currentTack.isInActiveZone(DEAD_RADIUS)) { // если новый галс не равен прежнему
            Log.i("racer_timer_wind_compare", " tack was changed from "+lastNumberOfTack+" to "+numberOfTack);
            lastNumberOfTack = numberOfTack; // переприсваиваем номер галса
            tackIsChanged = true;
        }
        return tackIsChanged;
    }

    /** обработчик при изменений курса
     * если количество смен курса выше 2, можно считать ветер, при этом уменьшаем на 1 счетчик (для затяжной лавировки)
     * если курс меняется на бакштаг, сбрасываем счетчик и массив максимума ВМГ по курсам
     * если курс меняется на бейдевинд, увеличиваем счетчик
     */
    private void onTackChanged(int numberOfTack, Location location) { // если у нас изменился номер курса
        if (numberOfTack == TACK_LEFT_UPWIND) {
            upwindLeft = new Tack.UpwindLeft(TACK_LEFT_UPWIND, location);
            currentTack = upwindLeft;
            if (upwindRight != null) {
                upwindRight.changeSecondPoint(location);
            }
        }

        if (numberOfTack == TACK_RIGHT_UPWIND) {
            upwindRight = new Tack.UpwindRight(TACK_RIGHT_UPWIND, location);
            currentTack = upwindRight;
            if (upwindLeft != null) {
                upwindLeft.changeSecondPoint(location);
            }
        }

        if (isUpwind(numberOfTack))
            if (upwindRight != null && upwindLeft != null)
                recalculateDirection(upwindRight, upwindLeft);
            else {
                upwindLeft = null;
                upwindRight = null;
            }
    }

    private boolean isUpwind (int numberOfTack) {
        if (numberOfTack == TACK_RIGHT_UPWIND || numberOfTack == TACK_LEFT_UPWIND)
            return true;
        else return false;
    }

    private void recalculateDirection (Tack tackRight, Tack tactLeft) {
        int bearingLeft = tactLeft.getBearing();
        int bearingRight = tackRight.getBearing();

        windDirection = CoursesCalculator.windBetweenTwoUpwinds(bearingLeft, bearingRight);
        windChangedHerald.onWindDirectionChanged(windDirection); // отправляем сообщение с новым значением
    }

//    private void calculateWindDirection () {
//        int updatedWindDirection;
//
//
//        // определяем разницу между углами. если правый бейдевинд больше левого, угол положительный
//        int angleDiffs = CoursesCalculator.diffAngles(tackVMGsBearings[0], tackVMGsBearings[1]);
//
//        // находим соотношение между ВМГ
//        if (maxTackVMG[0] > maxTackVMG[1]) { // если ВМГ в правом бакштаге больше чем в левом
//            // в таком случае напр ветра нужно сместить против часовой (уменьшить)
//            int rateVMGs = maxTackVMG[1] * 100 / maxTackVMG[0]; // соотношение ВМГ меньшего к большему
//            int windDirCorrection = (int) angleDiffs * rateVMGs / 100; // насколько корректируем напр ветра
//            Log.i("racer_timer_wind_compare", " max VMG right = "+maxTackVMG[0]+", max VMG left = "+maxTackVMG[1]);
//            Log.i("racer_timer_wind_compare", " wind correction1: rate = "+rateVMGs+", windDirCirrection = "+windDirCorrection+", wind dir = "+windDirection);
//            updatedWindDirection = tackVMGsBearings[0] + windDirCorrection; // уменьшаем направление ветра
//        } else { // если ВМГ в левом бакштаге больше чем в правом
//            // напр ветра нужно сместить по часовой (увеличить)
//            int rateVMGs = maxTackVMG[0] * 100 / maxTackVMG[1]; // соотношение ВМГ большего к меньшему
//            int windDirCorrection = (int) angleDiffs * rateVMGs / 100; // насколько корректируем напр ветра
//            Log.i("racer_timer_wind_compare", " max VMG right = "+maxTackVMG[0]+", max VMG left = "+maxTackVMG[1]);
//            Log.i("racer_timer_wind_compare", " wind correction2: rate = "+rateVMGs+", windDirCirrection = "+windDirCorrection+", wind dir = "+windDirection);
//            updatedWindDirection = tackVMGsBearings[1] + windDirCorrection; // увеличиваем направление ветра
//        }
//
//        // обрабатываем изменение ветра если оно есть
//        if (updatedWindDirection != windDirection) { // если есть значительные изменения направления ветра
//            Log.i("racer_timer_wind_compare", " wind dir is changed. New = "+updatedWindDirection+", old = "+windDirection);
//            Log.i("racer_timer_wind_compare", " max VMG right = "+maxTackVMG[0]+", bearing = "+tackVMGsBearings[0]+". left = "+maxTackVMG[1]+", bearing = "+tackVMGsBearings[1]);
//            averageMaximumsAndCount();
//            windDirection = updatedWindDirection; // обновляем текущее значение ветра
//            windChangedHerald.onWindDirectionChanged(windDirection); // отправляем сообщение с новым значением
//        }
//    }

//    private void renewCounterByChangedTack() {
//        if (lastNumberOfTack == TACK_LEFT_DOWNWIND || lastNumberOfTack == TACK_RIGHT_DOWNWIND) { // если курс изменился на бакштаг
//            Log.i("racer_timer_wind_compare", "the tack is downwind, setting counter to 0" );
//            upwindTackChangedCounter = 0; // обнуляем счетчик галсов против ветра
//            Arrays.fill(maxTackVMG, 0); // очищаем массив скоростей
//        } else upwindTackChangedCounter++; // если не на бакштаг, считаем количество измененных курсов
//    }

    // подготовка к смене галса опять на апвинд, на случай динамической корректировки ветра при длительном апвинде
    private void averageMaximumsAndCount() {
        upwindTackChangedCounter--; // уменьшили на 1 чтобы на следующую смену опять высчитывать
        int optimumVMG = (int) ((maxTackVMG[0] + maxTackVMG[1]) / 2); // берем среднюю максималку, и
        Arrays.fill (maxTackVMG, optimumVMG); // усредняем обе максималки ВМГ
    }
}

// TODO: - Как будет считаться ветер в случае если он не находится между двумя галсами? (пока косячно)
//       - диалоговое меню с настройкой исходного направления ветра
//       - меню с вариантами выбора методов расчета ветра
//       - запуск расчета ветра вручную либо во время гонки (после окончания таймера)

class WindChangeCalculator {
    int maxVmgRight = 0;
    int maxVmgLeft = 0;
    int leftBearing = 0;
    int rightBearing = 0;

    public void setUpwindStatistics(int maxVmgRight, int maxVmgLeft, int rightBearing, int leftBearing) {
        this.maxVmgRight = maxVmgRight;
        this.maxVmgLeft = maxVmgLeft;
        this.rightBearing = rightBearing;
        this.leftBearing = leftBearing;
    }

    public int calculateCurrentWindDir () {
        int updatedWindDirection;
        // определяем разницу между углами. если правый бейдевинд больше левого, угол положительный
        int angleDiffs = CoursesCalculator.diffAngles(rightBearing, leftBearing);
        if (maxVmgRight > maxVmgLeft) { // если ВМГ в правом бакштаге больше чем в левом
            // в таком случае напр ветра нужно сместить против часовой (уменьшить)
            int rateVMGs = maxVmgLeft * 100 / maxVmgRight; // соотношение ВМГ меньшего к большему
            int windDirCorrection = (int) angleDiffs * rateVMGs / 100; // насколько корректируем напр ветра
            Log.i("racer_timer_wind_compare", " max VMG right = "+maxVmgRight+", max VMG left = "+maxVmgLeft );
            updatedWindDirection = rightBearing + windDirCorrection; // уменьшаем направление ветра
            Log.i("racer_timer_wind_compare", " wind correction1: rate = "+rateVMGs+", windDirCirrection = "+windDirCorrection+", wind dir = "+updatedWindDirection);
        } else { // если ВМГ в левом бакштаге больше чем в правом
            // напр ветра нужно сместить по часовой (увеличить)
            int rateVMGs = maxVmgRight * 100 / maxVmgLeft; // соотношение ВМГ большего к меньшему
            int windDirCorrection = (int) angleDiffs * rateVMGs / 100; // насколько корректируем напр ветра
            Log.i("racer_timer_wind_compare", " max VMG right = "+maxVmgRight+", max VMG left = "+maxVmgLeft);
            updatedWindDirection = leftBearing + windDirCorrection; // увеличиваем направление ветра
            Log.i("racer_timer_wind_compare", " wind correction2: rate = "+rateVMGs+", windDirCirrection = "+windDirCorrection+", wind dir = "+updatedWindDirection);
        }
        return updatedWindDirection;
    }
}

interface CalculatedWindUpdater {
    void windIsCalculated(int calculatedWind);
}
