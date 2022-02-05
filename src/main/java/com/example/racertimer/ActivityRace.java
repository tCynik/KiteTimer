package com.example.racertimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ActivityRace extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener { // добавить интерфейс
    private final static String PROJECT_LOG_TAG = "racer_timer";
    final String BROADCAST_ACTION = "com.example.racertimer.action.new_location"; // значение для фильтра приемника

    private ImageView windFrameIV, arrowVelocityIV, frameVelocityIV, arrowDirectionIV;
    private SeekBar windSB, bearingSB, velocitySB;
    private Button btnReset, btnWindPlus, btnWindMinus;
    private TextView velocityTV, bearingTV, windTV, VMGTV, maxVelocityTV, maxVMGTV;
    private ConstraintLayout constraintLayout;

    private int velocity, bearing, wind, velocityMadeGood, velocityMax, VMGMax;

    private Activity thisActivity; // эта активность - для простоты перехода между экранами
    private TextView timerRace; // таймер гонки
    private TextView textTime; // переменная времени в левом вехнем углу
    private Button buttonExitToMain;

    private String timerString = "00:00.00"; // переменная для вывода текущего секундомера чч:мм:сс.сот
    private int timerHour = 0; // переменная в часах
    private int timerMin = 0; // переменная счетчика в минутах
    private int timerSec = 0; // текущее значение таймера в сотых долей секунды

    private TextView speedTV, maxSpeedTV, courseTV, countLocalChangedTV; // переменные для привызки полей скорости и курса

    private int velosity = 0; // скорость в кмч
    private int maxSpeed = 0; // максимальная зарегистрированная скорость
    private boolean isFirstIteration = true; // флаг о том, что это первая итерация для выставления первичных цифр
    private int countLocationChanged = 0; // счетчик сколько раз изменялось геоположение

    private BroadcastReceiver locationBroadcastReceiver;
    private IntentFilter locationIntentFilter;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);
////////// вынеси определение вьюшек в отдельный метод
        constraintLayout = findViewById(R.id.constraintLayout);
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
        VMGTV = findViewById(R.id.vmg);
        maxVelocityTV = findViewById(R.id.max_velocity);
        maxVMGTV = findViewById(R.id.max_vmg);
        btnReset = findViewById(R.id.but_reset);
        btnWindPlus = findViewById(R.id.wind_inc);
        btnWindMinus = findViewById(R.id.wind_dec);

        velocityMax = 1;
        wind = 125;
        windTV.setText(String.valueOf(wind));
        windFrameIV.setRotation(wind + 180);


//        timerRace = findViewById(R.id.timer_race);
        textTime = findViewById(R.id.currentTime);
//        buttonExitToMain = findViewById(R.id.exit_to_main);

//        speedTV = findViewById(R.id.speed);
//        maxSpeedTV = findViewById(R.id.max_speed);
//        courseTV = findViewById(R.id.course);
//        countLocalChangedTV = findViewById(R.id.counter_loc_changed);

        thisActivity = this;

/////////// добавляем в пакет Instruments класс thread с мясом для отправки и обработки погодных запросов
/////////// при наличии геоданных направляем запрос
/////////// по получении запроса исходя из актуального времени выбираем текущий ветер, выводим в отдельный TV
/////////// добавляем ImageLayout, вставляем туда шкалу ветра, стрелку скорости
/////////// исходя из текущего курса поворачиваем шкалу ветра, из скорости стрелку скорости
/////////// курить по conctrateLayout, порядок прорисовки, наложение, и т.д....
        context = this;

        /** запускаем таймер */
        timerRunning(); // запускаем отсчет и обработку таймера

        initBroadcastListener(); // запускаем слушатель новых геоданных

        // обновляем положение вьюшек

//// потом перепишу слушатели кнопок в единый блок кода. Кнопок добавится много, в т.ч поля
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                velocityMax = 1;
                VMGMax = 0;
                calculateViewsPosition();
                updateMaxVelocityVMG();
            }
        });

        btnWindPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wind ++;
                calculateViewsPosition();
                updateMaxVelocityVMG();
                windTV.setText(String.valueOf(wind));
                windFrameIV.setRotation(180- wind);
            }
        });

        btnWindMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wind--;
                calculateViewsPosition();
                updateMaxVelocityVMG();
                windTV.setText(String.valueOf(wind));
                windFrameIV.setRotation(180 - wind);
            }
        });

        bearing = 0;
        windSB.setOnSeekBarChangeListener(this);
        bearingSB.setOnSeekBarChangeListener(this);
        velocitySB.setOnSeekBarChangeListener(this);

        velocity = 0;

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar == windSB) {
            wind = i;
            windFrameIV.setRotation(180 - wind);
            windTV.setText(String.valueOf(wind));
            calculateViewsPosition();
            updateMaxVelocityVMG();
        }
        if (seekBar == bearingSB) {
            bearing = i;
            bearingTV.setText(String.valueOf(bearing));
//            arrowVelocityIV.setRotation(bearing); // - 90 - первоначальная ориентация
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

    /** создаем и регистрируем слушатель геолокации */
    private void initBroadcastListener() {
        locationBroadcastReceiver = new BroadcastReceiver() { // создаем broadcastlistener
            @Override
            public void onReceive(Context context, Intent intent) { // обработка интента
                if (intent.hasExtra("location")) { // если в сообщении есть геолокация
                    Location location = (Location) intent.getExtras().get("location");
                    processorChangedLocation(location); // отдаем точку на обработку в процессор
                }
            }
        };
        locationIntentFilter = new IntentFilter(BROADCAST_ACTION); // прописываем интент фильтр для слушателя
        registerReceiver(locationBroadcastReceiver, locationIntentFilter); // регистрируем слушатель
    }

    /** обработка вновь полученных геолокации */
    private void processorChangedLocation (Location location) { // обработчик новой измененной позиции
        Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + " Activity race get new location ");
        double tempVelosity;
        if (location.hasSpeed()) {
            tempVelosity = (double) location.getSpeed()*3.6;
            velocity = (int) tempVelosity;
//            velosity = (velosity + (int) tempVelosity) / 2; // усреднение скорости
            bearing = courseAverage((int) location.getBearing()); // с учетом усреднения
        } else velosity = 0;
//        countLocationChanged++;
//        countLocalChangedTV.setText(String.valueOf(countLocationChanged));
//        speedTV.setText(String.valueOf(velosity));
//        if (velosity > maxSpeed) maxSpeed = velosity;
//        maxSpeedTV.setText(String.valueOf(maxSpeed));
//        courseTV.setText(String.valueOf(course));
//        textTime.setText(String.valueOf(location.getTime()));
        velocityTV.setText(String.valueOf(velocity));
        bearingTV.setText(String.valueOf(bearing));

        calculateViewsPosition();
        updateMaxVelocityVMG();
    }

    void calculateViewsPosition () {
        int centerScreenX, centerScreenY;
        double radiusSpeedMin = (windFrameIV.getWidth() / 2) - windFrameIV.getWidth()/3.7; // радиус при нулевой скорости
        int radiusMaxMinDiference = arrowDirectionIV.getHeight(); // максимальная разница между максимальными и минимальным значениями
        int priceGradeSpeed;
        priceGradeSpeed = radiusMaxMinDiference / velocityMax; // цена деления: сколько ед сикбара в ед радиуса
        int deltaBearing = bearing - wind ;

        // поворот рамы скорости
        frameVelocityIV.setRotation( deltaBearing + 180);

        // разворот стрелки скорости
        arrowVelocityIV.setRotation( deltaBearing + 225);

        // положение стрелки скорости
        centerScreenX = windFrameIV.getWidth()/2;
        centerScreenY = windFrameIV.getHeight()/2;
        double courseForWindRadians = Math.toRadians(90 + deltaBearing);
        double radiusVelocityArrow = radiusSpeedMin + velocity * priceGradeSpeed;
        if (radiusVelocityArrow > (radiusSpeedMin + radiusMaxMinDiference)) radiusVelocityArrow = radiusSpeedMin + radiusMaxMinDiference;
        arrowVelocityIV.setX((float) (centerScreenX - arrowVelocityIV.getWidth()/2 + radiusVelocityArrow * Math.cos(courseForWindRadians)));
        arrowVelocityIV.setY((float) (centerScreenY - arrowVelocityIV.getHeight()/2 + radiusVelocityArrow * Math.sin(courseForWindRadians)));
        velocityMadeGood = (int) (Math.sin(Math.toRadians(90 - Math.abs(deltaBearing))) * velocity);
        VMGTV.setText(String.valueOf(velocityMadeGood));

        // разворот стрелки направления
        arrowDirectionIV.setRotation( deltaBearing);
        // положение стрелки направления
        courseForWindRadians = Math.toRadians(90 + deltaBearing);
        arrowDirectionIV.setX((float) (centerScreenX - arrowDirectionIV.getWidth()/2 + ( radiusSpeedMin + arrowDirectionIV.getHeight()/2 + 40) * Math.cos(courseForWindRadians)));
        arrowDirectionIV.setY((float) (centerScreenY - arrowDirectionIV.getHeight()/2 + ( radiusSpeedMin + arrowDirectionIV.getHeight()/2 + 40) * Math.sin(courseForWindRadians)));

    }

    void updateMaxVelocityVMG () {
        if (velocity > velocityMax) velocityMax = velocity;
        maxVelocityTV.setText(String.valueOf(velocityMax));

        if (velocityMadeGood > VMGMax) VMGMax = velocityMadeGood;
        maxVMGTV.setText(String.valueOf(VMGMax));
    }
}
