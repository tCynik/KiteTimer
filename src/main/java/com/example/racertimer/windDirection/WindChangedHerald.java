package com.example.racertimer.windDirection;

import com.example.racertimer.Instruments.WindProvider;

public interface WindChangedHerald { // глашатай изменения ветра для публикации новых данных
    void onWindDirectionChanged(int windDirection, WindProvider provider);
}

