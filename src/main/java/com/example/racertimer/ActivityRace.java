package com.example.racertimer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.racertimer.Instruments.CoursesCalculator;
import com.example.racertimer.Instruments.LocationService;
import com.example.racertimer.Instruments.ManuallyWind;
import com.example.racertimer.multimedia.Voiceover;

public class ActivityRace extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener, ForecastFragment.OpenerTimerInterface, TimerFragment.CloserTimerInterface { // добавить интерфейс
    private final static String PROJECT_LOG_TAG = "racer_timer";
    final String BROADCAST_ACTION = "com.example.racertimer.action.new_location"; // значение для фильтра приемника

    private SeekBar bearingSB, velocitySB;
    private Button btnReset, btnUpdateWind, btnMenu;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch muteVmgSwitch;
    private boolean windDirectionGettedFromService = false; // флаг того, что уже были получены данные по направлению ветра

    private TimerFragment timerFragment = null;
    private ForecastFragment forecastFragment = null;
    private SailingToolsFragment sailingToolsFragment = null;

    private Voiceover voiceover;

    private int velocity, bearing, windDirection;// !!!ПРОВЕРИТЬ ПУСТЫШКИ

    private TextView textTime; // переменная времени в левом вехнем углу

    private String timerString = "00:00.00"; // переменная для вывода текущего секундомера чч:мм:сс.сот
    private int timerHour = 0; // переменная в часах
    private int timerMin = 0; // переменная счетчика в минутах
    private int timerSec = 0; // текущее значение таймера в сотых долей секунды

    private double latitude = 0;
    private double longitude = 0; // координаты для получения прогноза
    private Location location = null; // текущее положение

    private Intent intentLocationService; // интент для создания сервиса геолокации
    private BroadcastReceiver locationBroadcastReceiver;
    private IntentFilter locationIntentFilter;

    private LocationService locationService;
    private ServiceConnection serviceConnection;
    private Binder binder;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);
////////// вынеси определение вьюшек в отдельный метод
        bearingSB = findViewById(R.id.seekBar_bearing);
        velocitySB = findViewById(R.id.seekBar_velosity);
        btnReset = findViewById(R.id.but_reset);
        muteVmgSwitch = findViewById(R.id.mute_vmg);
        btnUpdateWind = findViewById(R.id.update_wind);
        btnMenu = findViewById(R.id.btn_menu);

        // TODO: нужно генерировать линии best VMG программно по заданным координатам.

        windDirection = 202;

        textTime = findViewById(R.id.currentTime);

        context = this;
        forecastFragment = new ForecastFragment();

        /** запускаем таймер */
        timerRunning(); // запускаем отсчет и обработку таймера

        /** блок работы с геоданными */
        createLocationService();

        voiceover = new Voiceover(context);

        deploySailingToolsFragment();

        /** обрабатываем свитч mute VMG */
        muteVmgSwitch.setOnCheckedChangeListener(this);

//// потом перепишу слушатели кнопок в единый блок кода. Кнопок добавится много, в т.ч поля
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("racer_timer", "btn menu1 was pressed ");

                openOptionsMenu();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sailingToolsFragment.resetPressed();
                Log.i("racer_timer", "reset VMG maximums");
            }
        });

        /** обрабатываем кнопку обновления данных по направлени ветра */
        btnUpdateWind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //locationService.updateWindDirection();
                int wind = locationService.getWindDirection();
                if (wind != 10000) {
                    Log.i("racer_timer", "getted actual wind dir");
                    onWindDirectionChanged(wind);
                }
            }
        });

        bearingSB.setOnSeekBarChangeListener(this);
        velocitySB.setOnSeekBarChangeListener(this);

        //sailingToolsFragment.setVelocity(2)
    }

    @Override
    protected void onResume() { // при восстановлении окна автоматически запрашиваем данные по ветру
        super.onResume();
        if (location == null) {
            initBroadcastListener(); // запускаем слушатель новых геоданных
            bindToLocationService();
        }

        if (locationService != null) locationService.updateWindDirection();
    }

    /** модуль методов выгрузки фрагментов */
    public void deployTimerFragment() { // создание фрагмента для таймера
        if (timerFragment == null) timerFragment = new TimerFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fr_place_timer_forecast, timerFragment);
        fragmentTransaction.commit();
    }

    public void deployForecastFragment() { // создание фрагмента для прогноза
        if (forecastFragment == null) forecastFragment = new ForecastFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fr_place_timer_forecast, forecastFragment);
        fragmentTransaction.commit();
    }

    public void deploySailingToolsFragment () { // механизм выгрузки фрагмента центральных элементов
        if (sailingToolsFragment == null) sailingToolsFragment = new SailingToolsFragment();
        sailingToolsFragment.setVoiceover(voiceover); // передаем экземпляр озвучки
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fr_sailing_tools, sailingToolsFragment);
        fragmentTransaction.commit();
    }

    public Location getCurrentLocation () {
        return location;
    }

    /** бегунки тестирования вьюшки курсов и скоростей */
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar == bearingSB) {
            bearing = i;
            sailingToolsFragment.onBearingChanged(bearing);
        }
        if (seekBar == velocitySB) {
            velocity = i;
            sailingToolsFragment.onVelocityChanged(velocity);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
//        Log.i(PROJECT_LOG_TAG, "heigh = " + constraintLayout.getHeight() + ", widht = "+ constraintLayout.getWidth());

    }

    /** Отработка нажатия кнопки "Назад" */
    @Override
    public void onBackPressed() { // в случае нажатия кнопки назад диалег по переходу в главное меню
        AlertDialog.Builder confurmingRaceEnd = new AlertDialog.Builder(this); // строитель диалога
        confurmingRaceEnd.setMessage("End the race?")
                .setCancelable(false) // не отменяемый (при нажатии вне поля диалога не закрывается)
                // назначаем кнопки взаимодействия
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        stopRace();
                        finish(); // закрываем эту активити
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = confurmingRaceEnd.create(); // создание диалога
        alertDialog.setTitle("Ending the race"); // заголовок
        alertDialog.show(); // отображение диалога
    }

    /** блок меню */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("racer_timer", "starting menu1... ");
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
        //return super.onCreateOptionsMenu(menu1);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void stopRace() { // остановка гонки
        super.onBackPressed();
    }

    /** усреднитель курса и обработчик перехода через нулевой азимут */
    private int courseAverage (int newCourse) {
        int deltaCourse = (newCourse - bearing); // разница курсов: "курс новый (newCourse) - курс старый (course)"
        if (deltaCourse > 180) deltaCourse = deltaCourse - 360; //newCourse - (360  - course);
        if (deltaCourse < -180) deltaCourse = 360 + deltaCourse;

        bearing = (int) (bearing + (deltaCourse * 0.75)) ; // усреднение - приращиваем на 75% от разницы
        bearing = CoursesCalculator.convertAngleFrom0To360(bearing);
        Log.i("ActivityRace", "averageCourse = " + bearing);
        return bearing;
    }

    // TODO: вот эту срань с таймером переносим во фрагмент прогноза.
    /** Счетчик таймера*/
    private void timerRunning () {
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                onTimerTicked();
            }
            @Override
            public void onFinish() {
                timerRunning();
            }
        }.start();
    }

    /** обработка изменения таймера*/
    private void onTimerTicked () {
        timerSec++;
        if (timerSec > 59) {
            timerSec -= 60;
            timerMin ++;
        }
        if (timerMin == 60) {
            timerMin = 0;
            timerHour ++;
        }
        timerString = calcTimer(timerHour, timerMin, timerSec);
//        timerRace.setText(timerString.toString());
    }

    /** Калькулятор гоночного таймера */
    private String calcTimer (int timerHour, int timerMin, int timerSec) {
//        timerString = timerSec / 10+ "." + (int) timerSec % 10; // тут таймер с сотыми секунды, но там время неверное
//        if (timerSec < 100 ) timerString = "" + 0 + timerString;
//        timerString = timerMin + ":" + timerString;
        if (timerSec < 10) timerString = timerMin + ":0" + timerSec;
        else timerString = timerMin + ":" + timerSec;
        if (timerMin < 10 ) timerString = "0" + timerString;
        if (timerHour !=0 ) timerString = timerHour + ":" + timerString;
        return timerString;
    }

    /** Настраиваем и запускаем сервис для приема и трансляции данных геолокации */
    private void createLocationService() {
        if (! checkPermission()) askPermission(); // если разрешения нет, запрашиваем разрешение

        if (checkPermission()) { // еще раз проверяем: если разрешение есть, запускаем сервис
            Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + " permission good, starting service ");
            intentLocationService = new Intent(this, LocationService.class);
            intentLocationService.setPackage("com.example.racertimer.Instruments");
            this.startService(intentLocationService);
        } else { // если разрешения нет, выводим тост
            Toast.makeText(this, "No GPS permission", Toast.LENGTH_LONG);
        }
    }

    /** Методы для работы с разрешениями на геолокацию */
    public boolean checkPermission() { // проверяем наличие разрешения на геоданные
        // если разрешения нет:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&  // если версия СДК выше версии M (API 23)
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) // если разрешения нет, то запускаем запрос разрешения, код ответа 100
        {
            return false; // если разрешения нет, возвращаем false
        } else
            return true; // в противном случае разрешение есть, возвращаем true
    }
    private void askPermission() { // запрос разрешения
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, // запрашиваем разрешение
                Manifest.permission.ACCESS_FINE_LOCATION}, 100); // ключ 100, такой же как ниже
    }

    /** биндимся к сервису для управления им */
    private void bindToLocationService() {
        Log.i("racer_timer", "Making service connection... " );
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.i("racer_timer", "Location service binded " );
                binder = (LocationService.MyBinder) iBinder; // приводим биндер к кастомному биндеру с методом связи
                locationService = ((LocationService.MyBinder) binder).getService(); // получаем экземпляр нашего сервиса через биндер
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.i("racer_timer", "location service disconnected " );

            }
        };

        bindService(intentLocationService, serviceConnection, BIND_EXTERNAL_SERVICE);
    }

    /** создаем и регистрируем слушатель геолокации */
    private void initBroadcastListener() {
        locationBroadcastReceiver = new BroadcastReceiver() { // создаем broadcastlistener
            @Override
            public void onReceive(Context context, Intent intent) { // обработка интента
                if (intent.hasExtra("location")) { // если в сообщении есть геолокация
                    Location location = (Location) intent.getExtras().get("location");
                    processorChangedLocation(location); // отдаем точку на обработку в процессор
                    Log.i("racer_timer", "getted location broadcast from locationService, " +
                            "new velocity = " + (int)((Location) intent.getExtras().get("location")).getSpeed());
                }
                if (intent.hasExtra("windDirection")) {
                    int windDirectionFromExtra = (int) intent.getExtras().get("windDirection");
                    if (windDirectionFromExtra != 10000) {
                        onWindDirectionChanged((int) intent.getExtras().get("windDirection"));
                        windDirectionGettedFromService = true;
                        Log.i("racer_timer", "getted wind broadcast from locationService, new windDir = " + intent.getExtras().get("windDirection"));
                    }
                }
            }
        };
        locationIntentFilter = new IntentFilter(BROADCAST_ACTION); // прописываем интент фильтр для слушателя
        registerReceiver(locationBroadcastReceiver, locationIntentFilter); // регистрируем слушатель
    }

    public void onWindDirectionChanged (int updatedWindDirection) { // смена направления ветра
        windDirection = updatedWindDirection;
        sailingToolsFragment.onWindDirectionChanged(updatedWindDirection);
    }

    public void manuallyWindManager () { // установка направления ветра вручную
        Log.i("racer_timer_activity_race", " starting manually setting wind  ");
        ManuallyWind manuallyWind = new ManuallyWind(this, windDirection);
        manuallyWind.showView();
    }

    /** обработка вновь полученных геолокации */
    private void processorChangedLocation (Location location) { // обработчик новой измененной позиции
        Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + " Activity race get new location ");
        double tempVelocity;
        if (latitude == 0 & longitude == 0) { // если это первое получение геолокации
            this.location = location;
            latitude = location.getLatitude();
            longitude = location.getLongitude();

//            Log.i(PROJECT_LOG_TAG+"_coord", "location coordinates: latitude= "+latitude + ", longitude = " + longitude);
//            forecastFragment.setCoordinates(latitude, longitude); // даем его в прогноз погоды
        }
//        latitude = location.getLatitude();
//        longitude = location.getLongitude();
//
//        Log.i(PROJECT_LOG_TAG+"_coord", "location coordinates: latitude= "+latitude + ", longitude = " + longitude);


        if (location.hasSpeed()) { // если есть скорость
            tempVelocity = (double) location.getSpeed()*3.6;
            velocity = (int) tempVelocity;
            Log.i("racer_timer", " sending velocity = "+ velocity);
            if (sailingToolsFragment != null) sailingToolsFragment.onVelocityChanged(velocity);
        } else sailingToolsFragment.onVelocityChanged(0);
        bearing = courseAverage((int) location.getBearing()); // с учетом усреднения
        if (sailingToolsFragment != null) sailingToolsFragment.onBearingChanged(bearing);
    }

    @Override
    public void finishTheTimer() {
        deployForecastFragment();
    }

    @Override
    public void openTimerFragment() {
        deployTimerFragment();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        sailingToolsFragment.muteChangedStatus(b);
    }

}


// TODO: сделать главное меню, где назначаем варианты определения ветра:
//       установка только вручную; установка по сравнению; установка по статистике

// TODO: организовать управление нахождения ветра:
//       если началась гонка, включаем запуск сравнения, если нет данных по ручному ветру -
// исходим из того, что у нас правый бейдевинд
//       либо запускаем если выбран чек поле "запуск сравнения"

