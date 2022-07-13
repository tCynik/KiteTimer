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
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.example.racertimer.Instruments.RacingTimer;
import com.example.racertimer.Instruments.TimerStatusUpdater;
import com.example.racertimer.map.MapHorizontalScrollView;
import com.example.racertimer.map.MapManager;
import com.example.racertimer.map.MapScrollView;
import com.example.racertimer.map.MapUITools;
import com.example.racertimer.multimedia.BeepSounds;
import com.example.racertimer.tracks.GeoTrack;
import com.example.racertimer.tracks.TracksDataManager;
import com.example.racertimer.tracks.TracksMenuFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final static String PROJECT_LOG_TAG = "racer_timer";
    final String BROADCAST_ACTION = "com.example.racertimer.action.new_location"; // значение для фильтра приемника

    private Button btnStopStartTimerAndStopRace;

    private ImageButton btnMenu;
    private TextView racingTimerTV;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private boolean windDirectionGettedFromService = false; // флаг того, что уже были получены данные по направлению ветра

    private TracksMenuFragment tracksMenuFragment;
    private TimerFragment timerFragment = null;
    private MapFragment mapFragment = null;
    public MapUITools mapUITools;
    public SailingToolsFragment sailingToolsFragment = null;
    public MenuFragment menuFragment = null;
    public DeveloperFragment developerFragment = null;
    public FragmentContainerView menuPlace; // место, в котором возникает меню

    private RacingTimer racingTimer;
    public TracksDataManager tracksDataManager;
    public MapManager mapManager;
    private String tracksFolderAddress = "\ntracks\nsaved\n";

    private ImageView arrowDirectionOnMap, arrowWindOnMap;

    private BeepSounds voiceover;

    private int velocity, bearing, windDirection;// !!!ПРОВЕРИТЬ ПУСТЫШКИ

    private int defaultMapScale = 1;

    private TimerStatusUpdater timerStatusUpdater;
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
        context = this;

        findViews();
        setClickListeners();

        windDirection = 202;

        voiceover = new BeepSounds(context);
        sailingToolsFragment.setVoiceover(voiceover);

        createLocationService();

        tracksDataManager = new TracksDataManager(this, tracksFolderAddress);
        mapManager = new MapManager(context);

        timerStatusUpdater = new TimerStatusUpdater() {
            @Override
            public void onTimerStatusUpdated(String timerStatus) {
                racingTimerTV.setText(timerStatus);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (location == null) {
            initBroadcastListener(); // запускаем слушатель новых геоданных
            bindToLocationService();
        }
        if (locationService != null) locationService.appWasResumedOrStopped(true);
    }

    @Override
    protected void onStop() {
        //Log.i("bugfix", " mainActivity: app is stopped ");
        locationService.appWasResumedOrStopped(false);
        super.onStop();
    }

    private void findViews() {
        btnMenu = findViewById(R.id.button_menu);
        btnStopStartTimerAndStopRace = findViewById(R.id.stopwatch);

        menuPlace = findViewById(R.id.fr_menu_place); // находим контейнер для дальнейшего размещения вьюшек

        racingTimerTV = findViewById(R.id.racing_timer);
    }

    private void setClickListeners() {

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deployMenuFragment();
            }
        });

        btnStopStartTimerAndStopRace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerFragment != null) { // race = 0, timer = 1 : close the timer
                    if (timerFragment.isTimerRan()) stopTimerAlertDialog();
                    else {
                        undeployTimerFragment();
                        racingTimerTV.setText("Go chase!");
                    }
                } else { // timer = 0,
                    if (isRaceStarted) { // race = 1 : stop the race
                        tracksDataManager.initSavingRecordedTrack();
                    } else { // race = 0, timer = 0 : start the timer
                        deployTimerFragment();
                        racingTimerTV.setText("GET READY!");
                    }
                }
            }
        });
    }

    private void stopTimerAlertDialog() {
        AlertDialog.Builder stopTimer = new AlertDialog.Builder(this);
        stopTimer.setMessage("Cancel the race timer?").setCancelable(true)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        undeployTimerFragment();
                        racingTimerTV.setText("Go chase!");
                    }
                }). setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = stopTimer.create();
        alertDialog.show();
    }

    public void updateWindDirectionFromService() {
        locationService.updateWindDirection();
    }

    public void askToSaveTrack(String trackName) {
        AlertDialog.Builder confirmSaveTrack = new AlertDialog.Builder(this); // строитель диалога
        confirmSaveTrack.setMessage("Track name: " + trackName)
                .setCancelable(true) // можно продолжить запись, нажав мимо
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        GeoTrack geoTrack = tracksDataManager.saveCurrentTrackByName(trackName);
                        mapManager.stopAndSaveTrack(geoTrack);
                        endRace();
                        btnStopStartTimerAndStopRace.setText("NEW RACE");
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setNeutralButton("delete track", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        tracksDataManager.clearTheTrack();
                        mapManager.stopAndDeleteTrack();
                        endRace();
                        dialogInterface.cancel();
                        btnStopStartTimerAndStopRace.setText("NEW RACE");
                    }
                });
        AlertDialog alertDialog = confirmSaveTrack.create(); // создание диалога
        alertDialog.setTitle("Save the track?"); // заголовок
        alertDialog.show(); // отображение диалога
    }

    public void setSailingToolsFragment(SailingToolsFragment sailingToolsFragment) {
        this.sailingToolsFragment = sailingToolsFragment;
    }

    public void uploadMapUIIntoTools (ImageView arrowDirection, ImageView arrowWind,
                                      Button btnIncScale, Button btnDecScale, ImageButton btnFixPosition,
                                      Button menuTracks) {
        mapUITools = new MapUITools(defaultMapScale);
        mapUITools.setUIViews(arrowDirection, arrowWind, btnIncScale, btnDecScale, btnFixPosition);
        mapUITools.setMapManager(mapManager);

        mapUITools.setWindArrowDirection(CoursesCalculator.invertCourse(windDirection));

        Button buttonMenuTracks = menuTracks;
        buttonMenuTracks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deployTracksMenuFragment();
            }
        });
    }

    public void uploadTrackLayout (MapScrollView windowForMap, MapHorizontalScrollView horizontalMapScroll,
                                   ConstraintLayout trackLayoutForTrackPainter, ImageButton btnFixPressed, ImageView arrowPosition) {
        mapManager.setTracksLayout(windowForMap, horizontalMapScroll, trackLayoutForTrackPainter, btnFixPressed, arrowPosition);
    }

    /** модуль методов выгрузки фрагментов */
    //TODO: make subclass FragmentDeployer, which will able to deploy and undeploy all fragments
    public void deployTimerFragment() { // создание фрагмента для таймера
        if (timerFragment == null) timerFragment = new TimerFragment();
        btnStopStartTimerAndStopRace.setText("Cancel");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.timer_container, timerFragment);
        findViewById(R.id.timer_container).setVisibility(View.VISIBLE);
        fragmentTransaction.commit();
    }

    public void deployMenuFragment () { // выгрузка фрагмена меню
        if (menuFragment == null) menuFragment = new MenuFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fr_menu_place, menuFragment);
        fragmentTransaction.commit();
        menuPlace.setVisibility(View.VISIBLE);
    }

    public void deployDeveloperTools () { // выгрузка фрагмена меню
        FragmentManager fragmentManager = getSupportFragmentManager();
        developerFragment = new DeveloperFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fr_menu_place, developerFragment);
        fragmentTransaction.commit();
    }

    public void deployTracksMenuFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        tracksMenuFragment = new TracksMenuFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fr_menu_place, tracksMenuFragment);
        fragmentTransaction.commit();
        menuPlace.setVisibility(View.VISIBLE);
    }

    public void undeployTracksMenu(){
        menuPlace.setVisibility(View.INVISIBLE);
    }

    private void undeployTimerFragment() {
        btnStopStartTimerAndStopRace.setText("NEW RACE");
        timerFragment.stopTheTimer();
        timerFragment = null;
        findViewById(R.id.timer_container).setVisibility(View.INVISIBLE);
    }

    public Location getCurrentLocation () {
        return location;
    }

    /** Отработка нажатия кнопки "Назад" */
    @Override
    public void onBackPressed() { // в случае нажатия кнопки назад диалог по переходу в главное меню
        AlertDialog.Builder confurmingRaceEnd = new AlertDialog.Builder(this); // строитель диалога
        confurmingRaceEnd.setMessage("End the race?")
                .setCancelable(false) // не отменяемый (при нажатии вне поля диалога не закрывается)
                // назначаем кнопки взаимодействия
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("racer_timer", "app is closing by user... ");
                        stopRace();
                        locationService.stopService(intentLocationService);
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

//    public void updateWindDirectionFromService() { // получение ветра для событий, требующих этого
//        if (locationService != null) locationService.updateWindDirection();
//    }

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
        if (! checkLocationPermission()) askLocationPermission();

        if (checkLocationPermission()) {
            Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + " permission good, starting service ");
            intentLocationService = new Intent(this, LocationService.class);
            intentLocationService.setPackage("com.example.racertimer.Instruments");
            this.startService(intentLocationService);
        } else { // если разрешения нет, выводим тост
            Toast.makeText(this, "No GPS permission", Toast.LENGTH_LONG);
        }
    }

    /** Методы для работы с разрешениями на геолокацию */
    public boolean checkLocationPermission() {
        // если разрешения нет:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&  // если версия СДК выше версии M (API 23)
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) // если разрешения нет, то запускаем запрос разрешения, код ответа 100
        {
            return false; // если разрешения нет, возвращаем false
        } else
            return true; //
    }
    private void askLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
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
        if (locationBroadcastReceiver == null) {
            locationBroadcastReceiver = new BroadcastReceiver() {
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
                            Log.i("racer_timer", "got wind broadcast from locationService, new windDir = " + intent.getExtras().get("windDirection"));
                        }
                    }

                    if (intent.hasExtra("locationsData")) {
                        ArrayList<Location> missedLocations = (ArrayList<Location>) intent.getExtras().get("locationsData");
                        if (missedLocations.size() == 0) {
                            Log.i("racer_timer", "activity got an empty missed locations");
                        }
                        else {
                            Log.i("racer_timer", "activity got missed locations with "+missedLocations.size()+ " points");
                            mapManager.hasMissedLocations(missedLocations);
                            tracksDataManager.hasMissedLocations(missedLocations);
                        }
                    }
                }
            };
        }

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
            mapUITools.setWindArrowDirection(updatedWindDirection);
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
        }
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.i(PROJECT_LOG_TAG+"_coord", "location coordinates: latitude= "+latitude + ", longitude = " + longitude);


        if (location.hasSpeed()) {
            Log.i("racer_timer_painter", "sending location to trackpainter from main activity" );
            if (!mapManager.isRecordingInProgress() & isRaceStarted) mapManager.beginNewCurrentTrackDrawing();
            mapManager.onLocationChanged(location);
            tracksDataManager.onLocationChanged(location);
            tempVelocity = (double) location.getSpeed()*3.6;
            velocity = (int) tempVelocity;
            if (sailingToolsFragment != null) {
                Log.i("racer_timer", " sending into sailing tools velocity = "+ velocity);
                sailingToolsFragment.onVelocityChanged(velocity);
            }
        } else sailingToolsFragment.onVelocityChanged(0);
        bearing = courseAverage((int) location.getBearing()); // с учетом усреднения
        if (sailingToolsFragment != null) sailingToolsFragment.onBearingChanged(bearing);
        if (mapUITools != null) mapUITools.onBearingChanged(bearing);
    }

    public void muteChangedStatus(boolean b) { // выключение звука пищалки
        sailingToolsFragment.muteChangedStatus(b);
    }

    public void resetAllMaximums() {
        sailingToolsFragment.resetPressed();
        Log.i("racer_timer", "reset VMG maximums");
    }

    public void endRace() {
        racingTimer.stop();
        isRaceStarted = false;
    }

    public void StartTheRace() {
        tracksDataManager.beginRecordTrack();
        isRaceStarted = true;
        undeployTimerFragment();
        btnStopStartTimerAndStopRace.setText("STOP RACE");
        // TODO: here must be tested correctness text in start-timer-button
        startRacingTimer();
    }

    private void startRacingTimer() {
        racingTimer = new RacingTimer(timerStatusUpdater);
        racingTimer.start();
    }

    public String getTracksPackage() {
        return tracksFolderAddress;
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

// TODO: make no GPS signal info in map