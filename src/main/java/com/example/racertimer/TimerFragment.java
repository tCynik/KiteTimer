package com.example.racertimer;

import static com.example.racertimer.multimedia.Voiceover.SOUND_ASSET_EIGHT;
import static com.example.racertimer.multimedia.Voiceover.SOUND_ASSET_FIFTY;
import static com.example.racertimer.multimedia.Voiceover.SOUND_ASSET_FIVE;
import static com.example.racertimer.multimedia.Voiceover.SOUND_ASSET_FORTY;
import static com.example.racertimer.multimedia.Voiceover.SOUND_ASSET_FOUR;
import static com.example.racertimer.multimedia.Voiceover.SOUND_ASSET_NINE;
import static com.example.racertimer.multimedia.Voiceover.SOUND_ASSET_ONE;
import static com.example.racertimer.multimedia.Voiceover.SOUND_ASSET_ONE_MINUTE;
import static com.example.racertimer.multimedia.Voiceover.SOUND_ASSET_ONE_MINUTE_READY;
import static com.example.racertimer.multimedia.Voiceover.SOUND_ASSET_PAUSE;
import static com.example.racertimer.multimedia.Voiceover.SOUND_ASSET_SEVEN;
import static com.example.racertimer.multimedia.Voiceover.SOUND_ASSET_SIX;
import static com.example.racertimer.multimedia.Voiceover.SOUND_ASSET_START;
import static com.example.racertimer.multimedia.Voiceover.SOUND_ASSET_TEN;
import static com.example.racertimer.multimedia.Voiceover.SOUND_ASSET_THIRTY;
import static com.example.racertimer.multimedia.Voiceover.SOUND_ASSET_THREE;
import static com.example.racertimer.multimedia.Voiceover.SOUND_ASSET_TWENTY;
import static com.example.racertimer.multimedia.Voiceover.SOUND_ASSET_TWO;
import static com.example.racertimer.multimedia.Voiceover.SOUND_ASSET_TWO_MINUTE;
import static com.example.racertimer.multimedia.Voiceover.SOUND_ASSET_TWO_MINUTE_READY;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.racertimer.Instruments.StartingProcedureTimer;
import com.example.racertimer.Instruments.TimerStatusUpdater;
import com.example.racertimer.multimedia.Voiceover;

public class TimerFragment extends Fragment {
    private final static String PROJECT_LOG_TAG = "racer_timer";

    private StartingProcedureTimer startingProcedureTimer;
    private TimerStatusUpdater timerStatusUpdater;
    private CountDownTimer countDownTimer;
    private Voiceover voiceover;
    private Button btnInstantStartRace, btn5Minutes, btn3Minutes, btn2Minutes, btn1Minutes;
    private TextView timerResult;

    private String timerString = "00:00.00"; // переменная для вывода текущего секундомера чч:мм:сс.сот
    private int timerHour = 0; // переменная в часах
    private int timerMin = 0; // переменная счетчика в минутах
    private int timerSec = 0; // текущее значение таймера в сотых долей секунды

    private boolean timerPaused = true; // флаг: поставлен ли таймер на паузу

    public TimerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, null);

        voiceover = new Voiceover(getActivity());

        findTheViews(view);
        setClickListeners();
        /** обработка нажатий и отображения циферблата */


        initTimer();

        timerStatusUpdater = new TimerStatusUpdater() {
            @Override
            public void onTimerStatusUpdated(String timerStatus) {
                timerResult.setText(timerStatus);
            }
        };
        startingProcedureTimer = new StartingProcedureTimer(timerStatusUpdater);
        startingProcedureTimer.setTimerPeriod(5 * 60 * 1000);

        return view;
    }

    private void findTheViews(View view) {
        timerResult = view.findViewById(R.id.timer_fragment_tv);
        btnInstantStartRace = view.findViewById(R.id.instant_start_race);
        btn5Minutes = view.findViewById(R.id.set_5min);
        btn3Minutes = view.findViewById(R.id.set_3min);
        btn2Minutes = view.findViewById(R.id.set_2min);
        btn1Minutes = view.findViewById(R.id.set_1min);
    }

    private void setClickListeners() {
        timerResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerPaused) { // если счетчик остановлен,
                    timerPaused = false; // снимамем счетчик с паузы
                    Log.i("bugfix", " starting the timer ");
                    startingProcedureTimer.start();
                    //runTimerCounter(60); // запускаем счетчик на 1 минуту
                    //countDownTimer.start();
                    timerResult.setTextColor(Color.WHITE);
                } else { // если таймер идет, ставим на паузу
                    timerPaused = true;
                    startingProcedureTimer.stop();
                    //if (countDownTimer != null ) countDownTimer.cancel();
                    timerResult.setTextColor(Color.RED);
                    voiceover.playSingleTimerSound(SOUND_ASSET_PAUSE);
                }
            }
        });

        btnInstantStartRace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startingProcedureTimer.stop();
                //if (countDownTimer != null) countDownTimer.cancel();
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.StartTheRace();
            }
        });

        /** кнопки корректировки таймера */
        btn5Minutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startingProcedureTimer.setTimerPeriod(5 * 60 * 1000);

                //timerSec = 60 * 5;
                //String timerString2Print = calcTimeMinSec(timerSec); // получаем стринговое отобржение таймера
                //timerResult.setText(timerString2Print.toString()); // выводим значение на экран
            }
        });

        btn3Minutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startingProcedureTimer.setTimerPeriod(3 * 60 * 1000);


//                timerSec = 60 * 3;
//                String timerString2Print = calcTimeMinSec(timerSec); // получаем стринговое отобржение таймера
//                timerResult.setText(timerString2Print.toString()); // выводим значение на экран
            }
        });

        btn2Minutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startingProcedureTimer.setTimerPeriod(2 * 60 * 1000);


//                Log.i(PROJECT_LOG_TAG, " button 2 min pressed " );
//                timerSec = 60 * 2;
//                String timerString2Print = calcTimeMinSec(timerSec); // получаем стринговое отобржение таймера
//                timerResult.setText(timerString2Print.toString()); // выводим значение на экран
            }
        });

        btn1Minutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startingProcedureTimer.setTimerPeriod(1 * 60 * 1000);


//                Log.i(PROJECT_LOG_TAG, " button 1 min pressed " );
//                timerSec = 60 * 1;
//                String timerString2Print = calcTimeMinSec(timerSec); // получаем стринговое отобржение таймера
//                timerResult.setText(timerString2Print.toString()); // выводим значение на экран
            }
        });
    }

    public boolean isTimerRan() {return startingProcedureTimer.isTimerRan();}

    public void stopTheTimer() {
        startingProcedureTimer.stop();
    }

    private void initTimer() {
        //runTimerCounter(60); // запускаем счетчик на 1 минуту
        timerSec = 60 * 5; // устанавливаем таймер на 5 минут
        String timerString2Print = calcTimeMinSec(timerSec); // получаем стринговое отобржение таймера
        timerResult.setText(timerString2Print.toString()); // выводим значение на экран
        Toast.makeText(getActivity(), "tap the timer to START", Toast.LENGTH_LONG).show();
    }

    public void runTimerCounter(long timerSeconds) {
        countDownTimer = new CountDownTimer(timerSeconds * 1000, 1000) {
            @Override
            public void onTick(long l) { // действия во время отсчета
                if (timerSec > 0) onTimerTicked();
//                Log.i("Race", " Thread: "+Thread.currentThread().getName() + " timerSec = " +timerSec);

            }
            @Override
            protected void finalize() throws Throwable {
                super.finalize();
            }
            @Override
            public void onFinish() { // действия по окончании отсчета
                if (timerSec > 0) runTimerCounter(timerSec * 1000);
            }
        }.start();
    }

    /** обновление и обработка данных таймера*/
    private void onTimerTicked () { // таймер обновился
        String timerString2Print = "00:00";
//        if (! timerPaused){
            timerSec -- ;
            switch (timerSec) {
                case 130: voiceover.playSingleTimerSound(SOUND_ASSET_TWO_MINUTE_READY); break;
                case 120: voiceover.playSingleTimerSound(SOUND_ASSET_TWO_MINUTE); break;
                case 70: voiceover.playSingleTimerSound(SOUND_ASSET_ONE_MINUTE_READY); break;
                case 60: voiceover.playSingleTimerSound(SOUND_ASSET_ONE_MINUTE); break;
                case 50: voiceover.playSingleTimerSound(SOUND_ASSET_FIFTY); break;
                case 40: voiceover.playSingleTimerSound(SOUND_ASSET_FORTY); break;
                case 30: voiceover.playSingleTimerSound(SOUND_ASSET_THIRTY); break;
                case 20: voiceover.playSingleTimerSound(SOUND_ASSET_TWENTY); break;
                case 10: voiceover.playSingleTimerSound(SOUND_ASSET_TEN); break;
                case 9: voiceover.playSingleTimerSound(SOUND_ASSET_NINE); break;
                case 8: voiceover.playSingleTimerSound(SOUND_ASSET_EIGHT); break;
                case 7: voiceover.playSingleTimerSound(SOUND_ASSET_SEVEN); break;
                case 6: voiceover.playSingleTimerSound(SOUND_ASSET_SIX); break;
                case 5: voiceover.playSingleTimerSound(SOUND_ASSET_FIVE); break;
                case 4: voiceover.playSingleTimerSound(SOUND_ASSET_FOUR); break;
                case 3: voiceover.playSingleTimerSound(SOUND_ASSET_THREE); break;
                case 2: voiceover.playSingleTimerSound(SOUND_ASSET_TWO); break;
                case 1: voiceover.playSingleTimerSound(SOUND_ASSET_ONE); break;
                case 0: voiceover.playSingleTimerSound(SOUND_ASSET_START);
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.StartTheRace();
                    countDownTimer.cancel();
                    timerPaused = true;
                default: break;
           // }
        }

        timerString2Print = calcTimeMinSec(timerSec); // получаем стринговое отобржение таймера
        if (timerSec <= 0) timerString2Print = "GO!!!";
        timerResult.setText(timerString2Print.toString()); // выводим значение на экран
    }
    /** получение стринговых показаний для TextView */
    public String calcTimeMinSec (int timerSec) {
        String result=null;
        int sec;
        int min;
        min = timerSec / 60;
        result = min + ":00";
        sec = timerSec % 60;
        if (sec !=0) result = min + ":" + sec;
        if (sec < 10) result = min + ":0" + sec;
        return result;
    }
}