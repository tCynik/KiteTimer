package com.example.racertimer;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.racertimer.Instruments.ForecastManager;
import com.example.racertimer.Instruments.geoLocation.LocationForecast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// для простоты обработки одинаковых данных табличные TextView обьявляем как массивы
public class ActivityForecast extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private final static String PROJECT_LOG_TAG = "racer_timer, ActivityForecast";
    final String BROADCAST_ACTION = "com.example.racertimer.action.new_location"; // значение для фильтра приемника
    private Context context;

    private TextView timeTV, tempTV, windDirTV, windSpeedTV, windGustTV;

    private Button updateButton;

    private ForecastManager forecastManager;

    private BroadcastReceiver locationBroadcastReceiver;
    private IntentFilter locationIntentFilter;
    private Location location = null;
    private ArrayList<LocationForecast> listLocationForecast;
    private double latitude, longitude;
    private boolean flagForecastIsAlreadyUpdated = false;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        context = this;

        Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + ", ForecastActivity is working " );

        // создаем хендер для получения Jsonа и направления его на исполнение при получении ответа
        createHandler(); // для простоты выненсен в отдельный метод
        // экземпляр класса, создающего и отправляющего запрос
        forecastManager = new ForecastManager(handler); // присваиваем экземпляру forecastManager'а наш хендлер

        /** если есть, принимаем даныне по местоположению из вывавшего интента */
        Intent catchLocation = getIntent();
        if (catchLocation.hasExtra("latitude") & catchLocation.hasExtra("longitude")) {
            latitude = catchLocation.getDoubleExtra("latitude", 0);
            longitude = catchLocation.getDoubleExtra("longitude", 0);
            flagForecastIsAlreadyUpdated = true;
            forecastManager.updateForecast(latitude, longitude); // обновляем данные прогноза
        }
        // создаем слушатель для получения геоданных
        initBroadcastListener();

        if (location != null & !flagForecastIsAlreadyUpdated) { // если есть геолокация и пока не обновлялись,
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            forecastManager.updateForecast(latitude, longitude); // обновляем данные прогноза
            updateButton = findViewById(R.id.button_update_forecast);
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        forecastManager.updateForecast(latitude, longitude);
                        Toast.makeText(context, "Forecast updated", Toast.LENGTH_LONG).show();
                    } else Toast.makeText(context, "No location info", Toast.LENGTH_LONG).show();
                }
            });
        }
        // загружаем из сериализации файл locations_forecast.bin

        // TODO: здесь и далее - создание листа точек для геолокации. Псоле сериализации - удалить!
        LocationForecast krasnoyarsk = new LocationForecast("Krasnoyarsk", 56.02698, 92.94564833333334);
        LocationForecast shumiha = new LocationForecast("Shumiha", 55.91477, 92.27641999999999);
        LocationForecast obskoe = new LocationForecast("Obskoe Sea", 54.82607500000001, 83.02941833333334);
        listLocationForecast.add(krasnoyarsk);
        listLocationForecast.add(shumiha);
        listLocationForecast.add(obskoe);
        // дальше сериализуем наш лист в файл locations_forecast.bin

    }

    private void createHandler() {
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) { // при получении сообщения handler (в виде String)
                super.handleMessage(msg);
                Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + " what: "+ msg.what );
                Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + " msg: "+ msg.obj );
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(msg.obj)); // превращаем его в Json
                    onJSONUpdated (jsonObject); // отправляем на обработку
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /** создаем и регистрируем слушатель геолокации */
    private void initBroadcastListener() {
        locationBroadcastReceiver = new BroadcastReceiver() { // создаем broadcastlistener
            @Override
            public void onReceive(Context context, Intent intent) { // обработка интента
                if (intent.hasExtra("location")) { // если в сообщении есть геолокация
                    if (location == null) { // если это первое значение локации,
                        location = (Location) intent.getExtras().get("location");
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        forecastManager.updateForecast(latitude, longitude);
                        Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + " location getted ");
                    }
                }
            }
        };
        locationIntentFilter = new IntentFilter(BROADCAST_ACTION); // прописываем интент фильтр для слушателя
        registerReceiver(locationBroadcastReceiver, locationIntentFilter); // регистрируем слушатель
    }

    /** выбор точки, для которой смотрим прогноз */
    public void selectLocation(View view) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.choose_location_layout);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.current:
                Toast.makeText(this, "selected current location", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.location_krsk:
                Toast.makeText(this, "selected Krasnoyarsk", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.location_shumiha:
                Toast.makeText(this, "selected Shumiha", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.location_obskoe:
                Toast.makeText(this, "selected Obskoe sea", Toast.LENGTH_SHORT).show();
                return true;
            default: return false;
        }
    }

    private void onJSONUpdated (JSONObject jsonObject) throws JSONException { // обработка полученного обьекта
        Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + ", Json is updated" );
        int numberForecastPeriods = 40; // переменная о количестве периодов прогноза (8 раз в сут * 5 сут)
        long time;
        int windDirection;
        double temperature, windSpeed, windGust;

        // формат для отображения даты
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        // сначала вытаскиваем массив прогноза из Json
        JSONArray jsonArray = jsonObject.getJSONArray("list");

        LinearLayout layoutToBeFulled = (LinearLayout) findViewById(R.id.listView);
        LayoutInflater layoutInflater = getLayoutInflater();

        SimpleDateFormat timeFormat = new SimpleDateFormat("d MMM HH:mm"); // определяем формат отображения времени

        // перебираем строчки прогноза и вытаскиваем конкретные данные для каждой текущей строчки
        for (int i = 0; i < numberForecastPeriods; i ++ ) {
            Log.i(PROJECT_LOG_TAG, "parsing forecast string #" + i );
            // разбираем JSON на поля, заполняем
            time = jsonArray.getJSONObject(i).getLong("dt") * 1000;
            temperature = jsonArray.getJSONObject(i).getJSONObject("main").getInt("temp");
            windDirection = jsonArray.getJSONObject(i).getJSONObject("wind").getInt("deg");
            windSpeed = jsonArray.getJSONObject(i).getJSONObject("wind").getInt("speed");
            windGust = jsonArray.getJSONObject(i).getJSONObject("wind").getDouble("gust");
            // заполняем arrayList, создавая конструктором новый экземпляр с полученными полями

            // из нашго XML файла готовим VIEW, используем его как образец.
            View item = layoutInflater.inflate(R.layout.forecast_string, layoutToBeFulled, false);

            timeTV = (TextView) item.findViewById(R.id.forecast_string_time);
            timeTV.setText(timeFormat.format(new Date (time)));

            tempTV = (TextView) item.findViewById(R.id.forecast_string_temp);
            tempTV.setText(String.valueOf(temperature));

            windDirTV = (TextView) item.findViewById(R.id.forecast_string_dir);
            windDirTV.setText(String.valueOf(windDirection));

            windSpeedTV = (TextView) item.findViewById(R.id.forecast_string_wind);
            windSpeedTV.setText(String.valueOf(windSpeed));

            windGustTV = (TextView) item.findViewById(R.id.forecast_string_gust);
            windGustTV.setText(String.valueOf(windGust));

            layoutToBeFulled.addView(item);
        }
    }

}