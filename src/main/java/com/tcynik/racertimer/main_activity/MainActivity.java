package com.tcynik.racertimer.main_activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.racertimer.R;
import com.tcynik.racertimer.info_bar.InfoBarPresenter;
import com.tcynik.racertimer.info_bar.InfoBarStatusUpdater;
import com.tcynik.racertimer.info_bar.TextViewController;
import com.tcynik.racertimer.location_access.LocationAccessDispatcher;
import com.tcynik.racertimer.location_access.LocationManagerInterface;
import com.tcynik.racertimer.location_access.LocationService;
import com.tcynik.racertimer.main_activity.data.network.WeatherResultInterface;
import com.tcynik.racertimer.main_activity.data.network.WindDirRequestUseCase;
import com.tcynik.racertimer.main_activity.data.wind_direction.WindChangedHeraldInterface;
import com.tcynik.racertimer.main_activity.data.wind_direction.WindDataRepository;
import com.tcynik.racertimer.main_activity.data.wind_direction.WindProvider;
import com.tcynik.racertimer.main_activity.domain.StatusUIModulesDispatcher;
import com.tcynik.racertimer.main_activity.presentation.ManuallyWind;
import com.tcynik.racertimer.main_activity.presentation.interfaces.LocationHeraldInterface;
import com.tcynik.racertimer.main_activity.presentation.interfaces.StatusUiUpdater;
import com.tcynik.racertimer.main_activity.racing_timer.RacingTimer;
import com.tcynik.racertimer.main_activity.racing_timer.TimerFragment;
import com.tcynik.racertimer.menu.DeveloperFragment;
import com.tcynik.racertimer.menu.MenuFragment;
import com.tcynik.racertimer.sailingToolsFragment.SailingToolsFragment;
import com.tcynik.racertimer.tracks_map.MapManager;
import com.tcynik.racertimer.tracks_map.MapUIToolsController;
import com.tcynik.racertimer.tracks_map.data.TracksDataManager;
import com.tcynik.racertimer.tracks_map.data.models.GeoTrack;
import com.tcynik.racertimer.tracks_map.presentation.MapFragment;
import com.tcynik.racertimer.tracks_map.presentation.TracksMenuFragment;
import com.tcynik.racertimer.tracks_map.presentation.scrolls.MapHorizontalScrollView;
import com.tcynik.racertimer.tracks_map.presentation.scrolls.MapScrollView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String PROJECT_LOG_TAG = "racer_timer";
    private final static String MODULE_LOG_TAG = "_main";
    private final String BROADCAST_ACTION = "com.example.racertimer.action.new_location"; // значение для фильтра приемника

    final int DEFAULT_WIND_DIRECTION = 202; // usual wind direction on the main developer's spot "Shumiha"

    private Button btnStopStartTimerAndStopRace;
    private ImageButton btnMenu;
    private TextView racingTimerTV;

    private TimerFragment timerFragment = null;
    private MapFragment mapFragment;
    public MapUIToolsController mapUITools;
    public SailingToolsFragment sailingToolsFragment = null;
    public MenuFragment menuFragment = null;
    public DeveloperFragment developerFragment = null;
    public FragmentContainerView menuPlace; // место, в котором возникает меню

    private RacingTimer racingTimer;
    public TracksDataManager tracksDataManager;
    public MapManager mapManager;
    private String tracksFolderAddress = "\ntracks\nsaved\n";

    private int windDirection;
    private Location location = null; // текущее положение

    private double defaultMapScale = 1.0;

    private InfoBarStatusUpdater infoBarStatusUpdater;
    private InfoBarPresenter infoBarPresenter;

    private boolean isRaceStarted = false; // флаг того то, происходит сейчас гонка

    private Intent intentLocationService; // интент для создания сервиса геолокации
    private BroadcastReceiver locationBroadcastReceiver;

    private LocationService locationService;
    private Binder binder;

    private WindChangedHeraldInterface windChangedHerald;
    private WindProvider windProvider;
    private WindDirRequestUseCase networkWindGetter;

    private WindDataRepository windDataRepository;

    private StatusUIModulesDispatcher statusUIModulesDispatcher;

    private boolean isWindDataFresh = false;

    private String lastTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        //todo: на устройстве пчмт навигационная панель (кнопки внизу) получается с белым фоном (в эмуляторе с черным). пытаюсь вообще убрать бар:
        //new ManagerVisibilityUI(this); // модуль для скрытия-появления панели. Работает, но после исчезновения, перехватывает ВСЕ нажатия на экран и появляется

        findViews();
        setClickListeners();

        LocationAccessDispatcher locationAccessDispatcher = new LocationAccessDispatcher(
                this,
                new LocationManagerInterface() {
                    @Override
                    public void accessGranted() {
                        startLocationService();
                        bindToLocationService();
                    }

                    @Override
            public void askPermissionGPS() {
                requestPermissions(new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        100); // ключ 100, такой же как ниже
            }

            @Override
            public void finishApp() {
                finish();
            }
        });
        locationAccessDispatcher.execute();

        windChangedHerald = initWindChangeHerald();
        tracksDataManager = new TracksDataManager(this, tracksFolderAddress);
        mapManager = new MapManager(this, defaultMapScale);

        infoBarStatusUpdater = new InfoBarStatusUpdater() {
            @Override
            public void onTimerStatusUpdated(String timerStatus) {
                infoBarPresenter.updateTheBar(timerStatus);
            }
        };
        initInfoBar();

        mapUITools = new MapUIToolsController(defaultMapScale);
        runStatusUIDispatcher();
        if (windProvider != WindProvider.FORECAST) loadWindData();

        networkWindGetter = new WindDirRequestUseCase(new WeatherResultInterface() {
            @Override
            public void gotResult(int windDir) {
                changeWindDirAndSave(windDir, WindProvider.FORECAST);
                Toast.makeText(MainActivity.this, R.string.wind_dir_upd_from_net, Toast.LENGTH_LONG).show();
            }

            @Override
            public void gorError(@NonNull String error) {
                Toast.makeText(MainActivity.this, "Unable to update wind direction from network. Use manually wind setting.", Toast.LENGTH_LONG).show();
                Log.i(PROJECT_LOG_TAG+MODULE_LOG_TAG, "The weather information is not updated. Reason: "+error);
            }
        });
    }

    private void initInfoBar() {
        TextViewController infoBarTVInterface = new TextViewController() {
            @Override
            public void updateTextView(String nexText) {
                if (true)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            racingTimerTV.setText(nexText);
                        }
                    });
            }

            @Override
            public String getTextFromView() {
                return (String) racingTimerTV.getText();
            }
        };
        infoBarPresenter = new InfoBarPresenter();
        infoBarPresenter.setInfoBarTVInterface(infoBarTVInterface);
        infoBarPresenter.updateTheBar("greetings");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (location == null) {
            initBroadcastListener();
        }
        if (locationService != null) {
            locationService.appWasResumedOrStopped(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void setMapFragment (MapFragment mapFragment) {
        this.mapFragment = mapFragment;
    }

    private void runStatusUIDispatcher() {
        // TODO: need to add here mapManager to transfer new geolocation info into one
        LocationHeraldInterface updaterMapTools = mapUITools.getContentUpdater();
        LocationHeraldInterface updaterTools = sailingToolsFragment.getContentUpdater();
        LocationHeraldInterface updaterMap = mapManager.getContentUpdater();
        LocationHeraldInterface updaterDataManager = tracksDataManager.getContentUpdater();
        LocationHeraldInterface[] locationHeralds = new LocationHeraldInterface[]{
                updaterTools,
                updaterMapTools,
                updaterMap,
                updaterDataManager};

        String[] moduleNames = new String[] {
                "sailing_tools",
                "map_tools",
                "map",
                "tracks_data_manager"};

        statusUIModulesDispatcher = new StatusUIModulesDispatcher(moduleNames, locationHeralds);
        StatusUiUpdater updaterStatusUi = statusUIModulesDispatcher.getStatusUiUpdater();
        mapFragment.setStatusUiUpdater(updaterStatusUi);
        sailingToolsFragment.setStatusUiUpdater(updaterStatusUi);
        StatusUiUpdater dataManagerUpdater = statusUIModulesDispatcher.getStatusUiUpdater();
        dataManagerUpdater.updateUIModuleStatus("tracks_data_manager", true);
    }

    private void serviceIsRan() {
        LocationHeraldInterface updaterLocationService = new LocationHeraldInterface() {
            @Override
            public void onLocationChanged(Location location) {
            }

            @Override
            public void onWindDirectionChanged(int windDirection, WindProvider provider) {
                locationService.setWindDirection(windDirection);
            }
        };
        statusUIModulesDispatcher.sendWindToContentUpdater(updaterLocationService);
    }

    @Override
    protected void onStop() {
        if (locationService != null) locationService.appWasResumedOrStopped(false);
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
                        infoBarPresenter.updateTheBar("ready to go");
                    }
                } else { // timer = 0,
                    if (isRaceStarted) { // race = 1 : stop the race
                        Log.i(PROJECT_LOG_TAG+MODULE_LOG_TAG, " init saving the track ");
                        tracksDataManager.initSavingRecordedTrack();
                    } else { // race = 0, timer = 0 : start the timer
                        deployTimerFragment();
                        infoBarPresenter.updateTheBar("timer");
                    }
                }
            }
        });
    }

    private WindChangedHeraldInterface initWindChangeHerald() {
        return new WindChangedHeraldInterface() {
            @Override
            public void onWindDirectionChanged(int updatedWindDirection, WindProvider provider) {
                windDirection = updatedWindDirection;
                sailingToolsFragment.onWindDirectionChanged(updatedWindDirection, provider);
                if (mapUITools != null) {
                    Log.i(PROJECT_LOG_TAG+MODULE_LOG_TAG, "changing the wind in the map to "+updatedWindDirection);
                    mapUITools.setWindArrowDirection(updatedWindDirection);
                }
                // TODO: move windDirection into location service
            }
        };
    }

    private void loadWindData() {
        WindChangedHeraldInterface windChangedHerald = new WindChangedHeraldInterface() {
            @Override
            public void onWindDirectionChanged(int windDirection, WindProvider provider) {
                if (windDirection == 10000) {
                    windDirection = DEFAULT_WIND_DIRECTION;
                    provider = WindProvider.DEFAULT;
                }
                overallWindChanging(windDirection, provider);
            }
        };

        if (windDataRepository == null) windDataRepository = new WindDataRepository(this);
        windDataRepository.returnWindData(windChangedHerald);
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

    public void forceUpdateWindDirectionFromService() {
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
                        clearCurrentTrack();
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = confirmSaveTrack.create(); // создание диалога
        alertDialog.setTitle("Save the track?"); // заголовок
        alertDialog.show(); // отображение диалога
    }

    public void clearCurrentTrack() {
        tracksDataManager.clearTheTrack();
        mapManager.stopAndDeleteTrack();
        endRace();
        btnStopStartTimerAndStopRace.setText("NEW RACE");
    }

    public void setSailingToolsFragment(SailingToolsFragment sailingToolsFragment) {
        this.sailingToolsFragment = sailingToolsFragment;
    }

    public void uploadMapUIIntoTools (ImageView arrowDirection,
                                      ImageView arrowWind,
                                      Button btnIncScale,
                                      Button btnDecScale,
                                      ImageButton btnFixPosition,
                                      Button menuTracks) {
        mapUITools.setUIViews(arrowDirection, arrowWind, btnIncScale, btnDecScale, btnFixPosition);
        if (mapManager != null) mapUITools.setMapManager(mapManager);
        else Log.i(PROJECT_LOG_TAG+MODULE_LOG_TAG, "map manager is null! Passing to Tools is false");

        menuTracks.setOnClickListener(new View.OnClickListener() {
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
        TracksMenuFragment tracksMenuFragment = new TracksMenuFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fr_menu_place, tracksMenuFragment);
        fragmentTransaction.commit();
        menuPlace.setVisibility(View.VISIBLE);
        infoBarPresenter.updateTheBar("timer");
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
        Log.i(PROJECT_LOG_TAG, "currentLocation is null = " +(location == null));
        return location;
    }

    /** Отработка нажатия кнопки "Назад" */
    @Override
    public void onBackPressed() { // в случае нажатия кнопки назад диалог по переходу в главное меню
        AlertDialog.Builder confurmingRaceEnd = new AlertDialog.Builder(this); // строитель диалога
        confurmingRaceEnd.setMessage("Are you sure you want to close the application?")
                .setCancelable(false)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i(PROJECT_LOG_TAG+MODULE_LOG_TAG, "app is closing by user... ");
                        stopRace();
                        if (locationService != null) locationService.stopService(intentLocationService);
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
        alertDialog.setTitle("Closing the app"); // заголовок
        alertDialog.show(); // отображение диалога
    }

    /** блок меню */
    public void closeMenu() {
        menuPlace.setVisibility(View.INVISIBLE);
    }

    //TODO: все что ниже - к прежнему меню. Сейчас перешел на кастомное, надо все будет убрать
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(PROJECT_LOG_TAG+MODULE_LOG_TAG, "starting menu1... ");
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void stopRace() { // остановка гонки
        super.onBackPressed();
    }

    private void startLocationService() {
        intentLocationService = new Intent(this, LocationService.class);
        intentLocationService.setPackage("com.example.racertimer.location_access");
        startService(intentLocationService);
    }

    /** биндимся к сервису для управления им */
    private void bindToLocationService() {
        Log.i(PROJECT_LOG_TAG+MODULE_LOG_TAG, "Making service connection... " );
        // приводим биндер к кастомному биндеру с методом связи
        // получаем экземпляр нашего сервиса через биндер
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.i(PROJECT_LOG_TAG+MODULE_LOG_TAG, "Location service binded ");
                binder = (LocationService.MyBinder) iBinder; // приводим биндер к кастомному биндеру с методом связи
                locationService = ((LocationService.MyBinder) binder).getService(); // получаем экземпляр нашего сервиса через биндер
                serviceIsRan();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.i(PROJECT_LOG_TAG+MODULE_LOG_TAG, "location service disconnected ");
            }
        };
        try{
            bindService(intentLocationService, serviceConnection, BIND_EXTERNAL_SERVICE);
        } catch (Exception e) {
            Log.i(PROJECT_LOG_TAG+MODULE_LOG_TAG, "location service disconnected ");
            Toast.makeText(this, "Error corrupted: " + e, Toast.LENGTH_LONG).show();
        }
    }

    /** блок работы со слушателем геолокации  */
    private void initBroadcastListener() {
        if (locationBroadcastReceiver == null) {
            locationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) { // обработка интента
                    if (intent.hasExtra("location")) { // если в сообщении есть геолокация
                        Location location = (Location) intent.getExtras().get("location");
                        processorChangedLocation(location); // отдаем геоданные на обработку
                        Log.i(PROJECT_LOG_TAG+MODULE_LOG_TAG, "getted location broadcast from locationService, " +
                                "new velocity = " + (int)((Location) intent.getExtras().get("location")).getSpeed());
                    }
                    if (intent.hasExtra("windDirection")) {
                        int windDirectionFromExtra = (int) intent.getExtras().get("windDirection");
                        if (windDirectionFromExtra != 10000) {
                            int windDirection = (int) intent.getExtras().get("windDirection");
                            changeWindDirAndSave(windDirection, WindProvider.CALCULATED);
                            Log.i(PROJECT_LOG_TAG+MODULE_LOG_TAG, "got wind broadcast from locationService, new windDir = " + intent.getExtras().get("windDirection"));
                        }
                    }

                    if (intent.hasExtra("locationsData")) {
                        ArrayList<Location> missedLocations = (ArrayList<Location>) intent.getExtras().get("locationsData");
                        if (missedLocations.size() == 0) {
                            Log.i(PROJECT_LOG_TAG+MODULE_LOG_TAG, "activity got an empty missed locations");
                        }
                        else {
                            Log.i(PROJECT_LOG_TAG+MODULE_LOG_TAG, "activity got missed locations with "+missedLocations.size()+ " points");
                            mapManager.hasMissedLocations(missedLocations);
                            tracksDataManager.hasMissedLocations(missedLocations);
                        }
                    }
                    if (intent.hasExtra("provider GPS")) {
                        int providerValue = (int) intent.getExtras().get("provider GPS");
                        if (providerValue == 0) {
                            Toast.makeText(context, "Device's GPS location is OFF! Turn it on!", Toast.LENGTH_LONG).show();
                            //todo: кнопка управлением гонки по центру экрана
                            //todo: make dialog to turn gps location on
                        }
                    }
                }
            };
        }

        IntentFilter locationIntentFilter = new IntentFilter(BROADCAST_ACTION); // прописываем интент фильтр для слушателя
        registerReceiver(locationBroadcastReceiver, locationIntentFilter); // регистрируем слушатель
    }

    private void changeWindDirAndSave(int updatedWindDirection, WindProvider provider) { // смена направления ветра
        if (windDataRepository == null) windDataRepository = new WindDataRepository(this);
        windDataRepository.saveWindData(updatedWindDirection, provider);
        overallWindChanging(updatedWindDirection, provider);
    }

    private void overallWindChanging(int updatedWindDirection, WindProvider provider) {
        Log.i(PROJECT_LOG_TAG+MODULE_LOG_TAG, "wind direction changed by provider: "+provider);
        windProvider = provider;
        windDirection = updatedWindDirection;
        statusUIModulesDispatcher.getWindChangedHerald().onWindDirectionChanged(updatedWindDirection, provider);
        infoBarPresenter.updateTheBar("set wind");
        if (provider == WindProvider.DEFAULT || provider == WindProvider.HISTORY)
            infoBarPresenter.updateTheBar("wind old");
        else infoBarPresenter.updateTheBar("wind ok");
        // TODO: если после запуска вручную ввели новое направление?
    }

    public void manuallyWindManager () { // установка направления ветра вручную
        Log.i(PROJECT_LOG_TAG+MODULE_LOG_TAG, " starting manually setting wind  ");
        windChangedHerald = new WindChangedHeraldInterface() {
            @Override
            public void onWindDirectionChanged(int windDirection, WindProvider provider) {
                MainActivity.this.changeWindDirAndSave(windDirection, provider);
            }
        };
        ManuallyWind manuallyWind = new ManuallyWind(this, windDirection, windChangedHerald);
        manuallyWind.showView();
    }

    /** обработка вновь полученных геолокации */
    private void processorChangedLocation (Location location) { // обработчик новой измененной позиции
        Log.i(PROJECT_LOG_TAG+MODULE_LOG_TAG, " Thread: "+Thread.currentThread().getName() + ". Activity race get new location ");

        if (this.location == null) { // если это первое получение геолокации
            Toast.makeText(this, "GPS is online", Toast.LENGTH_LONG).show();
            this.location = location;
            networkWindGetter.execute(location);
            infoBarPresenter.updateTheBar("gps");
        }

        statusUIModulesDispatcher.getLocationChanger().onLocationChanged(location);

        if (location.hasSpeed()) {
            if (!mapManager.isRecordingInProgress() & isRaceStarted) mapManager.beginNewCurrentTrackDrawing();
        }
    }

    public void muteChangedStatus(boolean b) { // выключение звука пищалки
        sailingToolsFragment.muteChangedStatus(b);
    }

    public void resetAllMaximums() {
        sailingToolsFragment.resetPressed();
    }

    public void endRace() {
        infoBarPresenter.stopRaceOnTimer(lastTimer);
        racingTimer.stop();
        isRaceStarted = false;
        sailingToolsFragment.stopTheRace();
        locationService.setCalculatorStatus(false);
    }

    public void startTheRace() {
        tracksDataManager.beginRecordTrack();
        sailingToolsFragment.startTheRace();
        isRaceStarted = true;
        undeployTimerFragment();
        infoBarPresenter.updateTheBar("start");
        locationService.setCalculatorStatus(true);
        btnStopStartTimerAndStopRace.setText("STOP RACE");
        startRacingTimer();
    }

    private void startRacingTimer() {
        racingTimer = new RacingTimer(this, infoBarStatusUpdater);
        racingTimer.start();
    }

    public void onStartingTimerPeriodChanged (String nextStatus) {// todo: spaghetti code! To be fixed
        infoBarPresenter.updateTheBar(nextStatus);
        lastTimer = nextStatus;
    }

    public String getTracksPackage() {
        return tracksFolderAddress;
    }
}