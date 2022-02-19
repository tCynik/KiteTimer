package com.example.racertimer.Instruments;

public interface WindChangedHerald { // глашатай изменения ветра для публикации новых данных
    void onWindDirectionChanged(int windDirection);
}
