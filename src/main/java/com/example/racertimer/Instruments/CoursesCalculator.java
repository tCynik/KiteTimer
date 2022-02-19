package com.example.racertimer.Instruments;

/** класс для выполнения различных вычислений с курсами и углами
    целиком покрывается тестами
 */

public class CoursesCalculator {

    /** приведение заданного угла к интервалу от 0 до 360 */
    public static int setAngleFrom0To360 (int angle) {
        int resultAngle = angle;
        if (angle < 0) resultAngle = angle + 360;
        if (angle > 360) resultAngle = angle - 360;
        return resultAngle;
    }

    /** расчет угла к ветру */
    public static int calcWindCourseAngle (int windDir, int bearing) { // расчет курса относительно ветра (позорная срань с ifами)
        int windCourseAngle = 0;
        if (windDir < 180) { // частный случай если напр ветра до 180 градусов
            if (bearing > windDir & bearing < windDir + 180) { // с какой стороны относительно ветра вектор движения
                windCourseAngle = bearing - windDir + 180; // если слева, считаем от вектора ветра. Азимут положительный
            } else {
                windCourseAngle = bearing - windDir ; // если справа, считаем в обратную сторону от ветра. Азимут отрицательный
                if (bearing > windDir) windCourseAngle = windCourseAngle - 360 ; //windCourseAngle = 180 - windCourseAngle;
            }
            if (windCourseAngle > 180) windCourseAngle -= 180;
        } else { // если направление ветра более 180 градусов
            if (windDir > bearing & (windDir - 180) < bearing ) { // если вектор движения справа по ветру
                windCourseAngle = bearing - windDir;
            } else { // если слева по ветру
                windCourseAngle = bearing - windDir;
                if (bearing < windDir) {
                    windCourseAngle = windCourseAngle + 360;
                }
            }
        }
        return windCourseAngle;
    }

    // нахождение симметричного к ветру угла
    public static int symmetryAngle (int windDirection, int windCourseAngle) {
        // на вход подается направление ветра (с какого азимута дует) и курс к ветру:
        // от бакштага к бейдевинду увеличение; правый галс отрицательный, левый галс положительный
        int symmetryCourse;
        symmetryCourse = windDirection - windCourseAngle;
        symmetryCourse = setAngleFrom0To360(symmetryCourse);
        return symmetryCourse;
    }

    public static int bearingByCoordinates (double x, double y) { // вычисление азимута вектора по координатам конца (первая точка 0:0)
        double vectorLength = vectorLevgthByCoordinates(x, y);
        int direction = (int) Math.toDegrees(Math.acos(y / vectorLength)); // симметрично нулевой оси 0Y
        if (x < 0) // если левее оси, симметрично переносим
            direction = 180 + (180 - direction);

        return direction;
    }

    public static double vectorLevgthByCoordinates (double x, double y) {
        return Math.pow((Math.pow(x, 2) + Math.pow(y, 2)), 0.5); // длина вектора
    }
}
