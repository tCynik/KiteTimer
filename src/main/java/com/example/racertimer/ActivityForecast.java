package com.example.racertimer;

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

import org.json.JSONException;
import org.json.JSONObject;

// для простоты обработки одинаковых данных табличные TextView обьявляем как массивы
public class ActivityForecast extends AppCompatActivity {
    private final static String PROJECT_LOG_TAG = "racer_timer - ActivityForecast";
    final String BROADCAST_ACTION = "com.example.racertimer.action.new_location"; // значение для фильтра приемника
    private Context context;


    private TextView[] timeTV = new TextView[5];
    private TextView[] tempTV = new TextView[5];
    private TextView[] windTV = new TextView[5];
    private TextView[] windDirTV = new TextView[5];

    private Button updateButton;

    private ForecastManager forecastManager;

    private BroadcastReceiver locationBroadcastReceiver;
    private IntentFilter locationIntentFilter;
    private Location location = null;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        context = this;
        // все окна времени
        timeTV[0] = findViewById(R.id.string0_time);
        timeTV[1] = findViewById(R.id.string1_time);

        // все окна температуры
        tempTV[0] = findViewById(R.id.string0_temp);
        tempTV[1] = findViewById(R.id.string1_temp);

        // все окна скорости ветра
        windTV[0] = findViewById(R.id.string0_wind);
        windTV[1] = findViewById(R.id.string1_wind);

        // все окна направления ветра
        windDirTV[0] = findViewById(R.id.string0_wind_dir);
        windDirTV[1] = findViewById(R.id.string1_wind_dir);

        // создаем слушатель для получения геоданных
        initBroadcastListener();

        // создаем хендер для получения Jsonа и направления его на исполнение при получении ответа
        createHandler(); // для простоты выненсен в отдельный метод

        // экземпляр класса, создающего и отправляющего запрос
        forecastManager = new ForecastManager(handler); // присваиваем экземпляру forecastManager'а наш хендлер
        if (location != null) { // если есть геолокация,
            forecastManager.updateForecast(location); // обновляем данные про
        updateButton = findViewById(R.id.button_update_forecast);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (location != null) {
                    forecastManager.updateForecast(location);
                Toast.makeText(context, "Forecast updated", Toast.LENGTH_LONG).show();
                }
                else Toast.makeText(context, "No location info", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createHandler () {
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
                        forecastManager.updateForecast(location); // обновляем прогноз
                    }
                }
            }
        };
        locationIntentFilter = new IntentFilter(BROADCAST_ACTION); // прописываем интент фильтр для слушателя
        registerReceiver(locationBroadcastReceiver, locationIntentFilter); // регистрируем слушатель
    }

    private void onJSONUpdated (JSONObject jsonObject) { // обработка полученного обьекта
////////////////// вот сюда дописать обработку Json со всеми вытекающими.
    }
}

///// может быть вообще тогда структуру сделать так:
///// отдельный публичный класс запрашиватель ЮРЛ. Ему в конструктор передаем локацию (как сделано ниже)
///// у него вложенный класс ассинктаск
///// этот класс получает на вход локацию и формирует в отдельном потоке юрл запрос, и обратно через хендлер посылает
///// посылает что? JSON? отдельыне окна? Наподумать!
