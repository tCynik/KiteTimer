package com.example.racertimer.Instruments;

import android.location.Location;
import android.util.Log;

public class WindStatistics { // класс для сбора статистики скоростей и рассчета истинного напр ветра
    private final static String PROJECT_LOG_TAG = "racer_timer_windStat";

    private int sizeOfSectors; // размер каждого сектора диаграммы в градусах - определяем в конструкторе
    private int numberOfSectors; // количество секторов - считаем из numberOfSectors
    private int[] windDiagram; // массив, в котором храним диаграмму
    private int sensitivity; // чувствительность диаграммы к изменениям в %

    // для определения репрезентативности накопленных данных переменные для расчета:
    private int maxAllowedEmptyZone = 120; // наибольший разрешенный незаполненный сектор В ГРАДУСАХ
    private int maxCurrentEmptyZone; // максимальная текущая не заполненная зона В КОЛИЧЕСТВЕ СКТОРОВ
    // !!!при сравнении переменных не забыть о переводе количества секторо в градусы!!!
    private int minFilledZones = 150; // минимальная разрешенная полностью заполненная зона диаграммы в градусах
    private int summVelocity;

    private int maxVelocity;
    // если есть хоть один незаполненный сектора больше этого, выборка считается не репрезентативной.
    private int windDirection, lastWindDirection;
    private float[] sin, cos;

    private boolean windDiagramIsRepresentable = false; // флаг того, что статистика набрана достаточная

    WindChangedHerald windChangedHerald; // интерфейс для вывода нового значения ветра

    public WindStatistics(int sizeOfSectors, WindChangedHerald windChangedHerald) {
        this.sizeOfSectors = sizeOfSectors;
        numberOfSectors = 360 / sizeOfSectors;
        windDiagram = new int[numberOfSectors];
        // заполняем таблицу синусов-косинусов для этой длины массива - в цикле
        sin = new float[numberOfSectors];
        cos = new float[numberOfSectors];
        for (int i = 0; i < numberOfSectors; i++) {
            double currentAngle = (i + 0.5) * sizeOfSectors; // берем середину сектора
            sin[i] = (float) Math.sin(Math.toRadians(currentAngle));
            cos[i] = (float) Math.cos(Math.toRadians(currentAngle));
//            Log.i(PROJECT_LOG_TAG, " sin = " + sin[i] + ", cos = " + cos[i]); // проверено
        }
        this.windChangedHerald = windChangedHerald;
        sensitivity = 50; // параметр чувствистельности для настройки
    }

    public void setSensitivity(int updSensitivity) { // сеттер для возможности настройки на ходу
        this.sensitivity = updSensitivity;
    }

    private int calculateNumberOfSector(int bearing) { // метод определения номера сектора
        return (int) bearing / sizeOfSectors;
    }

    public void onLocationChanged(Location location) { // главный метод. в этом методе заполняем диаграмму
        int bearing = (int) location.getBearing();
        int numTheSector = calculateNumberOfSector(bearing); // высчитываем номер сектора

        int velocity = (int) location.getSpeed();

        if (velocity > windDiagram[calculateNumberOfSector(bearing)]) { // если новый максимум
            windDiagram[numTheSector] = velocity; // обновляем максимум

            if (windDiagramIsRepresentable) { // если репрез. - уменьшаем симметричную скорость
                int velocityDifferent = velocity - windDiagram[numTheSector];
                cutSymmetricalMaximum(bearing, velocityDifferent);
            }

            /////// после тестирования считать только при репрезентативности для экономии  ресурсов - условие ниже
            windDirection = calculateWindDirection();

            if (windDiagramIsRepresentable) // если репрезент. - отправляем бродкаст с новыми данными
                if (lastWindDirection != windDirection) {
                    windChangedHerald.onWindDirectionChanged(windDirection);// отправляем broadcast с новым направлением
                }
            lastWindDirection = windDirection; // обновляем данные по направлению
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
        double summX = 0; // координата X суммирующего вектора
        double summY = 0; // координата Y суммирующего вектора

        int representCounter = 0; // счетчик текущего не заполненного сектора
        maxCurrentEmptyZone = 0; // счетчик ширины максимального не заполненного сектора
        maxVelocity = 0; // переменная для определения максимальной завфиксированной скорости

        for (int i = 0; i < numberOfSectors; i++) { // перебираем все сектора
            // определяем репрезентативность диаграммы
            if (! windDiagramIsRepresentable) { // считаем длину наибольшего незаполненного сектора
                if (windDiagram[i] == 0) representCounter ++;
                else representCounter = 0;
                if (representCounter > maxCurrentEmptyZone) maxCurrentEmptyZone = representCounter;
            }
            // находим координаты конца суммирующего вектора
            summX = summX + sin[i] * windDiagram[i]; // координата вектора X
            summY = summY + cos[i] * windDiagram[i]; // Координата вектора Y

            // считаем промежуточные данные для определения достаточности количества данных
            summVelocity = summVelocity + windDiagram[i]; // находим сумму длин всех векторов
            if (windDiagram[i] > maxVelocity) maxVelocity = windDiagram [i]; // находим максимальную скорость
//            if (windDiagram[i] != 0) Log.i(PROJECT_LOG_TAG, "calcWindDir: sector = " + i + ", speed = "+ windDiagram[i]+ ", sumX = " + summX+", sumY ="+ summY);
        }

        // по итогу перебора проверяем массив на репрезентативность
        if (! windDiagramIsRepresentable ) windDiagramIsRepresentable = checkRepresentative();
//        if (! windDiagramIsRepresentable & maxCurrentEmptyZone < maxAllowedEmptyZone / sizeOfSectors) {
//            windDiagramIsRepresentable = true;
//            Log.i(PROJECT_LOG_TAG, "max repres counter = "+ maxCurrentEmptyZone +", the wind diagram is being representative now!");
//        }

        windDirection = CoursesCalculator.bearingByCoordinates(summX, summY);
        windDirection = CoursesCalculator.invertCourse(windDirection);
        Log.i(PROJECT_LOG_TAG, " founded wind direction =  " + windDirection + ", last windDir = " + this.windDirection);

        return windDirection;
    }

    private boolean checkRepresentative() { // метод проверки выборки на репрезентативность
        boolean checkIsPassed = true;
        // если ширина максимального незаполненного сектора больше указанной в настройках (перевод из секторов в градусы)
        if (maxCurrentEmptyZone > maxAllowedEmptyZone / sizeOfSectors) checkIsPassed = false;
            Log.i(PROJECT_LOG_TAG, "max repres counter = "+ maxCurrentEmptyZone +", the wind diagram is being representative now!");

        // если сумма скоростей всех секторов менее чем в X раз превышает скорость наибольшего вектора
        // коэффициент требуемого превышения суммы по секторам над наибольшим сектором X = exсeedRatio
        int exсeedRatio = minFilledZones / (sizeOfSectors * 4);
        // среднюю скорость примем равную половине максимальной скорости
        if (summVelocity < maxVelocity * exсeedRatio) checkIsPassed = false;

        Log.i(PROJECT_LOG_TAG, "checking represent: "+checkIsPassed+", maxNotFilledSector = "+ maxCurrentEmptyZone
                +", sumVelocities = "+ summVelocity +", maxVelocity*ratio = " +(maxVelocity * exсeedRatio));

        return  checkIsPassed;
    }
}
