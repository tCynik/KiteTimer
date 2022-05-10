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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.racertimer.Instruments.CoursesCalculator;
import com.example.racertimer.Instruments.LocationService;
import com.example.racertimer.Instruments.ManuallyWind;
import com.example.racertimer.map.DrawView;
import com.example.racertimer.map.MapUITools;
import com.example.racertimer.map.TrackDrawerTranzister;
import com.example.racertimer.map.TrackPainterOnMap;
import com.example.racertimer.multimedia.Voiceover;

public class ActivityRace extends AppCompatActivity implements
        TimerFragment.CloserTimerInterface {

    private final static String PROJECT_LOG_TAG = "racer_timer";
    final String BROADCAST_ACTION = "com.example.racertimer.action.new_location"; // значение для фильтра приемника

    private Button btnReset, btnStopwach;
    private Button btnStartRecordTrack;
    private ImageButton btnMenu;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private boolean windDirectionGettedFromService = false; // флаг того, что уже были получены данные по направлению ветра

    private TimerFragment timerFragment = null;
    private MapFragment mapFragment = null;
    public MapUITools mapUITools;
    public SailingToolsFragment sailingToolsFragment = null;
    public MenuFragment menuFragment = null;
    public DeveloperFragment developerFragment = null;
    public FragmentContainerView menuPlace; // место, в котором возникает меню

    private TrackPainterOnMap trackPainterOnMap;
    private TrackDrawerTranzister trackDrawerTranzister;
    private DrawView trackDrawerView;

    private ImageView arrowDirectionOnMap, arrowWindOnMap;

    private Voiceover voiceover;

    private int velocity, bearing, windDirection;// !!!ПРОВЕРИТЬ ПУСТЫШКИ

    private int defaultMapScale = 1;

    private String timerString = "00:00.00"; // переменная для вывода текущего секундомера чч:мм:сс.сот
    private int timerHour = 0; // переменная в часах
    private int timerMin = 0; // переменная счетчика в минутах
    private int timerSec = 0; // текущее значение таймера в сотых долей секунды

    private double latitude = 0;
    private double longitude = 0; // координаты для получения прогноза
    private Location location = null; // текущее положение

    private boolean isRaceStarted = false; // флаг того то, происходит сейчас гонка

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
        btnMenu = findViewById(R.id.button_menu);
        btnStopwach = findViewById(R.id.stopwach);

        menuPlace = findViewById(R.id.fr_menu_place); // находим контейнер для дальнейшего размещения вьюшек

        // TODO: нужно генерировать линии best VMG программно по заданным координатам.

        windDirection = 202;

        context = this;

        mapFragment = new MapFragment();

        btnStartRecordTrack = findViewById(R.id.button_start);
        btnStartRecordTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trackPainterOnMap != null) trackPainterOnMap.beginNewTrackDrawing(location);
                Log.i("racer_timer_painter", "track drawing is beginning");

            }
        });

        /** запускаем таймер */
        timerRunning(); // запускаем отсчет и обработку таймера

        /** блок работы с геоданными */
        createLocationService();

        voiceover = new Voiceover(context);

        deploySailingToolsFragment();

        trackDrawerTranzister = new TrackDrawerTranzister() {
            @Override
            public void setDrawView(DrawView drawView) {
                trackDrawerView = drawView;
                Log.i("racer_timer_painter", "racer activity - transiting the drawView by callback");
            }
        };
        trackPainterOnMap = new TrackPainterOnMap(trackDrawerTranzister, context);

//// потом перепишу слушатели кнопок в единый блок кода. Кнопок добавится много, в т.ч поля
//        btnMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.i("racer_timer", "btn menu1 was pressed ");
//
//                openOptionsMenu();
//            }
//        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deployMenuFragment();
            }
        });

        btnStopwach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRaceStarted) { // если гонка идет, выводим диалоговое меню об остановке
                    // TODO: диалоговое меню "остановить гонку? да/нет"
                } else { // если гонка не идет, вызываем таймер
                    deployTimerFragment();
                }
            }
        });

        //sailingToolsFragment.setVelocity(2)
    }

    @Override
    protected void onResume() { // при восстановлении окна автоматически запрашиваем данные по ветру
        super.onResume();
        if (location == null) {
            initBroadcastListener(); // запускаем слушатель новых геоданных
            bindToLocationService();
        }
        //activateMapFragment();

        updateWindDirection();
    }

    public void uploadMapUIIntoTools (ConstraintLayout tracksLayout, ImageView arrowDirection, ImageView arrowWind, Button btnIncScale, Button btnDecScale) {
        mapUITools = new MapUITools(defaultMapScale);
        mapUITools.setUIViews(tracksLayout, arrowDirection, arrowWind, btnIncScale, btnDecScale);
        mapUITools.onWindChanged(CoursesCalculator.invertCourse(windDirection));
    }

    /** модуль методов выгрузки фрагментов */
    public void deployTimerFragment() { // создание фрагмента для таймера
        if (timerFragment == null) timerFragment = new TimerFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fr_place_map, timerFragment);
        fragmentTransaction.commit();
    }

    public void deployMapFragment() { // создание фрагмента для прогноза
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fr_place_map, mapFragment);
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

    public void deployMenuFragment () { // выгрузка фрагмена меню
        menuPlace.setVisibility(View.VISIBLE);
        if (menuFragment == null) menuFragment = new MenuFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fr_menu_place, menuFragment);
        fragmentTransaction.commit();
    }

    public void deployDeveloperTools () { // выгрузка фрагмена меню
        FragmentManager fragmentManager = getSupportFragmentManager();
        developerFragment = new DeveloperFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fr_menu_place, developerFragment);
        fragmentTransaction.commit();
    }

    public Location getCurrentLocation () {
        return location;
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
                        Log.i("racer_timer", "app is closing by user... ");
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
    public void closeMenu() {
        menuPlace.setVisibility(View.INVISIBLE);
    }

    //TODO: все что ниже - к прежнему меню. Сейчас перешел на кастомное, надо все будет убрать
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("racer_timer", "starting menu1... ");
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void updateWindDirection() { // получение ветра для событий, требующих этого
        if (locationService != null) locationService.updateWindDirection();
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

    /** блок работы со слушателем геолокации  */
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

    public void setArrowDirectionOnMap(ImageView imageView) {
        arrowDirectionOnMap = imageView;
    }

    public void setArrowWindOnMap (ImageView imageView) {
        arrowWindOnMap = imageView;
    }

    public void onWindDirectionChanged (int updatedWindDirection) { // смена направления ветра
        windDirection = updatedWindDirection;
        sailingToolsFragment.onWindDirectionChanged(updatedWindDirection);
        if (mapUITools != null) {
            Log.i(PROJECT_LOG_TAG, "changing the wind in the map to "+windDirection);
            mapUITools.onWindChanged(updatedWindDirection);
        }
    }

    public void manuallyWindManager () { // установка направления ветра вручную
        Log.i("racer_timer_activity_race", " starting manually setting wind  ");
        ManuallyWind manuallyWind = new ManuallyWind(this, windDirection);
        manuallyWind.showView();
    }

    /** обработка вновь полученных геолокации */
    private void processorChangedLocation (Location location) { // обработчик новой измененной позиции
        Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + ". Activity race get new location ");
        double tempVelocity;

        // TODO: прогноз открывается после получения локации? Нужно реализовать такой принцип, что
        //  если нет кординат, прогноз открывается для ранее использованной точки, а при
        //  попытке выбрать current location выходит тост, что нет связи со спутником
        if (latitude == 0 & longitude == 0) { // если это первое получение геолокации
            this.location = location;
            latitude = location.getLatitude();
            longitude = location.getLongitude();
//            Log.i(PROJECT_LOG_TAG+"_coord", "location coordinates: latitude= "+latitude + ", longitude = " + longitude);
//            forecastFragment.setCoordinates(latitude, longitude); // даем его в прогноз погоды
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();
//
        Log.i(PROJECT_LOG_TAG+"_coord", "location coordinates: latitude= "+latitude + ", longitude = " + longitude);


        if (location.hasSpeed()) {
            Log.i("racer_timer_painter", "sending location to trackpainter from main activity" );
            trackPainterOnMap.onLocatoinChanged(location);

            //mapFragment.locationIsChanged(location);
            tempVelocity = (double) location.getSpeed()*3.6;
            velocity = (int) tempVelocity;
            Log.i("racer_timer", " sending velocity = "+ velocity);
            if (sailingToolsFragment != null) sailingToolsFragment.onVelocityChanged(velocity);
            //mapFragment.locationIsChanged(location);
        } else sailingToolsFragment.onVelocityChanged(0);
        bearing = courseAverage((int) location.getBearing()); // с учетом усреднения
        if (sailingToolsFragment != null) sailingToolsFragment.onBearingChanged(bearing);
        if (mapUITools != null) mapUITools.onBearingChanged(bearing);
    }

    public void muteChangedStatus(boolean b) { // выключение звука пищалки
        sailingToolsFragment.muteChangedStatus(b);
    }

    public void startRace () { // изменение статуса, идет ли гонка
        this.isRaceStarted = true;
    }

    public void endRace () {
        this.isRaceStarted = false;
    }

    public void resetAllMaximums() {
        sailingToolsFragment.resetPressed();
        Log.i("racer_timer", "reset VMG maximums");
    }

    @Override
    public void finishTheTimer() {
        startRace();
        // TODO: при окончании таймера закрываем фрагмерт (или делаем контейнер прозрачным)
        //deployMapFragment();
    }

}


// TODO: сделать главное меню, где назначаем варианты определения ветра:
//       установка только вручную; установка по сравнению; установка по статистике

// TODO: организовать управление нахождения ветра:
//       если началась гонка, включаем запуск сравнения, если нет данных по ручному ветру -
// исходим из того, что у нас правый бейдевинд
//       либо запускаем если выбран чек поле "запуск сравнения"

// TODO: при первом открытии по умолчанию загружаем фрагмент карты
//   обработка запуска гонки (по окончании таймера)
//  обработка остановки гонки (из таймера)
//  cancelRace - кнопка вверху
//  нужна кнопка моментального старта гонки


// TODO: перед праздниками остановился на реализации отображения записываемого трека на карте
//  логировать и тестировать вызов старта записи (сделал временную кнопку) и процесс создания и рисования трека - координаты, отображение точки, и т.д.
//  переписать старт записи трека на остановку таймера

// TODO: при вызове новой гонки таймер получается скомканный. что-то с контейнером таймера.