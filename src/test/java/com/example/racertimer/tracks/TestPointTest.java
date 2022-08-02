package com.example.racertimer.tracks;

import static org.junit.Assert.assertEquals;

import android.location.Location;

import org.junit.Test;

public class TestPointTest {
    TestPoint firstPoint = new TestPoint(0, 0);
    TestPoint secondPoint = new TestPoint(5, 5);

    @Test
    public void checkScale () throws Exception {
        Location firstLocation = firstPoint.castToLocation();
        Location secondLocation = secondPoint.castToLocation();

        int distPoints = firstPoint.distTo(secondPoint);
        assertEquals(distPoints, firstLocation.distanceTo(secondLocation));
    }
}
