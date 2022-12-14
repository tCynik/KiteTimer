package com.tcynik.racertimer.forecast.data.models;

import java.io.Serializable;
import java.util.ArrayList;

/** сериализуемый список локаций прогноза для поледующего сохранения и загрузки через бинарный файл */
public class ListForecastLocations extends ArrayList<LocationForecast> implements Serializable {
}
