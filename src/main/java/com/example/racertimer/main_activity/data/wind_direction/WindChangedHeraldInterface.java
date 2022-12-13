package com.example.racertimer.main_activity.data.wind_direction;

public interface WindChangedHeraldInterface { // глашатай изменения ветра для публикации новых данных
    void onWindDirectionChanged(int windDirection, WindProvider provider);
}

