package com.tcynik.racertimer.instruments;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.tcynik.racertimer.main_activity.domain.CoursesCalculator;
import com.tcynik.racertimer.main_activity.domain.CoursesCalculator;

import org.junit.Test;

//import org.junit.Assert;

public class CoursesCalculatorTest {
    CoursesCalculator coursesCalculator = new CoursesCalculator();

    /** setAngleFrom0To360*/
    @Test
    public void setAngleFrom0To360_60Correct () throws Exception {
        int angle = 60;
        assertEquals(60, CoursesCalculator.convertAngleFrom0To360(angle));
    }
    @Test
    public void setAngleFrom0To360_Min120Correct () throws Exception {
        int angle = -120;
        assertEquals(240, CoursesCalculator.convertAngleFrom0To360(angle));
    }
    @Test
    public void setAngleFrom0To360_510Correct () throws Exception {
        int angle = 510;
        assertNotEquals(516 - 360, CoursesCalculator.convertAngleFrom0To360(angle));
    }
    @Test
    public void setAngleFrom0To360_510Incorrect () throws Exception {
        int angle = 510;
        assertNotEquals(120, CoursesCalculator.convertAngleFrom0To360(angle));
    }

    /** Разница курсов между направлением ветра и направлением движения*/
    @Test
    public void windCourseAngle_60_30Correct () throws Exception{
        int wind = 60;
        int course = 30;
        int caseCorrect = 30 - 60; // курс справа, острый
        assertEquals(caseCorrect, CoursesCalculator.calcWindCourseAngle(wind, course));
    }
    @Test
    public void windCourseAngle_170_280Correct () throws Exception{
        int wind = 170;
        int course = 280;
        int caseCorrect = 280 - 170; // курс слева, полный 110град
        assertEquals(caseCorrect, CoursesCalculator.calcWindCourseAngle(wind, course));
    }
    @Test
    public void windCourseAngle_30_330Correct () throws Exception{
        int wind = 30;
        int course = 330;
        int caseCorrect = -1 * ((360 - 330) + 30); // курс справа, острый -60град, переход через 0
        assertEquals(caseCorrect, CoursesCalculator.calcWindCourseAngle(wind, course));
    }
    @Test
    public void windCourseAngle_300_10Correct () throws Exception{
        int wind = 300;
        int course = 10;
        int caseCorrect = (360 - 300) + 10; // курс слева, острый 70 град, переход через 0
        assertEquals(caseCorrect, CoursesCalculator.calcWindCourseAngle(wind, course));
    }

    /** симметричный к ветру курс*/
    @Test
    public void symmetryAngle_wind0CourseMin30Correct () throws Exception {
        int windDir = 0;
        int courseToWind = -30;
        int symmetryCorrect = 30;
        assertEquals(symmetryCorrect, CoursesCalculator.symmetryAngle(windDir, courseToWind));
    }

    @Test
    public void symmetryAngle_left () throws Exception {
        int windDir = 45;
        int courseToWind = 15;
        int symmetryCorrect = 30;
        assertEquals(symmetryCorrect, CoursesCalculator.symmetryAngle(windDir, courseToWind));
    }

    @Test
    public void symmetryAngle_right () throws Exception {
        int windDir = 45;
        int courseToWind = - 15;
        int symmetryCorrect = 60;
        assertEquals(symmetryCorrect, CoursesCalculator.symmetryAngle(windDir, courseToWind));
    }

    @Test
    public void symmetryAngle_leftCross360 () throws Exception {
        int windDir = 315;
        int courseToWind = 60;
        int symmetryCorrect = 255;
        assertEquals(symmetryCorrect, CoursesCalculator.symmetryAngle(windDir, courseToWind));
    }

    @Test
    public void symmetryAngle_rightCross360 () throws Exception {
        int windDir = 45;
        int courseToWind = -60;
        int symmetryCorrect = 105;
        assertEquals(symmetryCorrect, CoursesCalculator.symmetryAngle(windDir, courseToWind));
    }

    /** находим направление вектора по координатам */
    @Test
    public void bearingByCoordinates_rightTop () throws Exception {
        int x = 5;
        int y = 5;
        int resultCorrect = 45;
        assertEquals(resultCorrect, CoursesCalculator.bearingByCoordinates(x, y));
    }

    @Test
    public void bearingByCoordinates_leftTop () throws Exception {
        int x = -5;
        int y = 5;
        int resultCorrect = 315;
        assertEquals(resultCorrect, CoursesCalculator.bearingByCoordinates(x, y));
    }

    @Test
    public void bearingByCoordinates_rightBottom () throws Exception {
        int x = 5;
        int y = -5;
        int resultCorrect = 135;
        assertEquals(resultCorrect, CoursesCalculator.bearingByCoordinates(x, y));
    }

    @Test
    public void bearingByCoordinates_leftBottom () throws Exception {
        int x = -5;
        int y = -5;
        int resultCorrect = 225;
        assertEquals(resultCorrect, CoursesCalculator.bearingByCoordinates(x, y));
    }

    @Test
    public void diffAnglesSecondMore () throws Exception {
        int angleFirst = 30;
        int angleSecond = 90;
        int result = 60;
        assertEquals(result, CoursesCalculator.diffAngles(angleFirst, angleSecond));
    }

    @Test
    public void diffAnglesFirstMore () throws Exception {
        int angleFirst = 90;
        int angleSecond = 30;
        int result = -60;
        assertEquals(result, CoursesCalculator.diffAngles(angleFirst, angleSecond));
    }

    @Test
    public void diffAnglesSecondMoreCross360 () throws Exception {
        int angleFirst = 330;
        int angleSecond = 30;
        int result = 60;
        assertEquals(result, CoursesCalculator.diffAngles(angleFirst, angleSecond));
    }

    @Test
    public void diffAnglesFirstMoreCross360 () {
        int angleFirst = 30;
        int angleSecond = 330;
        int result = -60;
        assertEquals(result, CoursesCalculator.diffAngles(angleFirst, angleSecond));
    }

    /** направление ветра по двум бейдеиндам */
    @Test
    public void windBetweenTwoUpwindsCross360 () {
        int bearing1 = 350;
        int bearing2 = 30;
        int result = 10;
        assertEquals(result, CoursesCalculator.windBetweenTwoUpwinds(bearing1, bearing2));
    }

    @Test
    public void windBetweenTwoUpwindsCross360Contrary () {
        int bearing2 = 350;
        int bearing1 = 30;
        int result = 10;
        assertEquals(result, CoursesCalculator.windBetweenTwoUpwinds(bearing1, bearing2));
    }

    @Test
    public void windBetweenTwoUpwindsDirect () {
        int bearing1 = 60;
        int bearing2 = 100;
        int result = 80;
        assertEquals(result, CoursesCalculator.windBetweenTwoUpwinds(bearing1, bearing2));
    }

    @Test
    public void windBetweenTwoUpwindsContrary () {
        int bearing2 = 60;
        int bearing1 = 100;
        int result = 80;
        assertEquals(result, CoursesCalculator.windBetweenTwoUpwinds(bearing1, bearing2));
    }


}
