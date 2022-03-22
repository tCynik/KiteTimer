package com.example.racertimer.Instruments;

import android.location.Location;

import java.util.Arrays;

// в этом классе рассчитываем истинное нарпавление ветра исходя из расчета разности ВМГ
// такой калькулятор требует управления и принудительного включения, затодает более точные результаты
public class WindByCompare {
    private final static String PROJECT_LOG_TAG = "racer_timer_windCompare";

    private int windDirection; // направление ветра
    private int lastNumberOfTack = 0; // последний номер галса: 1 - правый бакштаг, 2 - правый бейдевинд, 3 - левый бейдевинд 4 - левый бакштаг
    private WindChangedHerald windChangedHerald; // экземпляр интерфейса для отправки измененного направления

    private int[] maxTackVelocity, tackVMGBearing;
    private int bearingLastMaxVMG; // курс, при котором зафиксирован максимальный ВМГ
    private int upwindTackCounter = 0; // счетчик неприрывных галсов против ветра

    public WindByCompare (int windDirection, WindChangedHerald windChangedHerald) {
        this.windChangedHerald = windChangedHerald;
        this.windDirection = windDirection;
        maxTackVelocity = new int[2]; // обьявляем массив для хранения максимальных данных
        tackVMGBearing = new int[2];
    }

    public void setWindDirection (int windDirection) {
        this.windDirection = windDirection;
    }

    public void onLocationChanged (Location location) { // прием новых данных по геолокации
        int bearing = (int) location.getBearing(); // определяем текущий курс
        int numberOfTack = CoursesCalculator.numberOfTack(windDirection, bearing); // определяем номер галса
        if (lastNumberOfTack == 0) lastNumberOfTack = numberOfTack; // если это наш первый курс, принимаем направление
        if (numberOfTack != lastNumberOfTack) { // если изменился номер галса
            lastNumberOfTack = numberOfTack; // переприсваиваем номер галса
            onTackNumberChanged(); // обратабываем изменение
        } else { // если галс не менялся
            if (numberOfTack == 2 || numberOfTack == 3) { // если речь о бейдевинде
                int velocity = (int) location.getSpeed(); // берем скорость для анализа
                if ( velocity > maxTackVelocity[(numberOfTack - 2)] ) { // если скорость текущая больше максимальной для данного галса
                    maxTackVelocity[numberOfTack - 2] = velocity; // обновляем максимум. номера галсов 2 и 3, переводим в 0 и 1
                    tackVMGBearing [numberOfTack - 2] = bearing;
                    bearingLastMaxVMG = (int) location.getBearing();
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
            Arrays.fill(maxTackVelocity, 0); // очищаем массив скоростей
        } else upwindTackCounter ++; // считаем количество измененных курсов
        if (upwindTackCounter > 2) { // когда количество курсов достигает трех, считаем ветер
            int differenceVMGs = maxTackVelocity[0] - maxTackVelocity[1]; // находим разницу в ВМГ (прав - лев)
            int optimumVMG = (int) ((maxTackVelocity[0] + maxTackVelocity[1]) / 2); // находим истинное ВМГ (среднее)

            // находим на сколько градусов развернуть ветер
            // находим соотношение между ВМГ
            // берем разницу курсов между ВМГ
            // если последний галс 2 и его ВМГ меньше, прибавляем к нему угол. Если 3 и ВМГ меньше,
            // прибавляем к меньшему (???) (разницу * соотношение)

            readyToNextTack();
        }
        // сравниваем максималки и исходя из этого корректируем ветер
        // отправляем интерфейс с новыми показаниями ветра
    }

    // подготовка к смене галса опять на апвинд, на случай динамической корректировки ветра при длительном апвинде
    private void readyToNextTack () {
        upwindTackCounter --; // уменьшили на 1 чтобы на следующую смену опять высчитывать
        int optimumVMG = (int) ((maxTackVelocity[0] + maxTackVelocity[1]) / 2); // берем среднюю максималку, и
        Arrays.fill (maxTackVelocity, optimumVMG); // усредняем обе максималки ВМГ
    }


}



