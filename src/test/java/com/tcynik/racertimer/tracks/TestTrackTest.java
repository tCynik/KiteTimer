package com.tcynik.racertimer.tracks;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestTrackTest {
    TestPoint firstPoint = new TestPoint(0, 0);

    @Test
    public void bearing45() throws Exception {
        TestPoint secondPoint = new TestPoint(5, 5);
        double bearing = firstPoint.bearingTo(secondPoint);
        assertEquals(45, bearing);
    }

    @Test
    public void bearing135() throws Exception {
        TestPoint secondPoint = new TestPoint(-5, 5);
        double bearing = firstPoint.bearingTo(secondPoint);
        assertEquals(135, bearing);
    }

    @Test
    public void bearing225() throws Exception {
        TestPoint secondPoint = new TestPoint(-5, -5);
        double bearing = firstPoint.bearingTo(secondPoint);
        assertEquals(225, bearing);
    }

    @Test
    public void bearing315() throws Exception {
        TestPoint secondPoint = new TestPoint(5, -5);
        double bearing = firstPoint.bearingTo(secondPoint);
        assertEquals(315, bearing);
    }

    @Test
    public void bearing0() throws Exception {
        TestPoint secondPoint = new TestPoint(5, 0);
        double bearing = firstPoint.bearingTo(secondPoint);
        assertEquals(0, bearing);
    }

    @Test
    public void bearing90() throws Exception {
        TestPoint secondPoint = new TestPoint(0, 5);
        double bearing = firstPoint.bearingTo(secondPoint);
        assertEquals(90, bearing);
    }

    @Test
    public void bearing180() throws Exception {
        TestPoint secondPoint = new TestPoint(-5, 0);
        double bearing = firstPoint.bearingTo(secondPoint);
        assertEquals(180, bearing);
    }

    @Test
    public void bearing270() throws Exception {
        TestPoint secondPoint = new TestPoint(0, -5);
        double bearing = firstPoint.bearingTo(secondPoint);
        assertEquals(270, bearing);
    }

}
