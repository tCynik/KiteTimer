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

    /** setAngleFrom0To360*/
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



}
