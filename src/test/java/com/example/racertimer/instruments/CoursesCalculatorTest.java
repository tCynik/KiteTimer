package com.example.racertimer.instruments;

import com.example.racertimer.Instruments.CoursesCalculator;

import org.junit.Assert;
import org.junit.Test;

public class CoursesCalculatorTest {
    CoursesCalculator coursesCalculator = new CoursesCalculator();

    /** setAngleFrom0To360*/
    @Test
    public void setAngleFrom0To360_60Correct () throws Exception {
        int angle = 60;
        Assert.assertEquals(60, CoursesCalculator.setAngleFrom0To360(angle));
    }
    @Test
    public void setAngleFrom0To360_Min120Correct () throws Exception {
        int angle = -120;
        Assert.assertEquals(240, CoursesCalculator.setAngleFrom0To360(angle));
    }
    @Test
    public void setAngleFrom0To360_510Correct () throws Exception {
        int angle = 510;
        Assert.assertNotEquals(516 - 360, CoursesCalculator.setAngleFrom0To360(angle));
    }
    @Test
    public void setAngleFrom0To360_510Incorrect () throws Exception {
        int angle = 510;
        Assert.assertNotEquals(120, CoursesCalculator.setAngleFrom0To360(angle));
    }

    /** Разница курсов между направлением ветра и направлением движения*/
    @Test
    public void windCourseAngle_60_30Correct () throws Exception{
        int wind = 60;
        int course = 30;
        int caseCorrect = 30 - 60; // курс справа, острый
        Assert.assertEquals(caseCorrect, CoursesCalculator.calcWindCourseAngle(wind, course));
    }
    @Test
    public void windCourseAngle_170_280Correct () throws Exception{
        int wind = 170;
        int course = 280;
        int caseCorrect = 280 - 170; // курс слева, полный 110град
        Assert.assertEquals(caseCorrect, CoursesCalculator.calcWindCourseAngle(wind, course));
    }
    @Test
    public void windCourseAngle_30_330Correct () throws Exception{
        int wind = 30;
        int course = 330;
        int caseCorrect = -1 * ((360 - 330) + 30); // курс справа, острый -60град, переход через 0
        Assert.assertEquals(caseCorrect, CoursesCalculator.calcWindCourseAngle(wind, course));
    }
    @Test
    public void windCourseAngle_300_10Correct () throws Exception{
        int wind = 300;
        int course = 10;
        int caseCorrect = (360 - 300) + 10; // курс слева, острый 70 град, переход через 0
        Assert.assertEquals(caseCorrect, CoursesCalculator.calcWindCourseAngle(wind, course));
    }

    /** симметричный к ветру курс*/
    @Test
    public void symmetryAngle_wind0CourseMin30Correct () throws Exception {
        int windDir = 0;
        int courseToWind = -30;
        int symmetryCorrect = 30;
        Assert.assertEquals(symmetryCorrect, CoursesCalculator.symmetryAngle(windDir, courseToWind));
    }

    @Test
    public void symmetryAngle_left () throws Exception {
        int windDir = 45;
        int courseToWind = 15;
        int symmetryCorrect = 30;
        Assert.assertEquals(symmetryCorrect, CoursesCalculator.symmetryAngle(windDir, courseToWind));
    }

    @Test
    public void symmetryAngle_right () throws Exception {
        int windDir = 45;
        int courseToWind = - 15;
        int symmetryCorrect = 60;
        Assert.assertEquals(symmetryCorrect, CoursesCalculator.symmetryAngle(windDir, courseToWind));
    }

    @Test
    public void symmetryAngle_leftCross360 () throws Exception {
        int windDir = 315;
        int courseToWind = 60;
        int symmetryCorrect = 255;
        Assert.assertEquals(symmetryCorrect, CoursesCalculator.symmetryAngle(windDir, courseToWind));
    }

    @Test
    public void symmetryAngle_rightCross360 () throws Exception {
        int windDir = 45;
        int courseToWind = -60;
        int symmetryCorrect = 105;
        Assert.assertEquals(symmetryCorrect, CoursesCalculator.symmetryAngle(windDir, courseToWind));
    }

    /** находим направление вектора по координатам */
    @Test
    public void bearingByCoordinates_rightTop () throws Exception {
        int x = 5;
        int y = 5;
        int resultCorrect = 45;
        Assert.assertEquals(resultCorrect, CoursesCalculator.bearingByCoordinates(x, y));
    }

    @Test
    public void bearingByCoordinates_leftTop () throws Exception {
        int x = -5;
        int y = 5;
        int resultCorrect = 315;
        Assert.assertEquals(resultCorrect, CoursesCalculator.bearingByCoordinates(x, y));
    }

    @Test
    public void bearingByCoordinates_rightBottom () throws Exception {
        int x = 5;
        int y = -5;
        int resultCorrect = 135;
        Assert.assertEquals(resultCorrect, CoursesCalculator.bearingByCoordinates(x, y));
    }

    @Test
    public void bearingByCoordinates_leftBottom () throws Exception {
        int x = -5;
        int y = -5;
        int resultCorrect = 225;
        Assert.assertEquals(resultCorrect, CoursesCalculator.bearingByCoordinates(x, y));
    }






}
