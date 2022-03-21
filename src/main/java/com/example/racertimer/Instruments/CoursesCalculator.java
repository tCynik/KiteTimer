package com.example.racertimer.Instruments;

/** класс для выполнения различных вычислений с курсами и углами
    целиком покрывается тестами
 */

public class CoursesCalculator {

    /** приведение заданного угла к интервалу от 0 до 359 (360 = 0) */
    public static int setAngleFrom0To360 (int angle) {
        int resultAngle = angle;
        if (angle < 0) resultAngle = angle + 360;
        if (angle >= 360) resultAngle = angle - 360;
        return resultAngle;
    }

    /** расчет угла к ветру */
    public static int calcWindCourseAngle (int windDir, int bearing) { // расчет курса относительно ветра (позорная срань с ifами)
        // правый галс - отрицательный, левый галс - положительный
        // Бейдевинд - модуль меньше 90, бакштаг - модуль больше 90
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

    public static int numberOfTack (int windDir, int bearing) { // определение номера галса
        // 1 - правый бакштаг, 2 - правый бейдевинд, 3 - левый бейдевинд 4 - левый бакштаг
        int windCourseAngle = calcWindCourseAngle(windDir, bearing);
        if (windCourseAngle > 90) return 4; // если больше 90 - левый бакштаг, = 4
        else if (windCourseAngle > 0) return 3; // если от 0 до 90 - левый бейдевинд, = 3
            else if (windCourseAngle < -90) return 1; // если меньше -90 - правый бакштаг, = 1
                else return 2; // в ином случае (не болььше 0 и не меньше -90 - правый бейдевинд, 2
    }

    // ВМГ по ветру, курсу, и скорости
    public static int VMGByWindBearingVelocity (int windDir, int bearing, int velocity) {
        double courseToWindRadians; // обьявляем переменную для расчета курсов в радианах
        int courseToWind = calcWindCourseAngle(windDir, bearing); // находим курс к ветру
        int velocityMadeGood;
        if (Math.abs(courseToWind) < 90) { // если курс острый, значит считаем апвинд
            courseToWindRadians = Math.toRadians( Math.abs(courseToWind) ); // считаем курс в радианах
            velocityMadeGood = (int)( Math.cos(courseToWindRadians) * velocity); // считаем ВМГ upwind -> положительный
        } else { // если курс тупой, считаем даунвинд
            courseToWindRadians = Math.toRadians( 180 - Math.abs(courseToWind) ); // считаем курс в радианах
            velocityMadeGood = (int)( -1 * Math.cos(courseToWindRadians) * velocity); // считаем ВМГ downwind -> отрицательный
        }
        return velocityMadeGood;
    }

    // нахождение симметричного к ветру угла - рассчитывается для угла к ветру!!! (не для аимута)
    public static int symmetryAngle (int windDirection, int windCourseAngle) {
        // на вход подается направление ветра (с какого азимута дует) и курс к ветру:
        // от бакштага к бейдевинду увеличение; правый галс отрицательный, левый галс положительный
        int symmetryCourse;
        symmetryCourse = windDirection - windCourseAngle;
        symmetryCourse = setAngleFrom0To360(symmetryCourse); // приводим в диапазон от 0 до 360
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

    public static int invertCourse (int course) {
        int resultCourse = course + 180;
        if (resultCourse > 360) resultCourse = resultCourse - 360;
        return resultCourse;
    }
}
