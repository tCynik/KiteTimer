package com.example.racertimer.Instruments;

import android.location.Location;
import android.os.CountDownTimer;
import android.util.Log;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class WindStatistics { // класс для сбора статистики скоростей и рассчета истинного напр ветра
    private final static String PROJECT_LOG_TAG = "racer_timer_windStat";

    private final static int MIN_KMH_TO_STATISTICS = 6; // минимальный порог скорости, который идет в
    // статистику - для фильтрации пешей хотьбы и случайных всплесков

    private int sizeOfSectors; // размер каждого сектора диаграммы в градусах - определяем в конструкторе
    private int numberOfSectors; // количество секторов - считаем из numberOfSectors
    private int[] windDiagram; // массив, в котором храним диаграмму
    private int sensitivity; // чувствительность диаграммы к изменениям в %

    // для определения репрезентативности накопленных данных переменные для расчета:
    private int maxAllowedEmptyZone = 120; // наибольший разрешенный незаполненный сектор В ГРАДУСАХ
    private int maxCurrentEmptyZone; // максимальная текущая не заполненная зона В КОЛИЧЕСТВЕ СКТОРОВ
    // !!!при сравнении переменных не забыть о переводе количества секторо в градусы!!!
    private int minFilledZones = 120; // минимальная разрешенная полностью заполненная зона диаграммы в градусах
    // = 2 х 60 градусов
    private int currentFilledSectors; // текущий счетчик количества заполненных секторов
    private int summVelocity = 0;

    private int maxVelocity;
    // если есть хоть один незаполненный сектора больше этого, выборка считается не репрезентативной.
    private int windDirection = 10000; // 10000 = статистических данных по ветру нет
    private int lastWindDirection;
    private float[] sin, cos;

    private boolean windDiagramIsRepresentable = false; // флаг того, что статистика набрана достаточная

    WindChangedHerald windChangedHerald; // интерфейс для вывода нового значения ветра
    private CountDownTimer countDownTimer; // таймер для причесывания диаграммы когда не приходят новые данные
    private Timer timer; // таймер для причесывания диаграммы когда какое-то время не приходят новые данные
    private TimerTask timerTask; // тело задачи, которая выполняется по команде таймера

    public WindStatistics(int sizeOfSectors, WindChangedHerald windChangedHerald) {
        this.sizeOfSectors = sizeOfSectors;
        numberOfSectors = 360 / sizeOfSectors;
        windDiagram = new int[numberOfSectors];
        Arrays.fill(windDiagram, 0); // инициализируем массив нулем
        lastWindDirection = windDirection;
        // заполняем таблицу синусов-косинусов для этой длины массива - в цикле
        sin = new float[numberOfSectors];
        cos = new float[numberOfSectors];
        for (int i = 0; i < numberOfSectors; i++) {
            double currentAngle = (i + 0.5) * sizeOfSectors; // берем середину сектора
            sin[i] = (float) Math.sin(Math.toRadians(currentAngle));
            cos[i] = (float) Math.cos(Math.toRadians(currentAngle));
        }
        this.windChangedHerald = windChangedHerald;
        sensitivity = 50; // параметр чувствистельности для настройки

    }

    public void setSensitivity(int updSensitivity) { // сеттер для возможности настройки на ходу
        this.sensitivity = updSensitivity;
    }

    public int getWindDirection () {
        if (windDiagramIsRepresentable)
            return windDirection;
        else return 10000;
    }

    private int calculateNumberOfSector(int bearing) { // метод определения номера сектора
        return (int) bearing / sizeOfSectors;
    }

    public void onLocationChanged(Location location) { // главный метод. в этом методе заполняем диаграмму
        int bearing = (int) location.getBearing();
        int numTheSector = calculateNumberOfSector(bearing); // высчитываем номер сектора

        int velocity = (int) (location.getSpeed() * 3.6); // в километрах в час

        // если имеем новый максимум в диагремме, превышающий порог фильтра чувствительности диаграммы
        if (velocity > MIN_KMH_TO_STATISTICS & velocity > windDiagram[calculateNumberOfSector(bearing)]) {
            // сначала обновляем максимум в текущем секторе
            windDiagram[numTheSector] = velocity;

            // если диаграмма репрезентативная, уменьшаем скорость в симметричном секторе
            if (windDiagramIsRepresentable) {
                int velocityDifferent = velocity - windDiagram[numTheSector];
                cutSymmetricalMaximum(bearing, velocityDifferent);
            }

            // высчитываем новое направление ветра
            windDirection = calculateWindDirection();

            if (windDiagramIsRepresentable) { //  запускаем/обнуляем таймер причесывания
            }

            // если у нас нет изменений в диаграмме, причесываем ее.
            // проверяем условия и отправляем бродкаст
            if (windDiagramIsRepresentable) {// если выборка репрезентативная, выполянем:

                // обнуляем таймер
                if (timerTask != null)  {
                    timer.cancel(); // останавливаем ранее запущенный таймер
                    timerTask.cancel();
                }
                timer = new Timer(); // пересоздаем таймер
                timerTask = new TimerTask() { // создаем таймер таск, который будет выполнять таймер
                    @Override
                    public void run() {
                        Log.i(PROJECT_LOG_TAG, " combing the diagram... ");
                        combTheDiagram();
                        timer.cancel();
                    }
                };
                timer.schedule(timerTask, 60000, 1000); // запускаем его заново

                // отправляем бродкаст с новыми данными
                if (lastWindDirection != windDirection) { // если зафиксировано изменение направления ветра с прошлой итерации
                    Log.i(PROJECT_LOG_TAG, "sending wind direction broadcast");
                    windChangedHerald.onWindDirectionChanged(windDirection);// отправляем broadcast с новым направлением
                }
            }
            lastWindDirection = windDirection; // обновляем данные по направлению ветра для фиксации изменений
        }
    }

    void cutSymmetricalMaximum(int bearing, int velocityDifferent) { // симметрично прибавлению отнимаем
        int symmetryDirection; // направление, симметричное исходному относительно ветра
        int sectorSymmetry; // искомый номер сектора, в котором находится symmetryDirection
        int incVelocityBySensitivity = (int) velocityDifferent * sensitivity / 100; // считаем разницу с учетом чувствительности
        Log.i(PROJECT_LOG_TAG, "incVelocity = "+ incVelocityBySensitivity +", velDiff = "+ velocityDifferent);

        // определяем номер симметричного ОТНОСИТЕЛЬНО ВЕТРА сектора для изменения
        symmetryDirection = CoursesCalculator.symmetryAngle(windDirection, CoursesCalculator.calcWindCourseAngle(windDirection, bearing));
        // сначала определяем минимальную разницу между скоростью и ветром
        Log.i(PROJECT_LOG_TAG, "first symmetrical dir = "+ symmetryDirection+", bearing = "+ bearing + ", wind = " + windDirection);

        // приводим направление к диапазону от 0 до 360 для подсчета сектора в пределах массива
        symmetryDirection = CoursesCalculator.setAngleFrom0To360(symmetryDirection);

        sectorSymmetry = calculateNumberOfSector(symmetryDirection); // высчитываем симметричный сектор
        Log.i(PROJECT_LOG_TAG, "wind = "+ windDirection+", bearing = "+ bearing+", symmetry direction =  " + symmetryDirection + ", sector = "+ sectorSymmetry);

        // уменьшаем в этом секторе показания если они не нулевые
        if (windDiagram[sectorSymmetry] > 0) {
            windDiagram[sectorSymmetry] = windDiagram[sectorSymmetry] - incVelocityBySensitivity;
            Log.i(PROJECT_LOG_TAG, "increese sector "+sectorSymmetry+" velocity: was = "+ (windDiagram[sectorSymmetry] + incVelocityBySensitivity) +", now = "+ windDiagram[sectorSymmetry]);
            if (windDiagram[sectorSymmetry] < 0) windDiagram[sectorSymmetry] = 0;
        }
    }

    int calculateWindDirection() { // в этом методе высчитываем направление ветра
        int windDirection = this.windDirection;
        double vectorLength;
        summVelocity = 0; // сумма длин всех векторов
        currentFilledSectors = 0;
        double summX = 0; // координата X суммирующего вектора
        double summY = 0; // координата Y суммирующего вектора
        // счетчик пустых зон
        int emptyZonesCounter = 0; // счетчик текущего не заполненного сектора
        maxCurrentEmptyZone = 0; // счетчик ширины максимального не заполненного сектора

        // TODO: параметр maxVelocity походу не востребован в рамках текущей логики. Подумать - почистить
        maxVelocity = 0; // переменная для определения максимальной завфиксированной скорости

        for (int i = 0; i < numberOfSectors; i++) { // перебираем все сектора

            // определяем репрезентативность диаграммы
            if (! windDiagramIsRepresentable) { // если диаграмма пока не репрезентативная
                if (windDiagram[i] == 0) {
                    emptyZonesCounter ++; // если сектор не равен нулю, считаем длину наибольшего незаполненного сектора
                    // обновляем параметр максимальной обнаруженной пустой зоны
                    if (emptyZonesCounter > maxCurrentEmptyZone) maxCurrentEmptyZone = emptyZonesCounter;
                }
                else { // иначе (в секторе записана ненулевая скорость):
                    emptyZonesCounter = 0; // счетчик нулевых секторов сбрасываем
                    currentFilledSectors ++; // а так же считаем количество заполненных секторов
                }
            }

            // суммируем координаты текущего вектора -> находим координаты конца суммирующего вектора
            summX = summX + sin[i] * windDiagram[i]; // координата вектора X
            summY = summY + cos[i] * windDiagram[i]; // Координата вектора Y

            // считаем промежуточные данные для определения достаточности количества данных
            summVelocity = summVelocity + windDiagram[i]; // находим сумму длин всех векторов
            if (windDiagram[i] > maxVelocity) maxVelocity = windDiagram [i]; // находим максимальную скорость
//            if (windDiagram[i] != 0) Log.i(PROJECT_LOG_TAG, "calcWindDir: sector = " + i + ", speed = "+ windDiagram[i]+ ", sumX = " + summX+", sumY ="+ summY);
        }

        // по итогу перебора проверяем массив на репрезентативность
        if (! windDiagramIsRepresentable ) windDiagramIsRepresentable = checkRepresentative();

        windDirection = CoursesCalculator.bearingByCoordinates(summX, summY); // направление ветра - куда дует
        windDirection = CoursesCalculator.invertCourse(windDirection); // инвертируем: откуда дует
        Log.i(PROJECT_LOG_TAG, " founded wind direction =  " + windDirection + ", last windDir = " + this.windDirection);

        return windDirection;
    }

    private boolean checkRepresentative() { // метод проверки выборки на репрезентативность
        boolean checkIsPassed = true;
        /** проверка по максимальной ширине незаполненного сектора
         * если ширина максимального незаполненного сектора больше указанной в настройках (перевод из секторов в градусы)
         */
        if (maxCurrentEmptyZone > maxAllowedEmptyZone / sizeOfSectors) checkIsPassed = false;

        /**  проверка по количеству заполненных секторов */
        // минимальное количество заполненных секторов
        int minNumberOfFilledZones = minFilledZones / sizeOfSectors; // минимальное количество заполненных зон
        if (currentFilledSectors < minNumberOfFilledZones) checkIsPassed = false;

        Log.i(PROJECT_LOG_TAG, "checking represent: "+checkIsPassed+", maxNotFilledSector = "+ maxCurrentEmptyZone
                +", min filled sectors = "+ minNumberOfFilledZones +", current filled = " + currentFilledSectors);

        if (checkIsPassed) Log.i(PROJECT_LOG_TAG, "max repres counter = "+ maxCurrentEmptyZone +", the wind diagram is being representative now!");

        return  checkIsPassed;
    }

    private void combTheDiagram() { // удаление локальных забросов-максимумов
        // убираем локальные забросы: перебираем диаграмму (половину), сравнивая все симметричные вектора, и
        // выравниваем больший с меньшим
//        printDiagram();
        int currentDirectionToWind, symmetryDirectionToWind; // рабочие переменные для сравнения. Симметричная - относительно ветра
        for (int i = 0; i < (numberOfSectors / 2); i++) {
            currentDirectionToWind = ((i * sizeOfSectors) + (sizeOfSectors / 5)); // азимут выбранного сектора
            // превращаем в курс к ветру
            currentDirectionToWind = CoursesCalculator.calcWindCourseAngle(windDirection, currentDirectionToWind);

            // симметричный к ветру курс
            symmetryDirectionToWind = CoursesCalculator.symmetryAngle(windDirection, currentDirectionToWind);
            // номер сектора, симметричного к заданному
            int symmetrySector = symmetryDirectionToWind / sizeOfSectors;

            // сравниваем, находим наименьшее значение, большее приравниваем к меньшему
            if (windDiagram[i] > windDiagram [symmetrySector]) {
                windDiagram [i] = windDiagram [symmetrySector];
                // если привели к нулю, выправляем на минималку
                if (windDiagram [i] == 0) windDiagram [i] = MIN_KMH_TO_STATISTICS;
            }
            if (windDiagram[i] < windDiagram [symmetrySector]) {
                windDiagram [symmetrySector] = windDiagram [i];
                if (windDiagram [symmetrySector] == 0) windDiagram [symmetrySector] = MIN_KMH_TO_STATISTICS;
            }
        }
//        printDiagram();

    }

    // служебный метод для вывода показаний диаграммы
    private void printDiagram () {
        Log.i(PROJECT_LOG_TAG, "Diagram printing: ");

        for (int i = 0; i < numberOfSectors; i++) {
            Log.i(PROJECT_LOG_TAG, "i = "+ i + ", value = " + windDiagram[i]);
        }
    }
}
