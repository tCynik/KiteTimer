package com.example.racertimer.main_activity.domain;

import com.example.racertimer.main_activity.data.wind_direction.TackDirection;

/** класс для выполнения различных вычислений с курсами и углами
    целиком покрывается тестами
 */

public class CoursesCalculator {

    /** приведение заданного угла к интервалу от 0 до 359 (360 = 0) */
    public static int convertAngleFrom0To360(int angle) {
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

    /** расчет номера галса по четвертям */
    public static TackDirection numberOfTack (int windDir, int bearing) { // определение номера галса
        int windCourseAngle = calcWindCourseAngle(windDir, bearing);
        if (windCourseAngle > 90) return TackDirection.DOWNWIND_LEFT; // если больше 90 - левый бакштаг
        else if (windCourseAngle > 0) return TackDirection.UPWIND_LEFT; // если от 0 до 90 - левый бейдевинд
            else if (windCourseAngle < -90) return TackDirection.DOWNWIND_RIGHT; // если меньше -90 - правый бакштаг
                else return TackDirection.UPWIND_RIGHT; // в ином случае (не болььше 0 и не меньше -90 - правый бейдевинд
    }

    /** расчет ВМГ по ветру, курсу, и скорости */
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

    /** нахождение симметричного к ветру угла - рассчитывается для угла к ветру!!! (не для аимута) */
    public static int symmetryAngle (int windDirection, int windCourseAngle) {
        // на вход подается направление ветра (с какого азимута дует) и курс к ветру:
        // от бакштага к бейдевинду увеличение; правый галс отрицательный, левый галс положительный
        int symmetryCourse;
        symmetryCourse = windDirection - windCourseAngle;
        symmetryCourse = convertAngleFrom0To360(symmetryCourse); // приводим в диапазон от 0 до 360
        return symmetryCourse;
    }

    /** вычисление азимута вектора по координатам конца (первая точка 0:0) */
    public static int bearingByCoordinates (double x, double y) {
        double vectorLength = vectorLengthByCoordinates(x, y);
        int direction = (int) Math.toDegrees(Math.acos(y / vectorLength)); // симметрично нулевой оси 0Y
        if (x < 0) // если левее оси, симметрично переносим
            direction = 180 + (180 - direction);

        return direction;
    }

    /** вычисление длины вектора по координатам конца (первая точка 0:0) */
    public static double vectorLengthByCoordinates(double x, double y) {
        return Math.pow((Math.pow(x, 2) + Math.pow(y, 2)), 0.5); // длина вектора
    }

    /** переворот курса на 180 градусов */
    public static int invertCourse (int course) {
        int resultCourse = course + 180;
        if (resultCourse > 360) resultCourse = resultCourse - 360;
        return resultCourse;
    }

    /** вычисление наименьшего угла между двумя лучами с учетом возможного перехода через 0
     * Считаем по часовой стрелке от 1 ко 2. положительная разница - по часовой, отрицательная - против часовой */
    public static int diffAngles (int angleFirst, int angleSecond) {
        int directDiff = angleSecond - angleFirst; // прямая разница
        if (directDiff > 180) { // если разница больше 180, то есть переход через 0 и второй угол больше первого
            directDiff = -1 * (angleFirst + (360 - angleSecond));// получаем отрицательное значение
        } else if (directDiff < -180) { // если разница меньше -180, то есть переход но первый больше второго
            directDiff = angleSecond + (360 - angleFirst); // получаем положительное значение
        }
        return directDiff;
    }

    /** вычисление напрвления ветра между двумя бейдевиндами */
    public static int windBetweenTwoUpwinds(int bearing1, int bearing2) {
        int windDirection = 1000;
        int biggerBearing, smallerBearing;

        if (bearing1 > bearing2) {
            biggerBearing = bearing1;
            smallerBearing = bearing2;
        }
        else {
            biggerBearing = bearing2;
            smallerBearing = bearing1;
        }

        int directDiff = biggerBearing - smallerBearing;
        if (directDiff > 180) { // обработка перехода через 0
            directDiff = 360 - directDiff;
            windDirection = smallerBearing - directDiff/2;
        } else windDirection = smallerBearing + directDiff/2;

        return windDirection;
    }

}
