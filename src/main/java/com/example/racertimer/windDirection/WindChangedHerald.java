package com.example.racertimer.windDirection;

public interface WindChangedHerald { // глашатай изменения ветра для публикации новых данных
    void onWindDirectionChanged(int windDirection);
}
