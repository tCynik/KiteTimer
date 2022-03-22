package com.example.racertimer.Instruments;

import android.location.Location;
import android.util.Log;

import java.util.Arrays;

/** в этом классе рассчитываем истинное направление ветра исходя из расчета разности ВМГ
 * такой калькулятор требует управления и принудительного включения, зато дает более точные результаты
 * ПРИНЦИП ДЕЙСТВИЯ: работает если идем апвинд. Программа запоминает максимальный ВМГ для каждого галса апвинд
 * если уже третий галс подряд получается апвинд, сравниваем максимальные ВМГ обоих галсов и разворачиваем ветер в сторону большего значения
 * пересчет направления ветра работает все время пока идем галсами апвинд. При переходе в даунвинд расчет прекращается
 * для корректной работы требуется исходное значение ветра
 */
public class WindByCompare {
    private final static String PROJECT_LOG_TAG = "racer_timer_windCompare";

    private int windDirection; // направление ветра
    private int lastNumberOfTack = 0; // последний номер галса: 1 - правый бакштаг, 2 - правый бейдевинд, 3 - левый бейдевинд 4 - левый бакштаг
    private WindChangedHerald windChangedHerald; // экземпляр интерфейса для отправки измененного направления

    private int[] maxTackVMG, tackVMGsBearings;
    private int bearingLastMaxVMG; // курс, при котором зафиксирован максимальный ВМГ
    private int upwindTackCounter = 0; // счетчик неприрывных галсов против ветра

    public WindByCompare (int windDirection, WindChangedHerald windChangedHerald) {
        this.windChangedHerald = windChangedHerald;
        this.windDirection = windDirection;
        maxTackVMG = new int[2]; // обьявляем массив для хранения максимальных данных
        tackVMGsBearings = new int[2];
    }

    public void setWindDirection (int windDirection) {
        this.windDirection = windDirection;
    }

    public void onLocationChanged (Location location) { // прием новых данных по геолокации
        int bearing = (int) location.getBearing(); // определяем текущий курс
        int numberOfTack = CoursesCalculator.numberOfTack(windDirection, bearing); // определяем номер галса
        if (lastNumberOfTack == 0) lastNumberOfTack = numberOfTack; // если это наш первый курс, запоминаем его
        if (numberOfTack != lastNumberOfTack) { // обрабатываем изменение галса
            lastNumberOfTack = numberOfTack; // переприсваиваем номер галса
            onTackNumberChanged(); // обратабываем изменение
        } else { // если галс не менялся
            if (numberOfTack == 2 || numberOfTack == 3) { // если речь о бейдевинде
                int velocity = (int) location.getSpeed(); // берем скорость для анализа
                int velocityMadeGood = CoursesCalculator.VMGByWindBearingVelocity(windDirection, bearing, velocity);
                if ( velocityMadeGood > maxTackVMG[(numberOfTack - 2)] ) { // если ВМГ текущая больше максимальной для данного галса
                    Log.i("racer_timer_wind_compare", " max VMG on course "+numberOfTack+" changed. Old = "+maxTackVMG[numberOfTack - 2]+", new = "+ velocityMadeGood);
                    maxTackVMG[numberOfTack - 2] = velocityMadeGood; // обновляем максимум. номера галсов 2 и 3, переводим в 0 и 1
                    tackVMGsBearings[numberOfTack - 2] = bearing;
                    //bearingLastMaxVMG = (int) location.getBearing();
                }
            }
        }
    }

    /** обработчик при изменении укурса
     * если курс меняется на бакштаг, сбрасываем счетчик и массив максимума ВМГ по курсам
     * если курс меняется на бейдевинд, увеличиваем счетчик
     * если количество смен курса выше 2, можно считать ветер, при этом уменьшаем на 1 счетчик (для затяжной лавировки) */
    private void onTackNumberChanged () { // если у нас изменился номер курса
        if (lastNumberOfTack == 1 || lastNumberOfTack == 4) { // если курс изменился на бакштаг
            upwindTackCounter = 0; // обнуляем счетчик галсов против ветра
            Arrays.fill(maxTackVMG, 0); // очищаем массив скоростей
        } else upwindTackCounter ++; // если не бакштаг, считаем количество измененных курсов
        if (upwindTackCounter > 2) { // когда количество курсов достигает трех, считаем ветер
            int updatedWindDirection; // обновленное направление ветра, которое ищем
            // определяем разницу между углами. если правый бейдевинд больше левого, угол положительный
            int angleDiffs = CoursesCalculator.diffAngles(tackVMGsBearings[0], tackVMGsBearings[1]);
            // находим соотношение между ВМГ
            if (maxTackVMG[0] > maxTackVMG[1]) { // если ВМГ в правом бакштаге больше чем в левом
                // в таком случае напр ветра нужно сместить против часовой (уменьшить)
                int rateVMGs = maxTackVMG[0] * 100 / maxTackVMG[1]; // соотношение ВМГ меньшего к большему
                int windDirCorrection = (int) angleDiffs * rateVMGs / 100; // насколько корректируем напр ветра
                updatedWindDirection = windDirection - windDirCorrection; // уменьшаем направление ветра
            } else { // если ВМГ в левом бакштаге больше чем в правом
                // напр ветра нужно сместить по часовой (увеличить)
                int rateVMGs = maxTackVMG[1] * 100 / maxTackVMG[0]; // соотношение ВМГ большего к меньшему
                int windDirCorrection = (int) angleDiffs * rateVMGs / 100; // насколько корректируем напр ветра
                updatedWindDirection = windDirection + windDirCorrection; // увеличиваем направление ветра
            }
            readyToNextTack();
            if (updatedWindDirection != windDirection) { // если есть значительные изменения направления ветра
                Log.i("racer_timer_wind_compare", " wind dir is changed. New = "+windDirection+", old = "+updatedWindDirection);
                windDirection = updatedWindDirection; // обновляем текущее значение ветра
                windChangedHerald.onWindDirectionChanged(windDirection); // отправляем сообщение с новым значением
            }
        }
    }

    // подготовка к смене галса опять на апвинд, на случай динамической корректировки ветра при длительном апвинде
    private void readyToNextTack () {
        upwindTackCounter --; // уменьшили на 1 чтобы на следующую смену опять высчитывать
        int optimumVMG = (int) ((maxTackVMG[0] + maxTackVMG[1]) / 2); // берем среднюю максималку, и
        Arrays.fill (maxTackVMG, optimumVMG); // усредняем обе максималки ВМГ
    }
}



