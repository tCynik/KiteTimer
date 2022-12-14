package com.tcynik.racertimer.main_activity.data.wind_direction;

import android.os.Parcel;
import android.os.Parcelable;

public enum WindProvider implements Parcelable {
    // взможные статусы (источники) данных о направлении ветра по мере повышения приоритета:

    DEFAULT,  // нет информации по ветру, значение дефолтное - красный
    HISTORY, // историческое значение с прошлой сессии - красный
    FORECAST, // значение из прогноза - зеленый
    CALCULATED, // значение высчитано калькулятором сегодня - белый
    MANUAL // выставлено вручную - голубой
    ;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
