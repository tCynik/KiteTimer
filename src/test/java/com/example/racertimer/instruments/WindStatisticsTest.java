package com.example.racertimer.instruments;

import com.example.racertimer.Instruments.WindChangedHerald;
import com.example.racertimer.Instruments.WindStatistics;

public class WindStatisticsTest {
    WindChangedHerald windChangedHerald = new WindChangedHerald() {
        @Override
        public void onWindDirectionChanged(int windDirection) {

        }
    };

    WindStatistics windStatistics = new WindStatistics(45, windChangedHerald);

//    @Test
//    public void CutSymmetricalMaximumCorrect () throws Exception {
//
//    }
//
//    @Test
//    public void calculateNumberOdSectorCorrect () throws Exception {
//        int result = windStatistics.calculateNumberOfSector(70);
//    }

}
