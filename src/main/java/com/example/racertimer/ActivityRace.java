package com.example.racertimer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.racertimer.Instruments.CoursesCalculator;
import com.example.racertimer.Instruments.LocationService;
import com.example.racertimer.multimedia.Voiceover;

public class ActivityRace extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, ForecastFragment.OpenerTimerInterface, TimerFragment.CloserTimerInterface { // добавить интерфейс
    private final static String PROJECT_LOG_TAG = "racer_timer";
    final String BROADCAST_ACTION = "com.example.racertimer.action.new_location"; // значение для фильтра приемника

    private ImageView windFrameIV, arrowVelocityIV, frameVelocityIV, arrowDirectionIV;
    private ImageView[] lineVMGIV;
    private ImageView lineUpLeftIV, lineUpRightIV, lineDownLeftTV, lineDownRightTV; // линии отображения курса VMG
    private Space centerScreenSpace;
    private SeekBar windSB, bearingSB, velocitySB;
    private Button btnReset, btnWindPlus, btnWindMinus;
    private TextView velocityTV, bearingTV, windTV, velocityMadeGoodTV, bestDownwindTV, maxVelocityTV, bestUpwindTV, courseToWindTV;
    private ConstraintLayout centralParamsCL, centralUiCL;

    private TimerFragment timerFragment = null;
    private ForecastFragment forecastFragment = null;


    private int velocity, bearing, windDirection, velocityMadeGood, velocityMax, VMGmax, VMGmin;

    private TextView textTime; // переменная времени в левом вехнем углу

    private String timerString = "00:00.00"; // переменная для вывода текущего секундомера чч:мм:сс.сот
    private int timerHour = 0; // переменная в часах
    private int timerMin = 0; // переменная счетчика в минутах
    private int timerSec = 0; // текущее значение таймера в сотых долей секунды

    private TextView speedTV, maxSpeedTV, courseTV, countLocalChangedTV; // переменные для привызки полей скорости и курса

    private int deltaBearing;
    private int courseToWind;
    private int velosity = 0; // скорость в кмч
    private int maxSpeed = 0; // максимальная зарегистрированная скорость
    private boolean isFirstIteration = true; // флаг о том, что это первая итерация для выставления первичных цифр
    private int countLocationChanged = 0; // счетчик сколько раз изменялось геоположение
    int centerScreenX, centerScreenY;
    private double latitude = 0;
    private double longitude = 0; // координаты для получения прогноза

    private Intent intentLocationService; // интент для создания сервиса геолокации
    private BroadcastReceiver locationBroadcastReceiver;
    private IntentFilter locationIntentFilter;

    private Context context;
//    public Voiceover voiceover;
//    private int beepID = 0; // переменная для записи номера потока произрываемых звуков

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);
////////// вынеси определение вьюшек в отдельный метод
        centerScreenSpace = findViewById(R.id.space_center);
        centralParamsCL = findViewById(R.id.central_params_cl);
        centralUiCL = findViewById(R.id.central_ui_cl);
        windFrameIV = findViewById(R.id.wind_frame);
        arrowVelocityIV = findViewById(R.id.arrow);
        frameVelocityIV = findViewById(R.id.frame_velocity);
        arrowDirectionIV = findViewById(R.id.arrow_direction);
        windSB = findViewById(R.id.seekBar_wind);
        bearingSB = findViewById(R.id.seekBar_bearing);
        velocitySB = findViewById(R.id.seekBar_velosity);
        velocityTV = findViewById(R.id.velocity);
        bearingTV = findViewById(R.id.bearing);
        windTV = findViewById(R.id.wind);
        velocityMadeGoodTV = findViewById(R.id.vmg);
        maxVelocityTV = findViewById(R.id.max_velocity);
        bestUpwindTV = findViewById(R.id.max_vmg);
        bestDownwindTV = findViewById(R.id.min_vmg);
        courseToWindTV = findViewById(R.id.course_to_wind);
        btnReset = findViewById(R.id.but_reset);
        btnWindPlus = findViewById(R.id.wind_inc);
        btnWindMinus = findViewById(R.id.wind_dec);

        // TODO: нужно генерировать линии программно по заданным координатам.
        // этот костыль надо убирать.
        // Линии отображения исторических показаний VMG
        lineVMGIV = new ImageView[4];
        lineVMGIV[0] = findViewById(R.id.line_vmg_down_left);
        lineVMGIV[1] = findViewById(R.id.line_vmg_down_right);
        lineVMGIV[2] = findViewById(R.id.line_vmg_up_right);
        lineVMGIV[3] = findViewById(R.id.line_vmg_up_left);

        // убираем до поры видимость вьюшек, которые пока не определились с положением.
        arrowVelocityIV.setVisibility(View.INVISIBLE);
        arrowDirectionIV.setVisibility(View.INVISIBLE);
        for (ImageView lineIV: lineVMGIV) {
            lineIV.setVisibility(View.INVISIBLE);
        }

        velocityMax = 1;
        //wind = 201;
        windDirection = 24;
        windTV.setText(String.valueOf(windDirection));
        windFrameIV.setRotation(180 - windDirection);

        textTime = findViewById(R.id.currentTime);

        context = this;
        //TODO: в будущем сделать единый войсовер, звуки передавать из таймера через интерфейс
//        voiceover = new Voiceover(context); // создаем озвучку
        forecastFragment = new ForecastFragment();

        /** запускаем таймер */
        // TODO: вот эту срань с таймером переносим во фрагмент прогноза.
        timerRunning(); // запускаем отсчет и обработку таймера

        /** работа с геоданными */
        createLocationService(); // запускаем сервис для полученич геоданных

        initBroadcastListener(); // запускаем слушатель новых геоданных

        // обновляем положение вьюшек

//// потом перепишу слушатели кнопок в единый блок кода. Кнопок добавится много, в т.ч поля
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                velocityMax = 1;
                VMGmax = 0;
                VMGmin = 0;
                lineVMGIV[0].setVisibility(View.INVISIBLE);
                lineVMGIV[1].setVisibility(View.INVISIBLE);
                lineVMGIV[2].setVisibility(View.INVISIBLE);
                lineVMGIV[3].setVisibility(View.INVISIBLE);
                calculateViewsPosition();
                updateMaxVelocityVMG();
            }
        });

        btnWindPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                windDirection++;
                onWindDirectionChanged(windDirection);
            }
        });

        btnWindMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                windDirection--;
                onWindDirectionChanged(windDirection);
            }
        });

        bearing = 0;
        windSB.setOnSeekBarChangeListener(this);
        bearingSB.setOnSeekBarChangeListener(this);
        velocitySB.setOnSeekBarChangeListener(this);

        velocity = 0;

    }

    public void deployTimerFragment() {
        if (timerFragment == null) timerFragment = new TimerFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fr_place_timer_forecast, timerFragment);
        fragmentTransaction.commit();
    }

    public void deployForecastFragment() {
        if (forecastFragment == null) forecastFragment = new ForecastFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fr_place_timer_forecast, forecastFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar == windSB) {
            windDirection = i;
            windDirection = CoursesCalculator.setAngleFrom0To360(windDirection);
            onWindDirectionChanged( windDirection);
        }
        if (seekBar == bearingSB) {
            bearing = i;
            bearingTV.setText(String.valueOf(bearing));
            calculateViewsPosition();
            updateMaxVelocityVMG();
        }
        if (seekBar == velocitySB) {
            velocity = i;
            velocityTV.setText(String.valueOf(velocity));
            calculateViewsPosition();
            updateMaxVelocityVMG();
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
                .setCancelable(false) // не отменяемый (без крестика вверху)
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

    private void stopRace() { // остановка гонки
        super.onBackPressed();
    }

    /** усреднитель курса и обработчик перехода через нулевой азимут */
    private int courseAverage (int newCourse) {
        int deltaCourse = (newCourse - bearing); // разница курсов: "курс новый (newCourse) - курс старый (course)"
        if (deltaCourse > 180) deltaCourse = deltaCourse - 360; //newCourse - (360  - course);
        if (deltaCourse < -180) deltaCourse = 360 + deltaCourse;

        bearing = (int) (bearing + (deltaCourse * 0.75)) ; // усреднение - приращиваем на 75% от разницы
        if (bearing > 360) bearing = bearing - 360;
        if (bearing < 0) bearing = bearing + 360;
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
    // TODO: вот эту срань с таймером переносим во фрагмент прогноза.

    /** Настраиваем и запускаем сервис для приема и трансляции данных геолокации */
    private void createLocationService() {
        if (checkPermission()) { // если разрешение есть, запускаем сервис
            Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + " permission good, starting service ");
            intentLocationService = new Intent(this, LocationService.class);
            intentLocationService.setPackage("com.example.racertimer.Instruments");
            this.startService(intentLocationService);
        } // если разрешения нет, выводим тост
        else Toast.makeText(this, "No GPS permission", Toast.LENGTH_LONG);
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

    /** создаем и регистрируем слушатель геолокации */
    private void initBroadcastListener() {
        locationBroadcastReceiver = new BroadcastReceiver() { // создаем broadcastlistener
            @Override
            public void onReceive(Context context, Intent intent) { // обработка интента
                if (intent.hasExtra("location")) { // если в сообщении есть геолокация
                    Location location = (Location) intent.getExtras().get("location");
                    processorChangedLocation(location); // отдаем точку на обработку в процессор
                    Log.i("ActivityRace", "getted location broadcast from locationService, " +
                            "new velocity = " + (int)((Location) intent.getExtras().get("location")).getSpeed());
                }
                if (intent.hasExtra("windDirection")) {
                    onWindDirectionChanged((int) intent.getExtras().get("windDirection"));
                    windTV.setTextColor(Color.WHITE);
                    Log.i("ActivityRace", "getted wind broadcast from locationService, new windDir = " + intent.getExtras().get("windDirection"));
                }
            }
        };
        locationIntentFilter = new IntentFilter(BROADCAST_ACTION); // прописываем интент фильтр для слушателя
        registerReceiver(locationBroadcastReceiver, locationIntentFilter); // регистрируем слушатель
    }

    private void onWindDirectionChanged (int updatedWindDirection) {
        updateMaxVMGByNewWindDirection (updatedWindDirection);
        windDirection = updatedWindDirection;
        calculateViewsPosition();
        updateMaxVelocityVMG();
        windTV.setText(String.valueOf(windDirection));
        windFrameIV.setRotation(180- windDirection);
    }

    private void updateMaxVMGByNewWindDirection (int updatedWindDirection) {
        int deltaWindDirection = Math.abs(windDirection - updatedWindDirection);
        Log.i("racer_timer", "windDir updated, changing VMG stat. delta wind dir = " + deltaWindDirection);
        if (deltaWindDirection > 180) { // обрабатываем возможный переход через 0-360
            deltaWindDirection = 360 - deltaWindDirection;
        }
        if (deltaWindDirection >= 90) {// если слишком крутое изменение курса, обнуляем все ВМГ
            VMGmin = 0;
            VMGmax = 0;
        } else { // если изменение <90 град, обновляем пропорционально изменению ветра
            double correction = Math.cos(Math.toRadians(deltaWindDirection));
            Log.i("racer_timer", "VMGmin1 = "+ VMGmin +", VMGmax1 = "+ VMGmax +", correction = " + correction);
            VMGmin = (int) (VMGmin * correction);
            VMGmax = (int) (VMGmax * correction);
            Log.i("racer_timer", " finaly VMG's now: VMGmin2 = "+ VMGmin +", VMGmax2 = "+ VMGmax);
        }
    }

    /** обработка вновь полученных геолокации */
    private void processorChangedLocation (Location location) { // обработчик новой измененной позиции
        Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + " Activity race get new location ");
        double tempVelosity;
        if (latitude == 0 & longitude == 0) { // если это первое получение геолокации
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            forecastFragment.setCoordinates(latitude, longitude); // даем его в прогноз погоды
        }
        if (location.hasSpeed()) {
            tempVelosity = (double) location.getSpeed()*3.6;
            velocity = (int) tempVelosity;
            bearing = courseAverage((int) location.getBearing()); // с учетом усреднения
        } else velosity = 0;
        velocityTV.setText(String.valueOf(velocity));
        bearingTV.setText(String.valueOf(bearing));

        calculateViewsPosition();
        updateMaxVelocityVMG();
    }

    void calculateViewsPosition () {
        arrowDirectionIV.setVisibility(View.VISIBLE);
        arrowVelocityIV.setVisibility(View.VISIBLE);
        double radiusSpeedMin = (frameVelocityIV.getWidth() / 2) - frameVelocityIV.getWidth()/4.3; // радиус при нулевой скорости
        int radiusMaxMinDiference = arrowDirectionIV.getHeight(); // максимальная разница между максимальными и минимальным значениями
        int priceGradeSpeed;
        priceGradeSpeed = radiusMaxMinDiference / velocityMax; // цена деления: сколько ед сикбара в ед радиуса
        deltaBearing = bearing - windDirection;
        bearing = CoursesCalculator.setAngleFrom0To360(bearing);
        courseToWind = CoursesCalculator.calcWindCourseAngle(windDirection, bearing);
        windDirection = CoursesCalculator.setAngleFrom0To360(windDirection);
        courseToWindTV.setText(String.valueOf(courseToWind));

        // поворот рамы скорости
        frameVelocityIV.setRotation( deltaBearing + 180);

        // разворот стрелки скорости
        arrowVelocityIV.setRotation( deltaBearing + 225);

        // положение стрелки скорости
//        centerScreenX = centerScreenSpace.getTop(); // добываем координаты центральной точки.
//        centerScreenX = centerScreenSpace.getLeft();
        centerScreenX = windFrameIV.getWidth()/2;
        centerScreenY = windFrameIV.getHeight()/2;
        double courseForWindRadians = Math.toRadians(90 + deltaBearing);
        double radiusVelocityArrow = radiusSpeedMin + velocity * priceGradeSpeed;
        if (radiusVelocityArrow > (radiusSpeedMin + radiusMaxMinDiference)) radiusVelocityArrow = radiusSpeedMin + radiusMaxMinDiference;
        arrowVelocityIV.setX((float) (centerScreenX - arrowVelocityIV.getWidth()/2 + radiusVelocityArrow * Math.cos(courseForWindRadians)));
        arrowVelocityIV.setY((float) (centerScreenY - arrowVelocityIV.getHeight()/2 + radiusVelocityArrow * Math.sin(courseForWindRadians)));
        velocityMadeGood = (int) (Math.sin(Math.toRadians(90 - Math.abs(deltaBearing))) * velocity);
        velocityMadeGoodTV.setText(String.valueOf(velocityMadeGood));

        // разворот стрелки направления
        arrowDirectionIV.setRotation( deltaBearing);
        // положение стрелки направления
        courseForWindRadians = Math.toRadians(90 + deltaBearing);
        arrowDirectionIV.setX((float) (centerScreenX - arrowDirectionIV.getWidth()/2 + ( radiusSpeedMin + arrowDirectionIV.getHeight()/2 + 40) * Math.cos(courseForWindRadians)));
        arrowDirectionIV.setY((float) (centerScreenY - arrowDirectionIV.getHeight()/2 + ( radiusSpeedMin + arrowDirectionIV.getHeight()/2 + 40) * Math.sin(courseForWindRadians)));

    }

    void updateMaxVelocityVMG () {
        int i;
        int imageStartX = lineVMGIV[0].getWidth() / 2;
        if (velocity > velocityMax) velocityMax = velocity;
        int radiusVMGMin = 40;
        double courseForWindRadians = Math.toRadians(90 + deltaBearing);
        float playbackSpeed;

        maxVelocityTV.setText(String.valueOf(velocityMax));

        // выставление меток исторических VMG
        centerScreenX = centralUiCL.getWidth()/2;
        centerScreenY = centralUiCL.getHeight()/2;

        if (velocityMadeGood > VMGmax || velocityMadeGood < VMGmin) { // при обновлении зафиксированного максимума VMG
//            lineVMGIV[2].setVisibility(View.VISIBLE);
            if (velocityMadeGood > 0) VMGmax = velocityMadeGood;
            bestUpwindTV.setText(String.valueOf(VMGmax));
            ////////// это все срань, нужно переписывать на canvas
            ////////// проверить центр экрана, приходится подгонять вручную подпорками
//            if (windCourseAngle > 0) { // курс больше ноля = левый галс
//                if (windCourseAngle < 90) { // идем левый в бакштаг
//                    // ставим метку под левый бакштаг
//                    lineVMGIV[3].setVisibility(View.VISIBLE);
//                    lineVMGIV[3].setX((float) (centerScreenX - 50 - (float) (lineVMGIV[0].getHeight()/2 ) * Math.cos(Math.toRadians(90-windCourseAngle))));
//                    lineVMGIV[3].setY((float) (centerScreenY  - (float) (lineVMGIV[0].getWidth()/2 + 50) * Math.sin(Math.toRadians(windCourseAngle))));
//                    lineVMGIV[3].setRotation(windCourseAngle-180);
//                    // ставим метку под левый бейдевинд
//                    lineVMGIV[2].setVisibility(View.VISIBLE);
//                } else { // идем в левый бейдевинд
//                    // ставим метку под левый бейдевинд
//
//                    // ставим метку под правый бейдевинд
//
//                }
//            } else { // курс меньше ноля = правый галс
//                if (windCourseAngle < -90) { // идем правый в бакштаг
//                    // ставим метку под правый бакштаг
//
//                    // ставим метку под левый бакштаг
//
//                } else { // идем в правый бейдевинд
//                    // ставим метку под правый бейдевинд
//
//                    // ставим метку под левый бейдевинд
//
//                }
//            }
        }

        if (velocityMadeGood < VMGmin) VMGmin = velocityMadeGood;
        bestDownwindTV.setText(String.valueOf(VMGmin));

        // озвучка высоких значений VMG
//        if (beepID == 0) {
//            if (velocityMadeGood > 5) { // если идем против ветра
//                if (VMGmax > (VMGmax / 3) ) {
//                    playbackSpeed = velocityMadeGood / (VMGmax - (VMGmax / 3));
//                    beepID = voiceover.playSoundLoop(voiceover.beepSID, playbackSpeed);
//                    Log.i(PROJECT_LOG_TAG, "playing beep sound, playbackSpeed = " + playbackSpeed);
//                }
//            }
//            if (velocityMadeGood < -10) { // если идем по ветру
//                if (VMGmin < (VMGmin/3) ) {
//                    playbackSpeed = Math.abs(velocityMadeGood / (VMGmax - (VMGmax / 3)));
//                    beepID = voiceover.playSoundLoop(voiceover.beepSID, playbackSpeed);
//                    Log.i(PROJECT_LOG_TAG, "playing beep sound, playbackSpeed = " + playbackSpeed);
//                }
//            }
//        } else { // если звук воспроизводится. проверяем условия для его остановки
//            if (velocityMadeGood < 5 & velocityMadeGood > -10) {
//                voiceover.stopPlaying(voiceover.beepSID, voiceover.beepSID.getSoundID());
//                beepID = 0;
//                Log.i(PROJECT_LOG_TAG, " stop playing beep sound in VMG < 0, soundID = " + beepID);
//            }
//        }


    }

    @Override
    public void finishTheTimer() {
        deployForecastFragment();
    }

    @Override
    public void openTimerFragment() {
        deployTimerFragment();
    }
}
