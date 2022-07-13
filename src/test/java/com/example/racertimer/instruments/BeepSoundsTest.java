package com.example.racertimer.instruments;

import com.example.racertimer.multimedia.BeepSounds;

import org.junit.Assert;
import org.junit.Test;

public class BeepSoundsTest {
    @Test
    public void calculateRateFromPercent_1 () throws Exception {
        int percent = 100;
        float result = 2f;
        Assert.assertEquals(result, BeepSounds.calculateRateFromPercent(percent), 0.1f);
    }

    @Test
    public void calculateRateFromPercent_2 () throws Exception {
        int percent = 0;
        float result = 0.5f;
        Assert.assertEquals(result, BeepSounds.calculateRateFromPercent(percent), 0.1f);
    }

    @Test
    public void calculateRateFromPercent_3 () throws Exception {
        int percent = 50;
        float result = 1.25f;
        Assert.assertEquals(result, BeepSounds.calculateRateFromPercent(percent), 0.1f);
    }

    @Test
    public void calculateRateFromPercent_4 () throws Exception {
        int percent = 10;
        float result = 0.65f;
        Assert.assertEquals(result, BeepSounds.calculateRateFromPercent(percent), 0.1f);
    }



}
