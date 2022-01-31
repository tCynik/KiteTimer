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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.racertimer.Instruments.ForecastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

// для простоты обработки одинаковых данных табличные TextView обьявляем как массивы
public class ActivityForecast extends AppCompatActivity {
    private final static String PROJECT_LOG_TAG = "racer_timer, ActivityForecast";
    final String BROADCAST_ACTION = "com.example.racertimer.action.new_location"; // значение для фильтра приемника
    private Context context;

    private int numberForecastStrings = 3; // сколько у нас отображается периодов прогноза
    // !!!!! при корректировке поправить Findview для предотвращения выхода за предлы массива
    ////////// изменить формат вьюшки для отображения большой таблицы произвольной длинны

    private TextView[] timeTV = new TextView[numberForecastStrings];
    private TextView[] tempTV = new TextView[numberForecastStrings];
    private TextView[] windTV = new TextView[numberForecastStrings];
    private TextView[] windDirTV = new TextView[numberForecastStrings];

    private Button updateButton;

    private ForecastManager forecastManager;

    private BroadcastReceiver locationBroadcastReceiver;
    private IntentFilter locationIntentFilter;
    private Location location = null;
    private double latitude, longitude;
    private boolean flagForecastIsAlreadyUpdated = false;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        context = this;
        // все окна времени
        timeTV[0] = findViewById(R.id.string0_time);
        timeTV[1] = findViewById(R.id.string1_time);
        timeTV[2] = findViewById(R.id.string2_time);

        // все окна температуры
        tempTV[0] = findViewById(R.id.string0_temp);
        tempTV[1] = findViewById(R.id.string1_temp);
        tempTV[2] = findViewById(R.id.string2_temp);

        // все окна скорости ветра
        windTV[0] = findViewById(R.id.string0_wind);
        windTV[1] = findViewById(R.id.string1_wind);
        windTV[2] = findViewById(R.id.string2_wind);

        // все окна направления ветра
        windDirTV[0] = findViewById(R.id.string0_wind_dir);
        windDirTV[1] = findViewById(R.id.string1_wind_dir);
        windDirTV[2] = findViewById(R.id.string2_wind_dir);

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

    private void onJSONUpdated (JSONObject jsonObject) throws JSONException { // обработка полученного обьекта
////////////////// вот сюда дописать обработку Json со всеми вытекающими.
        Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + ", Json is updated" );
        int numberForecastPeriods = 40; // переменная о количестве периодов прогноза (8 раз в сут * 5 сут)
        long[] times = new long[numberForecastPeriods];
        int[] winds = new int[numberForecastPeriods];
        double[] speeds = new double[numberForecastPeriods];
        double[] gusts = new double[numberForecastPeriods];
        double[] tempers = new double[numberForecastPeriods];
        int currentTVStringNumber = 0;


        Date date; // переменная для перевода long в дату
        // формат для отображения даты
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        Date currentDateTime = new Date(); // текущее время и дата в милисекундах

        // сначала вытаскиваем массив прогноза из Json
        JSONArray jsonArray =jsonObject.getJSONArray("list");

        // перебираем строчки прогноза и вытаскиваем конкретные данные для каждой текущей строчки
        for (int i = 0; i < numberForecastPeriods; i ++ ) {
            Log.i(PROJECT_LOG_TAG, "parsing forecast string #" + i );
//+
            int j = 0; // переменная для исп периоа времени перед текущим

            Log.i(PROJECT_LOG_TAG, "long = " + jsonArray.getJSONObject(i).getLong("dt"));

            times[i] = jsonArray.getJSONObject(i).getLong("dt") * 1000;
            winds[i] = jsonArray.getJSONObject(i).getJSONObject("wind").getInt("deg");
            speeds[i] = jsonArray.getJSONObject(i).getJSONObject("wind").getInt("speed");
            gusts[i] = jsonArray.getJSONObject(i).getJSONObject("wind").getDouble("gust");
            tempers[i] = jsonArray.getJSONObject(i).getJSONObject("main").getInt("temp");

            Log.i(PROJECT_LOG_TAG, ", times = " + times [i]);
            date = new Date (times [i]); // переводим long в формат даты
            Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + ", iter. = " + i + ", time is - " + date);

            // работаем только с периодами, близкими к текущему (для старых прогнозов)
            if (date.after(currentDateTime)) { // выясняем, что дата прогноза близка к текущей
                if (i > 0) { // если началось сразу с 0, продолжаем с 0
                    j = i - 1; // если нет - с предыдущего периода
                    Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + ", writing TV string #" + currentTVStringNumber );

                    // заполняем поочередно все TV
                    timeTV[currentTVStringNumber].setText(String.valueOf(format.format(date)));
                    tempTV[currentTVStringNumber].setText(String.valueOf(tempers[j]));
                    windTV[currentTVStringNumber].setText(String.valueOf(speeds[j]));
                    windDirTV[currentTVStringNumber].setText(String.valueOf(winds[j]));
                    currentTVStringNumber++;
                }
            }

            // если заполнили все TV, заказнчиваем заполнение
            if (currentTVStringNumber == numberForecastStrings) { // если заполнили все TV - выходим
                break;
            }

        }
    }
}