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
import com.example.racertimer.Instruments.geoLocation.ListForecastLocations;
import com.example.racertimer.Instruments.geoLocation.LocationForecast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

// для простоты обработки одинаковых данных табличные TextView обьявляем как массивы
public class ActivityForecast extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private final static String PROJECT_LOG_TAG = "racer_timer, ActivityForecast";
    final String BROADCAST_ACTION = "com.example.racertimer.action.new_location"; // значение для фильтра приемника
    private Context context;

    private TextView timeTV, tempTV, windDirTV, windSpeedTV, windGustTV;

    private Button updateButton, selectLocationButton;

    private ForecastManager forecastManager;

    private BroadcastReceiver locationBroadcastReceiver;
    private IntentFilter locationIntentFilter;
    private Location location = null;
    private ListForecastLocations listLocationForecast; // сериализуемый список наследованный от ArrayList<>
    private double latitude, longitude;
    private boolean flagForecastIsAlreadyUpdated = false;

    private LinearLayout layoutToBeFilled;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        context = this;

        layoutToBeFilled = (LinearLayout) findViewById(R.id.listView); // пустая линия, в которой будем надувать таблицу
        selectLocationButton = findViewById(R.id.btn_select_location);

        Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + ", ForecastActivity is working " );

        // создаем хенлдер для получения Jsonа и направления его на исполнение при получении ответа
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

        // загружаем из сериализации файл locations_forecast.bin из папки saved
// TODO: здесь и далее - создание листа точек для геолокации. После сериализации - удалить!
//        listLocationForecast = new ListForecastLocations();
//        LocationForecast krasnoyarsk = new LocationForecast("Krasnoyarsk", 56.02698, 92.94564833333334);
//        LocationForecast shumiha = new LocationForecast("Shumiha", 55.91477, 92.27641999999999);
//        LocationForecast obskoe = new LocationForecast("Obskoe Sea", 54.82607500000001, 83.02941833333334);
//        listLocationForecast.add(krasnoyarsk);
//        listLocationForecast.add(shumiha);
//        listLocationForecast.add(obskoe);
//        listNames(listLocationForecast);
//        uploadListForecastLocations(listLocationForecast); // записываем список локаций в бинарный файл
//        listLocationForecast.clear();

        listLocationForecast = downloadListForecastLocations(); // загружаем список локаций из бинарного файла
//        listNames(listLocationForecast);

    }

    /** сериализация списка локаций для прогноза */
    private void uploadListForecastLocations (ListForecastLocations listLocationForecast) {
        Log.i("racer_timer, loc list serialization", " started uploading ");
        try { // записываем обьект список локаций в файл
            FileOutputStream fileOutputStream = context.openFileOutput("saved.locations_list.bin", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream( fileOutputStream);
            Log.i("racer_timer, loc list serialization", " uploading stream is launched ");
            objectOutputStream.writeObject(listLocationForecast);
            Log.i("racer_timer, loc list serialization", " object was writed ");
            objectOutputStream.close();
            fileOutputStream.close();
            Log.i("racer_timer, loc list serialization", " location saved ");
        } catch (IOException e) {
            Log.i("racer_timer, loc list serialization", " location was not saved = "+e);
            e.printStackTrace();
        }
    }

    private ListForecastLocations downloadListForecastLocations () {
        try {
            FileInputStream fileInputStream = context.openFileInput("saved.locations_list.bin");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            listLocationForecast = (ListForecastLocations) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();

            Log.i("racer_timer, loc list serialization", " location downloaded ");
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "No saved locations", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error while reading locations list", Toast.LENGTH_LONG).show();
        } catch (ClassNotFoundException e) {
            Toast.makeText(this, "Saved locations list read error", Toast.LENGTH_LONG).show();
        }
        return listLocationForecast;
    }
    
    private void listNames (ListForecastLocations arrayList) {
        for (LocationForecast locationForecst: arrayList
             ) {
            String name = locationForecst.getName();
            Log.i("racer_timer, loc list serialization", " the name of location is "+name);
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

    /** выбор точки, для которой смотрим прогноз */
    public void selectLocation(View view) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.choose_location_layout);

        // модуль инфлейтора для создания попап меню по загруженному содерданию
        LayoutInflater layoutInflater = getLayoutInflater(); // создаем инфлейтор
        for (int i = 0; i < listLocationForecast.size(); i++) { // перебираем все загруженные локации
            LocationForecast locationForecast = listLocationForecast.get(i);
            String name = locationForecast.getName();
            popup.getMenu().add(0, i, 0, name); // добавлям пункт в меню: 0, id, 0, имя
        }
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        boolean flagReturn = false;
        if (menuItem.getItemId() == R.id.current) { // если выбрана текущая локация
            Toast.makeText(this, "selected current location is current", Toast.LENGTH_SHORT).show();
            flagReturn = true;
        } else { // если выбрана не текущая локация, а другая
            for (int i = 0; i < listLocationForecast.size(); i++) { // перебираем все варианты и сравниваем
                if (i == menuItem.getItemId()) {
                    Toast.makeText(this, "selected current location is " + listLocationForecast.get(i).getName(), Toast.LENGTH_SHORT).show();
                    layoutToBeFilled.removeAllViewsInLayout();
                    selectLocationButton.setText(listLocationForecast.get(i).getName());
                    latitude = listLocationForecast.get(i).getLatitude();
                    longitude = listLocationForecast.get(i).getLongitude();
                    forecastManager.updateForecast(latitude, longitude);
                    flagReturn = true;
                }
            }
        }
        return flagReturn;
        // TODO: сделать реализацию вызова прогноза для выбранного варианта
        // показывать прогноз только после выбора одного из вариантов
        // на будущее: длинный тап - создание либо удаление локации из памяти. Создание для текущей позиции
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

//        LayoutInflater layoutInflater = getLayoutInflater();

        SimpleDateFormat timeFormat = new SimpleDateFormat("d MMM HH:mm"); // определяем формат отображения времени
        fillForecastLine("day time", "temp", "wing", "gust", "w.dir"); // шапка

        // перебираем строчки прогноза и вытаскиваем конкретные данные для каждой текущей строчки
        for (int i = 0; i < numberForecastPeriods; i ++ ) {
            Log.i(PROJECT_LOG_TAG, "parsing forecast string #" + i );
            // разбираем JSON на поля, заполняем
            time = jsonArray.getJSONObject(i).getLong("dt") * 1000;
            temperature = jsonArray.getJSONObject(i).getJSONObject("main").getInt("temp");
            windDirection = jsonArray.getJSONObject(i).getJSONObject("wind").getInt("deg");
            windSpeed = jsonArray.getJSONObject(i).getJSONObject("wind").getInt("speed");
            windGust = jsonArray.getJSONObject(i).getJSONObject("wind").getDouble("gust");

            // преобразуем все поля в String
            String timeString = timeFormat.format(new Date (time));
            String tempString = String.valueOf(temperature);
            String windDirString = String.valueOf(windDirection);
            String windSpeedString = String.valueOf(windSpeed);
            String windGustString = String.valueOf(windGust);

            fillForecastLine(timeString, tempString, windSpeedString, windGustString, windDirString);

        }
    }

    private void fillForecastLine (String time, String temp, String windSpeed, String windGust, String windDir) {
        LayoutInflater layoutInflater = getLayoutInflater();

        View item = layoutInflater.inflate(R.layout.forecast_line, layoutToBeFilled, false);

        // заполняем arrayList, создавая конструктором новый экземпляр с полученными полями
        timeTV = (TextView) item.findViewById(R.id.forecast_string_time);
        timeTV.setText(time);

        tempTV = (TextView) item.findViewById(R.id.forecast_string_temp);
        tempTV.setText(temp);

        windSpeedTV = (TextView) item.findViewById(R.id.forecast_string_wind);
        windSpeedTV.setText(windSpeed);

        windGustTV = (TextView) item.findViewById(R.id.forecast_string_gust);
        windGustTV.setText(windGust);

        windDirTV = (TextView) item.findViewById(R.id.forecast_string_dir);
        windDirTV.setText(windDir);

        layoutToBeFilled.addView(item);
    }
// TODO: выделить цветом ночные часы
//  реализовать отработку поворота экрана
}

