package com.example.racertimer.windDirection;

public interface WindChangedHeraldInterface { // глашатай изменения ветра для публикации новых данных
    void onWindDirectionChanged(int windDirection, WindProvider provider);
}

