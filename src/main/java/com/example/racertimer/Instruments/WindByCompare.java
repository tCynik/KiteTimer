package com.example.racertimer.Instruments;

import android.location.Location;

// в этом классе рассчитываем истинное нарпавление ветра исходя из расчета разности ВМГ
// такой калькулятор требует управления и принудительного включения, затодает более точные результаты
public class WindByCompare {
    private final static String PROJECT_LOG_TAG = "racer_timer_windCompare";

    private int windDirection;
    private int lastNumberOfTack = 0; // последний номер галса: 1 - правый бакштаг, 2 - правый бейдевинд, 3 - левый бейдевинд 4 - левый бакштаг
    private WindChangedHerald windChangedHerald; // экземпляр интерфейса для отправки измененного направления

    public WindByCompare (int windDirection, WindChangedHerald windChangedHerald) {
        this.windChangedHerald = windChangedHerald;
        this.windDirection = windDirection;
    }

    public void onLocationChanged (Location location) { // прием новых данных по геолокации
        int bearing = (int) location.getBearing(); // определяем текущий курс
        int numberOfTack = CoursesCalculator.numberOfTack(windDirection, bearing); // определяем номер галса
        if (lastNumberOfTack == 0) lastNumberOfTack = numberOfTack; // если это наш первый курс, принимаем направление
        if (numberOfTack != lastNumberOfTack) { // если изменился номер курса
            //onTackNumberChanged();
        }
    }


}



